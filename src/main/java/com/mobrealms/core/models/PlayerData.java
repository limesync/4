package com.mobrealms.core.models;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private int level;
    private long xp;
    private long coins;
    private String playerClass;
    private String currentRealm;
    private long lastKitClaim;
    private boolean dirty;

    private int kills;
    private int bossKills;
    private int deaths;
    private long totalCoinsEarned;
    private int currentKillstreak;
    private int highestKillstreak;
    private long lastActionTime;
    private boolean afk;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.level = 1;
        this.xp = 0;
        this.coins = 0;
        this.playerClass = null;
        this.currentRealm = null;
        this.lastKitClaim = 0;
        this.dirty = false;
        this.kills = 0;
        this.bossKills = 0;
        this.deaths = 0;
        this.totalCoinsEarned = 0;
        this.currentKillstreak = 0;
        this.highestKillstreak = 0;
        this.lastActionTime = System.currentTimeMillis();
        this.afk = false;
    }

    public PlayerData(UUID uuid, int level, long xp, long coins, String playerClass, String currentRealm, long lastKitClaim) {
        this.uuid = uuid;
        this.level = level;
        this.xp = xp;
        this.coins = coins;
        this.playerClass = playerClass;
        this.currentRealm = currentRealm;
        this.lastKitClaim = lastKitClaim;
        this.dirty = false;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        this.dirty = true;
    }

    public long getXP() {
        return xp;
    }

    public void setXP(long xp) {
        this.xp = xp;
        this.dirty = true;
    }

    public void addXP(long amount) {
        this.xp += amount;
        this.dirty = true;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
        this.dirty = true;
    }

    public void addCoins(long amount) {
        this.coins += amount;
        this.dirty = true;
    }

    public void removeCoins(long amount) {
        this.coins = Math.max(0, this.coins - amount);
        this.dirty = true;
    }

    public String getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
        this.dirty = true;
    }

    public boolean hasClass() {
        return playerClass != null && !playerClass.isEmpty();
    }

    public String getCurrentRealm() {
        return currentRealm;
    }

    public void setCurrentRealm(String currentRealm) {
        this.currentRealm = currentRealm;
        this.dirty = true;
    }

    public long getLastKitClaim() {
        return lastKitClaim;
    }

    public void setLastKitClaim(long lastKitClaim) {
        this.lastKitClaim = lastKitClaim;
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        this.kills++;
        this.dirty = true;
    }

    public int getBossKills() {
        return bossKills;
    }

    public void incrementBossKills() {
        this.bossKills++;
        this.dirty = true;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementDeaths() {
        this.deaths++;
        this.dirty = true;
    }

    public long getTotalCoinsEarned() {
        return totalCoinsEarned;
    }

    public void addTotalCoinsEarned(long amount) {
        this.totalCoinsEarned += amount;
        this.dirty = true;
    }

    public int getCurrentKillstreak() {
        return currentKillstreak;
    }

    public void incrementCurrentKillstreak() {
        this.currentKillstreak++;
        this.dirty = true;
    }

    public void setCurrentKillstreak(int streak) {
        this.currentKillstreak = streak;
        this.dirty = true;
    }

    public int getHighestKillstreak() {
        return highestKillstreak;
    }

    public void setHighestKillstreak(int streak) {
        this.highestKillstreak = streak;
        this.dirty = true;
    }

    public long getLastActionTime() {
        return lastActionTime;
    }

    public void updateLastActionTime() {
        this.lastActionTime = System.currentTimeMillis();
    }

    public boolean isAfk() {
        return afk;
    }

    public void setAfk(boolean afk) {
        this.afk = afk;
    }

    public long getXp() {
        return xp;
    }
}
