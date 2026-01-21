package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.Achievement;
import org.bukkit.entity.Player;

import java.util.*;

public class AchievementManager {
    private final MobRealmsCore plugin;
    private final Map<UUID, Map<String, Achievement>> playerAchievements;

    public AchievementManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.playerAchievements = new HashMap<>();
    }

    public void loadPlayerAchievements(UUID uuid) {
        plugin.getDatabaseManager().loadAchievements(uuid).thenAccept(achievements -> {
            Map<String, Achievement> achievementMap = new HashMap<>();

            for (Achievement defaultAch : getDefaultAchievements()) {
                Achievement playerAch = achievements.stream()
                        .filter(a -> a.getId().equals(defaultAch.getId()))
                        .findFirst()
                        .orElse(defaultAch);

                achievementMap.put(playerAch.getId(), playerAch);
            }

            playerAchievements.put(uuid, achievementMap);
        });
    }

    public Map<String, Achievement> getPlayerAchievements(UUID uuid) {
        return playerAchievements.getOrDefault(uuid, new HashMap<>());
    }

    public List<Achievement> getPlayerAchievementsSorted(UUID uuid) {
        List<Achievement> achievements = new ArrayList<>(getPlayerAchievements(uuid).values());
        achievements.sort(Comparator.comparingInt(Achievement::getOrder));
        return achievements;
    }

    public void unlockAchievement(Player player, String achievementId) {
        Map<String, Achievement> achievements = playerAchievements.get(player.getUniqueId());
        if (achievements == null) return;

        Achievement achievement = achievements.get(achievementId);
        if (achievement == null || achievement.isUnlocked()) return;

        achievement.setUnlocked(true);
        plugin.getDatabaseManager().saveAchievement(player.getUniqueId(), achievement);

        player.sendMessage("§6§l§m                                              ");
        player.sendMessage("");
        player.sendMessage("          §e§lACHIEVEMENT UNLOCKED!");
        player.sendMessage("");
        player.sendMessage("          " + achievement.getName());
        player.sendMessage("          §7" + achievement.getDescription());
        player.sendMessage("");
        player.sendMessage("§6§l§m                                              ");

        player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    public void saveAll() {
        for (UUID uuid : playerAchievements.keySet()) {
            Map<String, Achievement> achievements = playerAchievements.get(uuid);
            if (achievements != null) {
                for (Achievement achievement : achievements.values()) {
                    if (achievement.isUnlocked()) {
                        plugin.getDatabaseManager().saveAchievement(uuid, achievement);
                    }
                }
            }
        }
    }

    public void unloadPlayerAchievements(UUID uuid) {
        playerAchievements.remove(uuid);
    }

    private List<Achievement> getDefaultAchievements() {
        List<Achievement> achievements = new ArrayList<>();

        achievements.add(new Achievement("baldric_graduate", "§6Baldric's Graduate",
                "Complete all of Baldric's beginner quests", Achievement.Material.BOOK, 1));

        achievements.add(new Achievement("first_class", "§aFirst Class",
                "Select your first class", Achievement.Material.IRON_SWORD, 2));

        achievements.add(new Achievement("level_10", "§eTen and Counting",
                "Reach level 10", Achievement.Material.GOLD_INGOT, 3));

        achievements.add(new Achievement("level_50", "§6Halfway There",
                "Reach level 50", Achievement.Material.DIAMOND, 4));

        achievements.add(new Achievement("level_100", "§bCentury Club",
                "Reach level 100", Achievement.Material.EMERALD, 5));

        achievements.add(new Achievement("mob_hunter_100", "§aMob Hunter",
                "Kill 100 mobs", Achievement.Material.IRON_SWORD, 6));

        achievements.add(new Achievement("mob_hunter_1000", "§6Mob Slayer",
                "Kill 1,000 mobs", Achievement.Material.DIAMOND, 7));

        achievements.add(new Achievement("millionaire", "§6Millionaire",
                "Earn 1,000,000 coins", Achievement.Material.GOLD_INGOT, 8));

        return achievements;
    }
}
