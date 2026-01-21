package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.ShopItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.stream.Collectors;

public class ShopManager {

    private final MobRealmsCore plugin;
    private final Map<String, ShopItem> shopItems;

    public ShopManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.shopItems = new HashMap<>();
        loadShopItems();
    }

    private void loadShopItems() {
        addWeapons();
        addArmor();
        addTools();
        addFood();
        addBlocks();
        addClasses();
        addSpecialItems();
    }

    private void addWeapons() {
        shopItems.put("iron_sword", new ShopItem("iron_sword", "§7Iron Sword", Material.IRON_SWORD,
            100, 1, ShopItem.ShopCategory.WEAPONS,
            Map.of(Enchantment.DAMAGE_ALL, 1),
            List.of("§7A basic sword for combat")));

        shopItems.put("diamond_sword_1", new ShopItem("diamond_sword_1", "§bDiamond Sword", Material.DIAMOND_SWORD,
            500, 5, ShopItem.ShopCategory.WEAPONS,
            Map.of(Enchantment.DAMAGE_ALL, 2),
            List.of("§7A powerful sword", "§7for experienced fighters")));

        shopItems.put("diamond_sword_2", new ShopItem("diamond_sword_2", "§bEnhanced Diamond Sword", Material.DIAMOND_SWORD,
            1500, 10, ShopItem.ShopCategory.WEAPONS,
            Map.of(Enchantment.DAMAGE_ALL, 3, Enchantment.FIRE_ASPECT, 1),
            List.of("§7An enhanced diamond sword", "§7with fire aspect")));

        shopItems.put("netherite_sword_1", new ShopItem("netherite_sword_1", "§4Netherite Sword", Material.NETHERITE_SWORD,
            3000, 20, ShopItem.ShopCategory.WEAPONS,
            Map.of(Enchantment.DAMAGE_ALL, 4, Enchantment.FIRE_ASPECT, 2),
            List.of("§7The finest weapon", "§7forged from netherite")));

        shopItems.put("netherite_sword_2", new ShopItem("netherite_sword_2", "§4§lLegendary Netherite Sword", Material.NETHERITE_SWORD,
            10000, 40, ShopItem.ShopCategory.WEAPONS,
            Map.of(Enchantment.DAMAGE_ALL, 5, Enchantment.FIRE_ASPECT, 2, Enchantment.KNOCKBACK, 2, Enchantment.LOOT_BONUS_MOBS, 3),
            List.of("§7A legendary weapon", "§7for true champions")));

        shopItems.put("bow_1", new ShopItem("bow_1", "§aHunter's Bow", Material.BOW,
            400, 5, ShopItem.ShopCategory.WEAPONS,
            Map.of(Enchantment.ARROW_DAMAGE, 2),
            List.of("§7A reliable bow", "§7for ranged combat")));

        shopItems.put("bow_2", new ShopItem("bow_2", "§a§lMaster Hunter's Bow", Material.BOW,
            2000, 20, ShopItem.ShopCategory.WEAPONS,
            Map.of(Enchantment.ARROW_DAMAGE, 4, Enchantment.ARROW_FIRE, 1, Enchantment.ARROW_INFINITE, 1),
            List.of("§7A masterwork bow", "§7with infinite arrows")));
    }

    private void addArmor() {
        shopItems.put("iron_helmet", new ShopItem("iron_helmet", "§7Iron Helmet", Material.IRON_HELMET,
            150, 1, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 1),
            List.of("§7Basic head protection")));

        shopItems.put("iron_chestplate", new ShopItem("iron_chestplate", "§7Iron Chestplate", Material.IRON_CHESTPLATE,
            200, 1, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 1),
            List.of("§7Basic chest protection")));

        shopItems.put("iron_leggings", new ShopItem("iron_leggings", "§7Iron Leggings", Material.IRON_LEGGINGS,
            175, 1, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 1),
            List.of("§7Basic leg protection")));

        shopItems.put("iron_boots", new ShopItem("iron_boots", "§7Iron Boots", Material.IRON_BOOTS,
            125, 1, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 1),
            List.of("§7Basic foot protection")));

        shopItems.put("diamond_helmet", new ShopItem("diamond_helmet", "§bDiamond Helmet", Material.DIAMOND_HELMET,
            750, 10, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 3, Enchantment.DURABILITY, 2),
            List.of("§7Strong head protection")));

        shopItems.put("diamond_chestplate", new ShopItem("diamond_chestplate", "§bDiamond Chestplate", Material.DIAMOND_CHESTPLATE,
            1000, 10, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 3, Enchantment.DURABILITY, 2),
            List.of("§7Strong chest protection")));

        shopItems.put("diamond_leggings", new ShopItem("diamond_leggings", "§bDiamond Leggings", Material.DIAMOND_LEGGINGS,
            875, 10, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 3, Enchantment.DURABILITY, 2),
            List.of("§7Strong leg protection")));

        shopItems.put("diamond_boots", new ShopItem("diamond_boots", "§bDiamond Boots", Material.DIAMOND_BOOTS,
            625, 10, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 3, Enchantment.DURABILITY, 2),
            List.of("§7Strong foot protection")));

        shopItems.put("netherite_helmet", new ShopItem("netherite_helmet", "§4Netherite Helmet", Material.NETHERITE_HELMET,
            4000, 30, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 4, Enchantment.DURABILITY, 3, Enchantment.THORNS, 2),
            List.of("§7Ultimate head protection")));

        shopItems.put("netherite_chestplate", new ShopItem("netherite_chestplate", "§4Netherite Chestplate", Material.NETHERITE_CHESTPLATE,
            5000, 30, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 4, Enchantment.DURABILITY, 3, Enchantment.THORNS, 2),
            List.of("§7Ultimate chest protection")));

        shopItems.put("netherite_leggings", new ShopItem("netherite_leggings", "§4Netherite Leggings", Material.NETHERITE_LEGGINGS,
            4500, 30, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 4, Enchantment.DURABILITY, 3, Enchantment.THORNS, 2),
            List.of("§7Ultimate leg protection")));

        shopItems.put("netherite_boots", new ShopItem("netherite_boots", "§4Netherite Boots", Material.NETHERITE_BOOTS,
            3500, 30, ShopItem.ShopCategory.ARMOR,
            Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 4, Enchantment.DURABILITY, 3, Enchantment.THORNS, 2, Enchantment.PROTECTION_FALL, 4),
            List.of("§7Ultimate foot protection")));
    }

    private void addTools() {
        shopItems.put("diamond_pickaxe", new ShopItem("diamond_pickaxe", "§bDiamond Pickaxe", Material.DIAMOND_PICKAXE,
            800, 5, ShopItem.ShopCategory.TOOLS,
            Map.of(Enchantment.DIG_SPEED, 3, Enchantment.DURABILITY, 2),
            List.of("§7Efficient mining tool")));

        shopItems.put("diamond_axe", new ShopItem("diamond_axe", "§bDiamond Axe", Material.DIAMOND_AXE,
            700, 5, ShopItem.ShopCategory.TOOLS,
            Map.of(Enchantment.DIG_SPEED, 3, Enchantment.DURABILITY, 2),
            List.of("§7Efficient chopping tool")));

        shopItems.put("diamond_shovel", new ShopItem("diamond_shovel", "§bDiamond Shovel", Material.DIAMOND_SHOVEL,
            500, 5, ShopItem.ShopCategory.TOOLS,
            Map.of(Enchantment.DIG_SPEED, 3, Enchantment.DURABILITY, 2),
            List.of("§7Efficient digging tool")));
    }

    private void addFood() {
        shopItems.put("steak", new ShopItem("steak", "§6Cooked Steak x16", Material.COOKED_BEEF,
            50, 1, ShopItem.ShopCategory.FOOD,
            new HashMap<>(),
            List.of("§7Restores hunger")));

        shopItems.put("golden_apple", new ShopItem("golden_apple", "§eGolden Apple x4", Material.GOLDEN_APPLE,
            200, 5, ShopItem.ShopCategory.FOOD,
            new HashMap<>(),
            List.of("§7Provides regeneration")));

        shopItems.put("enchanted_golden_apple", new ShopItem("enchanted_golden_apple", "§6§lEnchanted Golden Apple", Material.ENCHANTED_GOLDEN_APPLE,
            2000, 20, ShopItem.ShopCategory.FOOD,
            new HashMap<>(),
            List.of("§7Provides powerful buffs")));
    }

    private void addBlocks() {
        shopItems.put("stone", new ShopItem("stone", "§7Stone x64", Material.STONE,
            20, 1, ShopItem.ShopCategory.BLOCKS,
            new HashMap<>(),
            List.of("§7Basic building block")));

        shopItems.put("oak_planks", new ShopItem("oak_planks", "§6Oak Planks x64", Material.OAK_PLANKS,
            30, 1, ShopItem.ShopCategory.BLOCKS,
            new HashMap<>(),
            List.of("§7Wooden building material")));

        shopItems.put("cobblestone", new ShopItem("cobblestone", "§7Cobblestone x64", Material.COBBLESTONE,
            15, 1, ShopItem.ShopCategory.BLOCKS,
            new HashMap<>(),
            List.of("§7Common building block")));

        shopItems.put("glass", new ShopItem("glass", "§fGlass x64", Material.GLASS,
            50, 1, ShopItem.ShopCategory.BLOCKS,
            new HashMap<>(),
            List.of("§7Transparent building material")));

        shopItems.put("bricks", new ShopItem("bricks", "§cBricks x64", Material.BRICKS,
            75, 5, ShopItem.ShopCategory.BLOCKS,
            new HashMap<>(),
            List.of("§7Decorative building block")));

        shopItems.put("quartz_block", new ShopItem("quartz_block", "§fQuartz Block x32", Material.QUARTZ_BLOCK,
            150, 10, ShopItem.ShopCategory.BLOCKS,
            new HashMap<>(),
            List.of("§7Premium building material")));

        shopItems.put("obsidian", new ShopItem("obsidian", "§5Obsidian x16", Material.OBSIDIAN,
            200, 15, ShopItem.ShopCategory.BLOCKS,
            new HashMap<>(),
            List.of("§7Strong blast-resistant block")));

        shopItems.put("glowstone", new ShopItem("glowstone", "§eGlowstone x32", Material.GLOWSTONE,
            100, 5, ShopItem.ShopCategory.BLOCKS,
            new HashMap<>(),
            List.of("§7Provides light")));
    }

    private void addClasses() {
        shopItems.put("warrior_unlock", new ShopItem("warrior_unlock", "§c§lWarrior Class", Material.IRON_SWORD,
            5000, 1, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Warrior class", "§7+20% melee damage", "§7+10% defense")));

        shopItems.put("mage_unlock", new ShopItem("mage_unlock", "§9§lMage Class", Material.BLAZE_ROD,
            5000, 1, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Mage class", "§7+30% magic damage", "§7+15% XP gain")));

        shopItems.put("archer_unlock", new ShopItem("archer_unlock", "§a§lArcher Class", Material.BOW,
            5000, 1, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Archer class", "§7+25% ranged damage", "§7+10% speed")));

        shopItems.put("rogue_unlock", new ShopItem("rogue_unlock", "§e§lRogue Class", Material.DIAMOND_SWORD,
            5000, 1, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Rogue class", "§7+15% crit chance", "§7+20% speed")));

        shopItems.put("paladin_unlock", new ShopItem("paladin_unlock", "§6§lPaladin Class", Material.GOLDEN_SWORD,
            7500, 15, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Paladin class", "§7+15% defense", "§7+10% healing", "§7+15% damage vs undead")));

        shopItems.put("assassin_unlock", new ShopItem("assassin_unlock", "§8§lAssassin Class", Material.NETHERITE_SWORD,
            10000, 25, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Assassin class", "§7+30% crit damage", "§7+25% speed", "§7+10% dodge chance")));

        shopItems.put("berserker_unlock", new ShopItem("berserker_unlock", "§4§lBerserker Class", Material.NETHERITE_AXE,
            10000, 30, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Berserker class", "§7+40% melee damage", "§7-10% defense", "§7Rage mode")));

        shopItems.put("necromancer_unlock", new ShopItem("necromancer_unlock", "§5§lNecromancer Class", Material.WITHER_SKELETON_SKULL,
            12500, 35, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Necromancer class", "§7Summon minions", "§7+25% magic damage", "§7Life steal")));

        shopItems.put("tank_unlock", new ShopItem("tank_unlock", "§7§lTank Class", Material.SHIELD,
            7500, 20, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Tank class", "§7+50% defense", "§7+30% health", "§7-15% speed")));

        shopItems.put("healer_unlock", new ShopItem("healer_unlock", "§d§lHealer Class", Material.GLISTERING_MELON_SLICE,
            8000, 20, ShopItem.ShopCategory.CLASSES,
            new HashMap<>(),
            List.of("§7Unlock the Healer class", "§7+50% healing", "§7+20% XP gain", "§7Regeneration aura")));
    }

    private void addSpecialItems() {
        shopItems.put("totem", new ShopItem("totem", "§e§lTotem of Undying", Material.TOTEM_OF_UNDYING,
            10000, 25, ShopItem.ShopCategory.SPECIAL,
            new HashMap<>(),
            List.of("§7Saves you from death")));

        shopItems.put("elytra", new ShopItem("elytra", "§f§lElytra", Material.ELYTRA,
            15000, 35, ShopItem.ShopCategory.SPECIAL,
            new HashMap<>(),
            List.of("§7Allows you to fly")));

        shopItems.put("exp_bottle", new ShopItem("exp_bottle", "§aExperience Bottle x16", Material.EXPERIENCE_BOTTLE,
            500, 5, ShopItem.ShopCategory.SPECIAL,
            new HashMap<>(),
            List.of("§7Grants experience points")));

        shopItems.put("ender_pearl", new ShopItem("ender_pearl", "§5Ender Pearl x4", Material.ENDER_PEARL,
            250, 10, ShopItem.ShopCategory.SPECIAL,
            new HashMap<>(),
            List.of("§7Teleport short distances")));

        shopItems.put("trident", new ShopItem("trident", "§b§lTrideant", Material.TRIDENT,
            20000, 40, ShopItem.ShopCategory.SPECIAL,
            Map.of(Enchantment.LOYALTY, 3, Enchantment.IMPALING, 4),
            List.of("§7Powerful aquatic weapon")));

        shopItems.put("spawner", new ShopItem("spawner", "§6§lMob Spawner", Material.SPAWNER,
            50000, 50, ShopItem.ShopCategory.SPECIAL,
            new HashMap<>(),
            List.of("§7Place to spawn mobs", "§7Right-click to configure")));
    }

    public ShopItem getShopItem(String id) {
        return shopItems.get(id);
    }

    public List<ShopItem> getItemsByCategory(ShopItem.ShopCategory category) {
        return shopItems.values().stream()
            .filter(item -> item.getCategory() == category)
            .sorted(Comparator.comparingInt(ShopItem::getRequiredLevel))
            .collect(Collectors.toList());
    }

    public Collection<ShopItem> getAllItems() {
        return shopItems.values();
    }
}
