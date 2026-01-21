package com.mobrealms.core.models;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class MobSpawner {

    private UUID id;
    private String world;
    private int x;
    private int y;
    private int z;
    private String mobId;
    private int spawnInterval;
    private int maxNearbyMobs;
    private int spawnRadius;
    private boolean active;
    private long lastSpawn;

    public MobSpawner(UUID id, String world, int x, int y, int z, String mobId, int spawnInterval, int maxNearbyMobs, int spawnRadius, boolean active, long lastSpawn) {
        this.id = id;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mobId = mobId;
        this.spawnInterval = spawnInterval;
        this.maxNearbyMobs = maxNearbyMobs;
        this.spawnRadius = spawnRadius;
        this.active = active;
        this.lastSpawn = lastSpawn;
    }

    public MobSpawner(String world, int x, int y, int z, String mobId) {
        this(UUID.randomUUID(), world, x, y, z, mobId, 30, 3, 16, true, 0);
    }

    public UUID getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getMobId() {
        return mobId;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public int getMaxNearbyMobs() {
        return maxNearbyMobs;
    }

    public int getSpawnRadius() {
        return spawnRadius;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getLastSpawn() {
        return lastSpawn;
    }

    public void setLastSpawn(long lastSpawn) {
        this.lastSpawn = lastSpawn;
    }

    public Location getLocation(World world) {
        return new Location(world, x, y, z);
    }

    public boolean canSpawn() {
        if (!active) return false;
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastSpawn) >= (spawnInterval * 1000L);
    }
}
