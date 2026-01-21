package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.Quest;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class QuestManager {
    private final MobRealmsCore plugin;
    private final File questsFolder;
    private final Map<UUID, Map<String, Quest>> playerQuests;

    public QuestManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.questsFolder = new File(plugin.getDataFolder(), "quests");
        this.playerQuests = new HashMap<>();

        if (!questsFolder.exists()) {
            questsFolder.mkdirs();
        }
    }

    public void loadPlayerQuests(UUID uuid) {
        File file = new File(questsFolder, uuid.toString() + ".yml");
        Map<String, Quest> quests = new HashMap<>();

        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (Quest defaultQuest : getDefaultQuests()) {
                String id = defaultQuest.getId();
                int progress = config.getInt(id + ".progress", 0);
                boolean completed = config.getBoolean(id + ".completed", false);
                boolean claimed = config.getBoolean(id + ".claimed", false);

                Quest quest = new Quest(
                        defaultQuest.getId(),
                        defaultQuest.getName(),
                        defaultQuest.getDescription(),
                        defaultQuest.getType(),
                        defaultQuest.getTargetAmount(),
                        defaultQuest.getCoinReward(),
                        defaultQuest.getXpReward(),
                        defaultQuest.getDifficulty()
                );
                quest.setProgress(progress);
                quest.setClaimed(claimed);

                quests.put(id, quest);
            }
        } else {
            for (Quest quest : getDefaultQuests()) {
                quests.put(quest.getId(), quest);
            }
        }

        playerQuests.put(uuid, quests);
    }

    public void savePlayerQuests(UUID uuid) {
        Map<String, Quest> quests = playerQuests.get(uuid);
        if (quests == null) return;

        File file = new File(questsFolder, uuid.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        for (Quest quest : quests.values()) {
            String id = quest.getId();
            config.set(id + ".progress", quest.getProgress());
            config.set(id + ".completed", quest.isCompleted());
            config.set(id + ".claimed", quest.isClaimed());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save quests for " + uuid + ": " + e.getMessage());
        }
    }

    public Map<String, Quest> getPlayerQuests(UUID uuid) {
        return playerQuests.getOrDefault(uuid, new HashMap<>());
    }

    public void handleKill(Player player) {
        updateQuestProgress(player, Quest.QuestType.KILL_MOBS, 1);
    }

    public void handleBossKill(Player player) {
        updateQuestProgress(player, Quest.QuestType.KILL_BOSS, 1);
    }

    public void handleCoinsEarned(Player player, long coins) {
        updateQuestProgress(player, Quest.QuestType.EARN_COINS, (int) coins);
    }

    public void handleKillstreak(Player player, int streak) {
        Map<String, Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return;

        for (Quest quest : quests.values()) {
            if (quest.getType() == Quest.QuestType.KILLSTREAK && !quest.isClaimed()) {
                boolean wasCompleted = quest.isCompleted();
                if (streak >= quest.getTargetAmount()) {
                    quest.setProgress(quest.getTargetAmount());
                }

                if (quest.isCompleted() && !wasCompleted) {
                    player.sendMessage("§a§l✓ Quest Completed: §f" + quest.getName());
                    player.sendMessage("§7Claim your reward in §e/quests§7!");
                    savePlayerQuests(player.getUniqueId());
                }
            }
        }
    }

    public void handleLevelUp(Player player, int level) {
        Map<String, Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return;

        for (Quest quest : quests.values()) {
            if (quest.getType() == Quest.QuestType.REACH_LEVEL && !quest.isClaimed()) {
                boolean wasCompleted = quest.isCompleted();
                if (level >= quest.getTargetAmount()) {
                    quest.setProgress(quest.getTargetAmount());
                }

                if (quest.isCompleted() && !wasCompleted) {
                    player.sendMessage("§a§l✓ Quest Completed: §f" + quest.getName());
                    player.sendMessage("§7Claim your reward in §e/quests§7!");
                    savePlayerQuests(player.getUniqueId());
                }
            }
        }
    }

    public void handleSpawnerPlaced(Player player) {
        updateQuestProgress(player, Quest.QuestType.PLACE_SPAWNERS, 1);
    }

    private void updateQuestProgress(Player player, Quest.QuestType type, int amount) {
        Map<String, Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return;

        boolean questCompleted = false;
        for (Quest quest : quests.values()) {
            if (quest.getType() == type && !quest.isClaimed()) {
                int oldProgress = quest.getProgress();
                boolean wasCompleted = quest.isCompleted();

                quest.addProgress(amount);

                if (quest.isCompleted() && !wasCompleted) {
                    player.sendMessage("§a§l✓ Quest Completed: §f" + quest.getName());
                    player.sendMessage("§7Claim your reward in §e/quests§7!");
                    questCompleted = true;
                }
            }
        }

        if (questCompleted) {
            savePlayerQuests(player.getUniqueId());
        }
    }

    public boolean claimQuest(Player player, String questId) {
        Map<String, Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return false;

        Quest quest = quests.get(questId);
        if (quest == null || !quest.isCompleted() || quest.isClaimed()) {
            return false;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return false;

        quest.setClaimed(true);
        data.addCoins(quest.getCoinReward());
        plugin.getLevelManager().addXP(player, quest.getXpReward());

        player.sendMessage("§a§l✓ §aQuest Claimed!");
        player.sendMessage("§7Rewards: §6+" + quest.getCoinReward() + " coins §7| §b+" + quest.getXpReward() + " XP");

        savePlayerQuests(player.getUniqueId());
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

    private List<Quest> getDefaultQuests() {
        List<Quest> quests = new ArrayList<>();

        quests.add(new Quest("first_blood", "§aFirst Blood", "Kill your first mob",
                Quest.QuestType.KILL_MOBS, 1, 100, 50, Quest.QuestDifficulty.EASY));

        quests.add(new Quest("mob_slayer_i", "§aMob Slayer I", "Kill 50 mobs",
                Quest.QuestType.KILL_MOBS, 50, 500, 250, Quest.QuestDifficulty.EASY));

        quests.add(new Quest("mob_slayer_ii", "§eMob Slayer II", "Kill 250 mobs",
                Quest.QuestType.KILL_MOBS, 250, 2500, 1500, Quest.QuestDifficulty.MEDIUM));

        quests.add(new Quest("mob_slayer_iii", "§6Mob Slayer III", "Kill 1000 mobs",
                Quest.QuestType.KILL_MOBS, 1000, 15000, 10000, Quest.QuestDifficulty.HARD));

        quests.add(new Quest("mob_slayer_iv", "§cMob Slayer IV", "Kill 5000 mobs",
                Quest.QuestType.KILL_MOBS, 5000, 100000, 75000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("boss_hunter_i", "§eBoss Hunter I", "Kill 10 bosses",
                Quest.QuestType.KILL_BOSS, 10, 5000, 3000, Quest.QuestDifficulty.MEDIUM));

        quests.add(new Quest("boss_hunter_ii", "§6Boss Hunter II", "Kill 50 bosses",
                Quest.QuestType.KILL_BOSS, 50, 25000, 20000, Quest.QuestDifficulty.HARD));

        quests.add(new Quest("boss_hunter_iii", "§cBoss Hunter III", "Kill 250 bosses",
                Quest.QuestType.KILL_BOSS, 250, 150000, 125000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("rich_i", "§eWealth Builder I", "Earn 10,000 coins",
                Quest.QuestType.EARN_COINS, 10000, 2500, 1500, Quest.QuestDifficulty.MEDIUM));

        quests.add(new Quest("rich_ii", "§6Wealth Builder II", "Earn 100,000 coins",
                Quest.QuestType.EARN_COINS, 100000, 25000, 15000, Quest.QuestDifficulty.HARD));

        quests.add(new Quest("rich_iii", "§cWealth Builder III", "Earn 1,000,000 coins",
                Quest.QuestType.EARN_COINS, 1000000, 250000, 200000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("leveling_i", "§aLevel Up I", "Reach level 10",
                Quest.QuestType.REACH_LEVEL, 10, 1000, 500, Quest.QuestDifficulty.EASY));

        quests.add(new Quest("leveling_ii", "§eLevel Up II", "Reach level 25",
                Quest.QuestType.REACH_LEVEL, 25, 5000, 3000, Quest.QuestDifficulty.MEDIUM));

        quests.add(new Quest("leveling_iii", "§6Level Up III", "Reach level 50",
                Quest.QuestType.REACH_LEVEL, 50, 25000, 15000, Quest.QuestDifficulty.HARD));

        quests.add(new Quest("streak_master_i", "§eStreak Master I", "Get a 10 killstreak",
                Quest.QuestType.KILLSTREAK, 10, 2500, 1500, Quest.QuestDifficulty.MEDIUM));

        quests.add(new Quest("streak_master_ii", "§6Streak Master II", "Get a 25 killstreak",
                Quest.QuestType.KILLSTREAK, 25, 10000, 7500, Quest.QuestDifficulty.HARD));

        quests.add(new Quest("streak_master_iii", "§cStreak Master III", "Get a 50 killstreak",
                Quest.QuestType.KILLSTREAK, 50, 50000, 40000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("spawner_expert_i", "§eSpawner Expert I", "Place 5 spawners",
                Quest.QuestType.PLACE_SPAWNERS, 5, 2000, 1000, Quest.QuestDifficulty.MEDIUM));

        quests.add(new Quest("spawner_expert_ii", "§6Spawner Expert II", "Place 25 spawners",
                Quest.QuestType.PLACE_SPAWNERS, 25, 15000, 10000, Quest.QuestDifficulty.HARD));

        quests.add(new Quest("spawner_expert_iii", "§cSpawner Expert III", "Place 100 spawners",
                Quest.QuestType.PLACE_SPAWNERS, 100, 75000, 50000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("mob_slayer_v", "§4§lMob Slayer V", "Kill 25,000 mobs",
                Quest.QuestType.KILL_MOBS, 25000, 500000, 400000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("boss_hunter_iv", "§4§lBoss Hunter IV", "Kill 1000 bosses",
                Quest.QuestType.KILL_BOSS, 1000, 750000, 650000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("leveling_iv", "§cLevel Up IV", "Reach level 100",
                Quest.QuestType.REACH_LEVEL, 100, 100000, 75000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("leveling_v", "§4§lLevel Up V", "Reach level 250",
                Quest.QuestType.REACH_LEVEL, 250, 500000, 400000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("leveling_vi", "§5§lLevel Up VI", "Reach level 500",
                Quest.QuestType.REACH_LEVEL, 500, 2500000, 2000000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("leveling_vii", "§d§lLevel Up VII", "Reach level 1000",
                Quest.QuestType.REACH_LEVEL, 1000, 10000000, 9000000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("streak_master_iv", "§4§lStreak Master IV", "Get a 100 killstreak",
                Quest.QuestType.KILLSTREAK, 100, 250000, 200000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("rich_iv", "§4§lWealth Builder IV", "Earn 10,000,000 coins",
                Quest.QuestType.EARN_COINS, 10000000, 2500000, 2000000, Quest.QuestDifficulty.EXTREME));

        quests.add(new Quest("beginner", "§aWelcome!", "Kill 10 mobs to get started",
                Quest.QuestType.KILL_MOBS, 10, 250, 100, Quest.QuestDifficulty.EASY));

        quests.add(new Quest("level_5", "§aGetting Started", "Reach level 5",
                Quest.QuestType.REACH_LEVEL, 5, 500, 250, Quest.QuestDifficulty.EASY));

        quests.add(new Quest("streak_5", "§aFirst Streak", "Get a 5 killstreak",
                Quest.QuestType.KILLSTREAK, 5, 1000, 500, Quest.QuestDifficulty.EASY));

        return quests;
    }
}
