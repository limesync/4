package com.mobrealms.core.models;

public class BaldricQuest {
    private final String id;
    private final String name;
    private final String description;
    private final QuestType type;
    private final int targetAmount;
    private final long coinReward;
    private final long xpReward;
    private final int order;
    private int progress;
    private boolean completed;
    private boolean claimed;

    public BaldricQuest(String id, String name, String description, QuestType type, int targetAmount,
                        long coinReward, long xpReward, int order) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.targetAmount = targetAmount;
        this.coinReward = coinReward;
        this.xpReward = xpReward;
        this.order = order;
        this.progress = 0;
        this.completed = false;
        this.claimed = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public QuestType getType() {
        return type;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public long getCoinReward() {
        return coinReward;
    }

    public long getXpReward() {
        return xpReward;
    }

    public int getOrder() {
        return order;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = Math.min(progress, targetAmount);
        if (this.progress >= targetAmount) {
            this.completed = true;
        }
    }

    public void addProgress(int amount) {
        setProgress(this.progress + amount);
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public double getProgressPercent() {
        return (double) progress / targetAmount * 100.0;
    }

    public enum QuestType {
        KILL_MOBS,
        EARN_COINS,
        REACH_LEVEL,
        TALK_TO_NPC
    }
}
