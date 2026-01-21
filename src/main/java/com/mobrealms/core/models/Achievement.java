package com.mobrealms.core.models;

import java.time.LocalDateTime;

public class Achievement {
    private final String id;
    private final String name;
    private final String description;
    private final Material icon;
    private final int order;
    private boolean unlocked;
    private LocalDateTime unlockedAt;

    public Achievement(String id, String name, String description, Material icon, int order) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.order = order;
        this.unlocked = false;
        this.unlockedAt = null;
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

    public Material getIcon() {
        return icon;
    }

    public int getOrder() {
        return order;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
        if (unlocked && unlockedAt == null) {
            this.unlockedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public enum Material {
        BOOK,
        DIAMOND,
        EMERALD,
        GOLD_INGOT,
        IRON_SWORD,
        DRAGON_HEAD,
        NETHER_STAR
    }
}
