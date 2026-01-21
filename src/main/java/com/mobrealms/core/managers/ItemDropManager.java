package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemDropManager {
    private final MobRealmsCore plugin;
    private final Random random = new Random();

    public ItemDropManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public ItemStack rollDrop(int mobLevel) {
        double roll = random.nextDouble();
        if (roll < 0.01) return createItem(mobLevel, "LEGENDARY");
        if (roll < 0.05) return createItem(mobLevel, "EPIC");
        if (roll < 0.15) return createItem(mobLevel, "RARE");
        if (roll < 0.30) return createItem(mobLevel, "COMMON");
        return null;
    }

    public ItemStack rollBossDrop(int mobLevel) {
        double roll = random.nextDouble();
        if (roll < 0.15) return createItem(mobLevel, "LEGENDARY");
        if (roll < 0.40) return createItem(mobLevel, "EPIC");
        if (roll < 0.70) return createItem(mobLevel, "RARE");
        return createItem(mobLevel, "COMMON");
    }

    private ItemStack createItem(int level, String rarity) {
        Material[] materials = {Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE,
                               Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS,
                               Material.DIAMOND_BOOTS, Material.BOW};
        Material mat = materials[random.nextInt(materials.length)];
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        String colorCode = rarity.equals("LEGENDARY") ? "§6" : rarity.equals("EPIC") ? "§5" :
                          rarity.equals("RARE") ? "§9" : "§f";
        meta.setDisplayName(colorCode + rarity + " " + mat.name().replace("_", " ") + " (Lvl " + level + ")");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Level Requirement: " + level);
        lore.add(colorCode + "" + ChatColor.BOLD + rarity);
        lore.add(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Soulbound");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }
}
