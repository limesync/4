package com.mobrealms.core.database;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.Achievement;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final MobRealmsCore plugin;
    private final File playerDataFolder;
    private final File locationsFile;

    private final File achievementsFolder;

    public DatabaseManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.locationsFile = new File(plugin.getDataFolder(), "locations.yml");
        this.achievementsFolder = new File(plugin.getDataFolder(), "achievements");
    }

    public boolean connect() {
        try {
            if (!playerDataFolder.exists()) {
                playerDataFolder.mkdirs();
            }

            if (!locationsFile.exists()) {
                locationsFile.createNewFile();
            }

            if (!achievementsFolder.exists()) {
                achievementsFolder.mkdirs();
            }

            plugin.getLogger().info("Successfully initialized file-based storage!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize storage: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        plugin.getLogger().info("Storage closed.");
    }

    public CompletableFuture<PlayerData> loadPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File playerFile = new File(playerDataFolder, uuid.toString() + ".yml");

                if (!playerFile.exists()) {
                    return new PlayerData(uuid);
                }

                YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

                int level = config.getInt("level", 1);
                long xp = config.getLong("xp", 0);
                long coins = config.getLong("coins", 0);
                String playerClass = config.getString("class", null);
                String currentRealm = config.getString("current_realm", null);
                long lastKitClaim = config.getLong("last_kit_claim", 0);

                return new PlayerData(uuid, level, xp, coins, playerClass, currentRealm, lastKitClaim);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load player data for " + uuid + ": " + e.getMessage());
                return new PlayerData(uuid);
            }
        });
    }

    public CompletableFuture<Void> savePlayerData(PlayerData data) {
        return CompletableFuture.runAsync(() -> {
            try {
                File playerFile = new File(playerDataFolder, data.getUUID().toString() + ".yml");

                YamlConfiguration config = new YamlConfiguration();
                config.set("uuid", data.getUUID().toString());
                config.set("level", data.getLevel());
                config.set("xp", data.getXP());
                config.set("coins", data.getCoins());
                config.set("class", data.getPlayerClass());
                config.set("current_realm", data.getCurrentRealm());
                config.set("last_kit_claim", data.getLastKitClaim());

                config.save(playerFile);
                data.setDirty(false);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save player data for " + data.getUUID() + ": " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> saveLocation(String type, String realmName, Location location) {
        return CompletableFuture.runAsync(() -> {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(locationsFile);

                String path = type;
                if (realmName != null) {
                    path = type + "." + realmName;
                }

                config.set(path + ".world", location.getWorld().getName());
                config.set(path + ".x", location.getX());
                config.set(path + ".y", location.getY());
                config.set(path + ".z", location.getZ());
                config.set(path + ".yaw", location.getYaw());
                config.set(path + ".pitch", location.getPitch());

                config.save(locationsFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save location: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Location> loadLocation(String type, String realmName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(locationsFile);

                String path = type;
                if (realmName != null) {
                    path = type + "." + realmName;
                }

                if (!config.contains(path)) {
                    return null;
                }

                String worldName = config.getString(path + ".world");
                double x = config.getDouble(path + ".x");
                double y = config.getDouble(path + ".y");
                double z = config.getDouble(path + ".z");
                float yaw = (float) config.getDouble(path + ".yaw");
                float pitch = (float) config.getDouble(path + ".pitch");

                return new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load location: " + e.getMessage());
                return null;
            }
        });
    }

    public CompletableFuture<List<Achievement>> loadAchievements(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<Achievement> achievements = new ArrayList<>();
            try {
                File achievementFile = new File(achievementsFolder, uuid.toString() + ".yml");

                if (!achievementFile.exists()) {
                    return achievements;
                }

                YamlConfiguration config = YamlConfiguration.loadConfiguration(achievementFile);

                for (String key : config.getKeys(false)) {
                    String name = config.getString(key + ".name");
                    String description = config.getString(key + ".description");
                    String iconStr = config.getString(key + ".icon");
                    int order = config.getInt(key + ".order");
                    boolean unlocked = config.getBoolean(key + ".unlocked", false);
                    String unlockedAtStr = config.getString(key + ".unlocked_at");

                    Achievement.Material icon = Achievement.Material.valueOf(iconStr);
                    Achievement achievement = new Achievement(key, name, description, icon, order);
                    achievement.setUnlocked(unlocked);

                    if (unlockedAtStr != null && !unlockedAtStr.isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                        achievement.setUnlockedAt(LocalDateTime.parse(unlockedAtStr, formatter));
                    }

                    achievements.add(achievement);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load achievements for " + uuid + ": " + e.getMessage());
            }

            return achievements;
        });
    }

    public CompletableFuture<Void> saveAchievement(UUID uuid, Achievement achievement) {
        return CompletableFuture.runAsync(() -> {
            try {
                File achievementFile = new File(achievementsFolder, uuid.toString() + ".yml");
                YamlConfiguration config;

                if (achievementFile.exists()) {
                    config = YamlConfiguration.loadConfiguration(achievementFile);
                } else {
                    config = new YamlConfiguration();
                }

                String id = achievement.getId();
                config.set(id + ".name", achievement.getName());
                config.set(id + ".description", achievement.getDescription());
                config.set(id + ".icon", achievement.getIcon().name());
                config.set(id + ".order", achievement.getOrder());
                config.set(id + ".unlocked", achievement.isUnlocked());

                if (achievement.getUnlockedAt() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    config.set(id + ".unlocked_at", achievement.getUnlockedAt().format(formatter));
                }

                config.save(achievementFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save achievement for " + uuid + ": " + e.getMessage());
            }
        });
    }
}
