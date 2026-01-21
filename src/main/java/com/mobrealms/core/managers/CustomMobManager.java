package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.CustomMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomMobManager {

    private final MobRealmsCore plugin;
    private final Map<String, CustomMob> customMobs;
    private final Map<UUID, CustomMob> spawnedMobs;

    public CustomMobManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.customMobs = new HashMap<>();
        this.spawnedMobs = new HashMap<>();
        initializeCustomMobs();
    }

    private void initializeCustomMobs() {
        customMobs.put("tiny_slime", new CustomMob("tiny_slime", "§aTiny Slime", EntityType.SLIME, 1, 10, 5, 2,
                0.8, 0.8, new HashMap<>(), Map.of(PotionEffectType.JUMP, 2)));
        customMobs.put("goblin", new CustomMob("goblin", "§2Goblin", EntityType.ZOMBIE, 2, 20, 10, 5,
                1.2, 0.9, Map.of(Material.LEATHER_CHESTPLATE, 0.3, Material.LEATHER_HELMET, 0.3), new HashMap<>()));
        customMobs.put("cave_spider", new CustomMob("cave_spider", "§8Cave Spider", EntityType.CAVE_SPIDER, 3, 15, 12, 6,
                1.4, 1.0, new HashMap<>(), Map.of(PotionEffectType.POISON, 1, PotionEffectType.SPEED, 1)));
        customMobs.put("bandit", new CustomMob("bandit", "§7Bandit", EntityType.PILLAGER, 4, 25, 15, 8,
                1.1, 1.1, Map.of(Material.LEATHER_CHESTPLATE, 0.5, Material.LEATHER_LEGGINGS, 0.4), new HashMap<>()));
        customMobs.put("forest_wolf", new CustomMob("forest_wolf", "§7Forest Wolf", EntityType.WOLF, 5, 30, 20, 10,
                1.6, 1.2, new HashMap<>(), Map.of(PotionEffectType.SPEED, 1)));

        customMobs.put("orc_grunt", new CustomMob("orc_grunt", "§cOrc Grunt", EntityType.ZOMBIE, 6, 35, 25, 12,
                1.0, 1.3, Map.of(Material.IRON_CHESTPLATE, 0.6, Material.IRON_HELMET, 0.5, Material.IRON_BOOTS, 0.4), Map.of(PotionEffectType.INCREASE_DAMAGE, 1)));
        customMobs.put("skeleton_warrior", new CustomMob("skeleton_warrior", "§fSkeleton Warrior", EntityType.SKELETON, 7, 30, 28, 14,
                1.0, 1.1, Map.of(Material.CHAINMAIL_CHESTPLATE, 0.7, Material.CHAINMAIL_HELMET, 0.6, Material.CHAINMAIL_LEGGINGS, 0.5), Map.of(PotionEffectType.SPEED, 1)));
        customMobs.put("desert_mummy", new CustomMob("desert_mummy", "§eDesert Mummy", EntityType.HUSK, 8, 40, 32, 16,
                0.8, 1.2, Map.of(Material.GOLDEN_CHESTPLATE, 0.5, Material.GOLDEN_HELMET, 0.4), Map.of(PotionEffectType.HUNGER, 2, PotionEffectType.WEAKNESS, 1)));
        customMobs.put("toxic_spider", new CustomMob("toxic_spider", "§2Toxic Spider", EntityType.SPIDER, 9, 35, 35, 18,
                1.5, 1.1, new HashMap<>(), Map.of(PotionEffectType.POISON, 3, PotionEffectType.SPEED, 2)));
        customMobs.put("dark_priest", new CustomMob("dark_priest", "§5Dark Priest", EntityType.WITCH, 10, 30, 40, 20,
                1.0, 1.0, new HashMap<>(), Map.of(PotionEffectType.REGENERATION, 2, PotionEffectType.SPEED, 1)));

        customMobs.put("fire_imp", new CustomMob("fire_imp", "§6Fire Imp", EntityType.BLAZE, 12, 45, 50, 25,
                1.3, 1.4, new HashMap<>(), Map.of(PotionEffectType.FIRE_RESISTANCE, 1, PotionEffectType.SPEED, 1)));
        customMobs.put("ice_wraith", new CustomMob("ice_wraith", "§bIce Wraith", EntityType.STRAY, 13, 50, 55, 28,
                1.2, 1.1, Map.of(Material.DIAMOND_HELMET, 0.3, Material.CHAINMAIL_CHESTPLATE, 0.4), Map.of(PotionEffectType.SLOW, 2, PotionEffectType.WEAKNESS, 1)));
        customMobs.put("corrupted_knight", new CustomMob("corrupted_knight", "§4Corrupted Knight", EntityType.ZOMBIE, 14, 60, 60, 30,
                0.9, 1.5, Map.of(Material.IRON_CHESTPLATE, 0.8, Material.IRON_HELMET, 0.8, Material.IRON_LEGGINGS, 0.7, Material.IRON_BOOTS, 0.6), Map.of(PotionEffectType.INCREASE_DAMAGE, 1)));
        customMobs.put("phantom_assassin", new CustomMob("phantom_assassin", "§8Phantom Assassin", EntityType.PHANTOM, 15, 55, 65, 32,
                1.8, 1.4, new HashMap<>(), Map.of(PotionEffectType.INVISIBILITY, 1, PotionEffectType.SPEED, 2)));
        customMobs.put("void_stalker", new CustomMob("void_stalker", "§5Void Stalker", EntityType.ENDERMAN, 16, 65, 70, 35,
                1.5, 1.3, new HashMap<>(), Map.of(PotionEffectType.SPEED, 3, PotionEffectType.INVISIBILITY, 1)));

        customMobs.put("inferno_guard", new CustomMob("inferno_guard", "§cInferno Guard", EntityType.WITHER_SKELETON, 18, 75, 80, 40,
                1.1, 1.6, Map.of(Material.NETHERITE_CHESTPLATE, 0.5, Material.NETHERITE_HELMET, 0.5, Material.NETHERITE_LEGGINGS, 0.4), Map.of(PotionEffectType.FIRE_RESISTANCE, 1, PotionEffectType.INCREASE_DAMAGE, 1)));
        customMobs.put("frost_giant", new CustomMob("frost_giant", "§bFrost Giant", EntityType.GIANT, 20, 100, 90, 45,
                0.7, 1.8, Map.of(Material.DIAMOND_CHESTPLATE, 0.7, Material.DIAMOND_HELMET, 0.7, Material.DIAMOND_LEGGINGS, 0.6), Map.of(PotionEffectType.DAMAGE_RESISTANCE, 2, PotionEffectType.SLOW, 1)));
        customMobs.put("shadow_demon", new CustomMob("shadow_demon", "§0Shadow Demon", EntityType.WITHER_SKELETON, 22, 85, 100, 50,
                1.6, 1.7, Map.of(Material.NETHERITE_HELMET, 0.6, Material.NETHERITE_BOOTS, 0.5), Map.of(PotionEffectType.SPEED, 3, PotionEffectType.INCREASE_DAMAGE, 2, PotionEffectType.INVISIBILITY, 1)));
        customMobs.put("arcane_guardian", new CustomMob("arcane_guardian", "§dArcane Guardian", EntityType.IRON_GOLEM, 25, 120, 120, 60,
                0.8, 1.5, new HashMap<>(), Map.of(PotionEffectType.DAMAGE_RESISTANCE, 3, PotionEffectType.REGENERATION, 1)));
        customMobs.put("death_knight", new CustomMob("death_knight", "§4Death Knight", EntityType.WITHER_SKELETON, 28, 110, 140, 70,
                1.0, 1.9, Map.of(Material.NETHERITE_CHESTPLATE, 0.8, Material.NETHERITE_HELMET, 0.8, Material.NETHERITE_LEGGINGS, 0.8, Material.NETHERITE_BOOTS, 0.7), Map.of(PotionEffectType.INCREASE_DAMAGE, 3, PotionEffectType.SPEED, 1)));

        customMobs.put("nether_lord", new CustomMob("nether_lord", "§4§lNether Lord", EntityType.PIGLIN_BRUTE, 30, 150, 180, 90,
                1.2, 2.0, Map.of(Material.NETHERITE_CHESTPLATE, 0.9, Material.NETHERITE_HELMET, 0.9, Material.NETHERITE_LEGGINGS, 0.8, Material.NETHERITE_BOOTS, 0.8), Map.of(PotionEffectType.FIRE_RESISTANCE, 1, PotionEffectType.INCREASE_DAMAGE, 3, PotionEffectType.SPEED, 1)));
        customMobs.put("elder_lich", new CustomMob("elder_lich", "§d§lElder Lich", EntityType.WITHER_SKELETON, 35, 180, 220, 110,
                1.1, 2.2, Map.of(Material.NETHERITE_HELMET, 0.9, Material.NETHERITE_CHESTPLATE, 0.8), Map.of(PotionEffectType.REGENERATION, 3, PotionEffectType.DAMAGE_RESISTANCE, 2, PotionEffectType.SPEED, 2)));
        customMobs.put("void_dragon", new CustomMob("void_dragon", "§5§lVoid Dragon", EntityType.ENDER_DRAGON, 40, 250, 300, 150,
                1.4, 2.8, new HashMap<>(), Map.of(PotionEffectType.SPEED, 3, PotionEffectType.INCREASE_DAMAGE, 4, PotionEffectType.DAMAGE_RESISTANCE, 2)));
        customMobs.put("titan_golem", new CustomMob("titan_golem", "§e§lTitan Golem", EntityType.IRON_GOLEM, 45, 300, 400, 200,
                0.7, 2.3, new HashMap<>(), Map.of(PotionEffectType.DAMAGE_RESISTANCE, 4, PotionEffectType.INCREASE_DAMAGE, 3, PotionEffectType.REGENERATION, 2)));
        customMobs.put("ancient_wyvern", new CustomMob("ancient_wyvern", "§c§lAncient Wyvern", EntityType.ENDER_DRAGON, 50, 400, 500, 250,
                1.5, 3.5, new HashMap<>(), Map.of(PotionEffectType.SPEED, 4, PotionEffectType.INCREASE_DAMAGE, 5, PotionEffectType.DAMAGE_RESISTANCE, 3, PotionEffectType.REGENERATION, 2)));
    }

    public CustomMob getCustomMob(String id) {
        return customMobs.get(id);
    }

    public Map<String, CustomMob> getAllCustomMobs() {
        return new HashMap<>(customMobs);
    }

    public Entity spawnCustomMob(String mobId, Location location) {
        CustomMob customMob = customMobs.get(mobId);
        if (customMob == null) return null;

        Entity entity = location.getWorld().spawnEntity(location, customMob.getEntityType());

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', customMob.getDisplayName() + " §7[Lvl " + customMob.getLevel() + "]"));
            livingEntity.setCustomNameVisible(true);

            if (livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
                livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(customMob.getHealth());
                livingEntity.setHealth(customMob.getHealth());
            }

            if (livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null && customMob.getSpeedMultiplier() != 1.0) {
                double currentSpeed = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
                livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(currentSpeed * customMob.getSpeedMultiplier());
            }

            if (livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null && customMob.getDamageMultiplier() != 1.0) {
                double currentDamage = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
                livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(currentDamage * customMob.getDamageMultiplier());
            }

            if (livingEntity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE) != null) {
                livingEntity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(40.0);
            }

            if (livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE) != null) {
                double knockbackRes = customMob.getLevel() >= 30 ? 0.5 : customMob.getLevel() >= 15 ? 0.3 : 0.1;
                livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackRes);
            }

            if (livingEntity instanceof Mob) {
                Mob mob = (Mob) livingEntity;
                mob.setRemoveWhenFarAway(false);
                mob.setAware(true);
                mob.setPersistent(true);
            }

            if (entity instanceof Slime) {
                Slime slime = (Slime) entity;
                slime.setSize(customMob.getLevel() >= 10 ? 3 : customMob.getLevel() >= 5 ? 2 : 1);
            }

            Map<Material, Double> armor = customMob.getArmor();
            if (!armor.isEmpty() && livingEntity instanceof Mob) {
                EntityEquipment equipment = livingEntity.getEquipment();
                if (equipment != null) {
                    for (Map.Entry<Material, Double> entry : armor.entrySet()) {
                        Material armorMaterial = entry.getKey();
                        ItemStack armorPiece = new ItemStack(armorMaterial);

                        if (armorMaterial.toString().contains("HELMET")) {
                            equipment.setHelmet(armorPiece);
                            equipment.setHelmetDropChance((float) entry.getValue().doubleValue());
                        } else if (armorMaterial.toString().contains("CHESTPLATE")) {
                            equipment.setChestplate(armorPiece);
                            equipment.setChestplateDropChance((float) entry.getValue().doubleValue());
                        } else if (armorMaterial.toString().contains("LEGGINGS")) {
                            equipment.setLeggings(armorPiece);
                            equipment.setLeggingsDropChance((float) entry.getValue().doubleValue());
                        } else if (armorMaterial.toString().contains("BOOTS")) {
                            equipment.setBoots(armorPiece);
                            equipment.setBootsDropChance((float) entry.getValue().doubleValue());
                        }
                    }
                }
            }

            Map<PotionEffectType, Integer> effects = customMob.getEffects();
            if (!effects.isEmpty()) {
                for (Map.Entry<PotionEffectType, Integer> entry : effects.entrySet()) {
                    livingEntity.addPotionEffect(new PotionEffect(entry.getKey(), Integer.MAX_VALUE, entry.getValue() - 1, false, false));
                }
            }

            livingEntity.setMetadata("CustomMob", new FixedMetadataValue(plugin, mobId));
            livingEntity.setMetadata("MobLevel", new FixedMetadataValue(plugin, customMob.getLevel()));
            livingEntity.setMetadata("XPReward", new FixedMetadataValue(plugin, customMob.getXPReward()));
            livingEntity.setMetadata("CoinReward", new FixedMetadataValue(plugin, customMob.getCoinReward()));
            livingEntity.setMetadata("DamageMultiplier", new FixedMetadataValue(plugin, customMob.getDamageMultiplier()));

            spawnedMobs.put(entity.getUniqueId(), customMob);
        }

        return entity;
    }

    public CustomMob getSpawnedMob(UUID entityUUID) {
        return spawnedMobs.get(entityUUID);
    }

    public void removeSpawnedMob(UUID entityUUID) {
        spawnedMobs.remove(entityUUID);
    }

    public boolean isCustomMob(Entity entity) {
        return entity.hasMetadata("CustomMob");
    }

    public String getCustomMobId(Entity entity) {
        if (!isCustomMob(entity)) return null;
        return entity.getMetadata("CustomMob").get(0).asString();
    }
}
