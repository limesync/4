package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.MobSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpawnerManager {

    private final MobRealmsCore plugin;
    private final Map<UUID, MobSpawner> spawners;
    private final File spawnersFile;
    private BukkitTask spawnerTask;

    public SpawnerManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.spawners = new HashMap<>();
        this.spawnersFile = new File(plugin.getDataFolder(), "spawners.yml");

        if (!spawnersFile.exists()) {
            try {
                spawnersFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create spawners.yml: " + e.getMessage());
            }
        }

        loadAllSpawners();
        startSpawnerTask();
    }

    public void loadAllSpawners() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(spawnersFile);

            if (!config.contains("spawners")) {
                return;
            }

            for (String key : config.getConfigurationSection("spawners").getKeys(false)) {
                String path = "spawners." + key;

                UUID id = UUID.fromString(key);
                String world = config.getString(path + ".world");
                int x = config.getInt(path + ".x");
                int y = config.getInt(path + ".y");
                int z = config.getInt(path + ".z");
                String mobId = config.getString(path + ".mob_id");
                int spawnInterval = config.getInt(path + ".spawn_interval", 30);
                int maxNearbyMobs = config.getInt(path + ".max_nearby_mobs", 3);
                int spawnRadius = config.getInt(path + ".spawn_radius", 16);
                boolean active = config.getBoolean(path + ".active", true);
                long lastSpawn = config.getLong(path + ".last_spawn", 0);

                MobSpawner spawner = new MobSpawner(id, world, x, y, z, mobId, spawnInterval, maxNearbyMobs, spawnRadius, active, lastSpawn);
                spawners.put(id, spawner);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load spawners: " + e.getMessage());
        }
    }

    public void saveAllSpawners() {
        try {
            YamlConfiguration config = new YamlConfiguration();

            for (Map.Entry<UUID, MobSpawner> entry : spawners.entrySet()) {
                MobSpawner spawner = entry.getValue();
                String path = "spawners." + entry.getKey().toString();

                config.set(path + ".world", spawner.getWorld());
                config.set(path + ".x", spawner.getX());
                config.set(path + ".y", spawner.getY());
                config.set(path + ".z", spawner.getZ());
                config.set(path + ".mob_id", spawner.getMobId());
                config.set(path + ".spawn_interval", spawner.getSpawnInterval());
                config.set(path + ".max_nearby_mobs", spawner.getMaxNearbyMobs());
                config.set(path + ".spawn_radius", spawner.getSpawnRadius());
                config.set(path + ".active", spawner.isActive());
                config.set(path + ".last_spawn", spawner.getLastSpawn());
            }

            config.save(spawnersFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save spawners: " + e.getMessage());
        }
    }

    public void addSpawner(MobSpawner spawner) {
        spawners.put(spawner.getId(), spawner);
        saveAllSpawners();
    }

    public void removeSpawner(Location location) {
        MobSpawner spawner = getSpawnerAt(location);
        if (spawner == null) return;

        spawners.remove(spawner.getId());
        saveAllSpawners();
    }

    public void removeSpawner(UUID id) {
        spawners.remove(id);
        saveAllSpawners();
    }

    public MobSpawner getSpawnerByShortId(String shortId) {
        for (MobSpawner spawner : spawners.values()) {
            if (spawner.getId().toString().startsWith(shortId)) {
                return spawner;
            }
        }
        return null;
    }

    public MobSpawner getSpawnerAt(Location location) {
        return spawners.values().stream()
                .filter(s -> s.getWorld().equals(location.getWorld().getName())
                        && s.getX() == location.getBlockX()
                        && s.getY() == location.getBlockY()
                        && s.getZ() == location.getBlockZ())
                .findFirst()
                .orElse(null);
    }

    public void updateLastSpawn(MobSpawner spawner) {
        spawner.setLastSpawn(System.currentTimeMillis());
        saveAllSpawners();
    }

    private void startSpawnerTask() {
        spawnerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (MobSpawner spawner : new ArrayList<>(spawners.values())) {
                if (!spawner.isActive()) continue;
                if (!spawner.canSpawn()) continue;

                World world = Bukkit.getWorld(spawner.getWorld());
                if (world == null) {
                    plugin.getLogger().warning("[Spawner] World '" + spawner.getWorld() + "' not found for spawner!");
                    continue;
                }

                Location location = spawner.getLocation(world);

                if (!world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                    continue;
                }

                int nearbyMobs = (int) world.getNearbyEntities(location, spawner.getSpawnRadius(), spawner.getSpawnRadius(), spawner.getSpawnRadius())
                        .stream()
                        .filter(e -> plugin.getCustomMobManager().isCustomMob(e))
                        .count();

                if (nearbyMobs < spawner.getMaxNearbyMobs()) {
                    Location spawnLoc = getRandomSpawnLocation(location, spawner.getSpawnRadius());
                    if (spawnLoc != null && spawnLoc.getBlock().getType().isAir() && spawnLoc.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) {
                        Entity entity = plugin.getCustomMobManager().spawnCustomMob(spawner.getMobId(), spawnLoc);

                        if (entity != null) {
                            updateLastSpawn(spawner);
                        }
                    }
                }
            }
        }, 20L, 20L);
    }

    private Location getRandomSpawnLocation(Location center, int radius) {
        Random random = new Random();
        for (int attempts = 0; attempts < 10; attempts++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * radius;
            int x = (int) (center.getX() + distance * Math.cos(angle));
            int z = (int) (center.getZ() + distance * Math.sin(angle));
            int y = center.getWorld().getHighestBlockYAt(x, z) + 1;

            Location spawnLoc = new Location(center.getWorld(), x + 0.5, y, z + 0.5);
            if (spawnLoc.getBlock().getType().isAir() && spawnLoc.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) {
                return spawnLoc;
            }
        }
        return center.clone().add(0.5, 1, 0.5);
    }

    public void stop() {
        if (spawnerTask != null) {
            spawnerTask.cancel();
        }
        saveAllSpawners();
    }

    public Collection<MobSpawner> getAllSpawners() {
        return new ArrayList<>(spawners.values());
    }
}
