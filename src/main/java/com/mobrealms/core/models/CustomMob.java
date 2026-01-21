package com.mobrealms.core.models;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CustomMob {

    private final String id;
    private final String displayName;
    private final EntityType entityType;
    private final int level;
    private final double health;
    private final long xpReward;
    private final long coinReward;
    private final double speedMultiplier;
    private final double damageMultiplier;
    private final Map<Material, Double> armor;
    private final Map<PotionEffectType, Integer> effects;

    public CustomMob(String id, String displayName, EntityType entityType, int level, double health, long xpReward, long coinReward) {
        this(id, displayName, entityType, level, health, xpReward, coinReward, 1.0, 1.0, new HashMap<>(), new HashMap<>());
    }

    public CustomMob(String id, String displayName, EntityType entityType, int level, double health, long xpReward, long coinReward,
                     double speedMultiplier, double damageMultiplier, Map<Material, Double> armor, Map<PotionEffectType, Integer> effects) {
        this.id = id;
        this.displayName = displayName;
        this.entityType = entityType;
        this.level = level;
        this.health = health;
        this.xpReward = xpReward;
        this.coinReward = coinReward;
        this.speedMultiplier = speedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.armor = armor;
        this.effects = effects;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public int getLevel() {
        return level;
    }

    public double getHealth() {
        return health;
    }

    public long getXPReward() {
        return xpReward;
    }

    public long getCoinReward() {
        return coinReward;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public Map<Material, Double> getArmor() {
        return armor;
    }

    public Map<PotionEffectType, Integer> getEffects() {
        return effects;
    }
}
