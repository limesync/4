package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.BaldricQuest;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BaldricQuestManager {
    private final MobRealmsCore plugin;
    private final File questsFolder;
    private final Map<UUID, Map<String, BaldricQuest>> playerQuests;

    public BaldricQuestManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.questsFolder = new File(plugin.getDataFolder(), "baldric_quests");
        this.playerQuests = new HashMap<>();

        if (!questsFolder.exists()) {
            questsFolder.mkdirs();
        }
    }

    public void loadPlayerQuests(UUID uuid) {
        File file = new File(questsFolder, uuid.toString() + ".yml");
        Map<String, BaldricQuest> quests = new HashMap<>();

        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (BaldricQuest defaultQuest : getDefaultQuests()) {
                String id = defaultQuest.getId();
                int progress = config.getInt(id + ".progress", 0);
                boolean completed = config.getBoolean(id + ".completed", false);
                boolean claimed = config.getBoolean(id + ".claimed", false);

                BaldricQuest quest = new BaldricQuest(
                        defaultQuest.getId(),
                        defaultQuest.getName(),
                        defaultQuest.getDescription(),
                        defaultQuest.getType(),
                        defaultQuest.getTargetAmount(),
                        defaultQuest.getCoinReward(),
                        defaultQuest.getXpReward(),
                        defaultQuest.getOrder()
                );
                quest.setProgress(progress);
                quest.setClaimed(claimed);

                quests.put(id, quest);
            }
        } else {
            for (BaldricQuest quest : getDefaultQuests()) {
                quests.put(quest.getId(), quest);
            }
        }

        playerQuests.put(uuid, quests);
    }

    public void savePlayerQuests(UUID uuid) {
        Map<String, BaldricQuest> quests = playerQuests.get(uuid);
        if (quests == null) return;

        File file = new File(questsFolder, uuid.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        for (BaldricQuest quest : quests.values()) {
            String id = quest.getId();
            config.set(id + ".progress", quest.getProgress());
            config.set(id + ".completed", quest.isCompleted());
            config.set(id + ".claimed", quest.isClaimed());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save Baldric quests for " + uuid + ": " + e.getMessage());
        }
    }

    public Map<String, BaldricQuest> getPlayerQuests(UUID uuid) {
        return playerQuests.getOrDefault(uuid, new HashMap<>());
    }

    public List<BaldricQuest> getPlayerQuestsSorted(UUID uuid) {
        List<BaldricQuest> quests = new ArrayList<>(getPlayerQuests(uuid).values());
        quests.sort(Comparator.comparingInt(BaldricQuest::getOrder));
        return quests;
    }

    public boolean hasCompletedAllQuests(UUID uuid) {
        Map<String, BaldricQuest> quests = playerQuests.get(uuid);
        if (quests == null || quests.isEmpty()) return false;

        for (BaldricQuest quest : quests.values()) {
            if (!quest.isClaimed()) {
                return false;
            }
        }
        return true;
    }

    public void handleKill(Player player) {
        updateQuestProgress(player, BaldricQuest.QuestType.KILL_MOBS, 1);
    }

    public void handleCoinsEarned(Player player, long coins) {
        updateQuestProgress(player, BaldricQuest.QuestType.EARN_COINS, (int) coins);
    }

    public void handleLevelUp(Player player, int level) {
        Map<String, BaldricQuest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return;

        for (BaldricQuest quest : quests.values()) {
            if (quest.getType() == BaldricQuest.QuestType.REACH_LEVEL && !quest.isClaimed()) {
                boolean wasCompleted = quest.isCompleted();
                if (level >= quest.getTargetAmount()) {
                    quest.setProgress(quest.getTargetAmount());
                }

                if (quest.isCompleted() && !wasCompleted) {
                    player.sendMessage("§a§l✓ Baldric Quest Completed: §f" + quest.getName());
                    player.sendMessage("§7Talk to §eBaldric §7to claim your reward!");
                    savePlayerQuests(player.getUniqueId());
                    checkAllQuestsCompleted(player);
                }
            }
        }
    }

    private void updateQuestProgress(Player player, BaldricQuest.QuestType type, int amount) {
        Map<String, BaldricQuest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return;

        boolean questCompleted = false;
        for (BaldricQuest quest : quests.values()) {
            if (quest.getType() == type && !quest.isClaimed()) {
                int oldProgress = quest.getProgress();
                boolean wasCompleted = quest.isCompleted();

                quest.addProgress(amount);

                if (quest.isCompleted() && !wasCompleted) {
                    player.sendMessage("§a§l✓ Baldric Quest Completed: §f" + quest.getName());
                    player.sendMessage("§7Talk to §eBaldric §7to claim your reward!");
                    questCompleted = true;
                }
            }
        }

        if (questCompleted) {
            savePlayerQuests(player.getUniqueId());
            checkAllQuestsCompleted(player);
        }
    }

    private void checkAllQuestsCompleted(Player player) {
        if (hasCompletedAllQuests(player.getUniqueId())) {
            plugin.getAchievementManager().unlockAchievement(player, "baldric_graduate");
        }
    }

    public boolean claimQuest(Player player, String questId) {
        Map<String, BaldricQuest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return false;

        BaldricQuest quest = quests.get(questId);
        if (quest == null || !quest.isCompleted() || quest.isClaimed()) {
            return false;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return false;

        quest.setClaimed(true);
        data.addCoins(quest.getCoinReward());
        plugin.getLevelManager().addXP(player, quest.getXpReward());

        player.sendMessage("§a§l✓ §aBaldric Quest Claimed!");
        player.sendMessage("§7Rewards: §6+" + quest.getCoinReward() + " coins §7| §b+" + quest.getXpReward() + " XP");

        savePlayerQuests(player.getUniqueId());
        checkAllQuestsCompleted(player);
        return true;
    }

    public void saveAll() {
        for (UUID uuid : playerQuests.keySet()) {
            savePlayerQuests(uuid);
        }
    }

    public void unloadPlayerQuests(UUID uuid) {
        savePlayerQuests(uuid);
        playerQuests.remove(uuid);
    }

    private List<BaldricQuest> getDefaultQuests() {
        List<BaldricQuest> quests = new ArrayList<>();

        quests.add(new BaldricQuest("baldric_1", "§aWelcome, Adventurer!", "Talk to Baldric to begin",
                BaldricQuest.QuestType.TALK_TO_NPC, 1, 50, 25, 1));

        quests.add(new BaldricQuest("baldric_2", "§aFirst Steps", "Kill 5 mobs",
                BaldricQuest.QuestType.KILL_MOBS, 5, 100, 50, 2));

        quests.add(new BaldricQuest("baldric_3", "§aGathering Strength", "Kill 15 mobs",
                BaldricQuest.QuestType.KILL_MOBS, 15, 200, 100, 3));

        quests.add(new BaldricQuest("baldric_4", "§aEarning Your Keep", "Earn 500 coins",
                BaldricQuest.QuestType.EARN_COINS, 500, 250, 150, 4));

        quests.add(new BaldricQuest("baldric_5", "§aRising Power", "Reach level 3",
                BaldricQuest.QuestType.REACH_LEVEL, 3, 300, 200, 5));

        quests.add(new BaldricQuest("baldric_6", "§eProven Warrior", "Kill 50 mobs",
                BaldricQuest.QuestType.KILL_MOBS, 50, 500, 350, 6));

        quests.add(new BaldricQuest("baldric_7", "§eWealth Seeker", "Earn 2,000 coins",
                BaldricQuest.QuestType.EARN_COINS, 2000, 600, 400, 7));

        quests.add(new BaldricQuest("baldric_8", "§6Baldric's Graduate", "Reach level 5",
                BaldricQuest.QuestType.REACH_LEVEL, 5, 1000, 750, 8));

        return quests;
    }
}
