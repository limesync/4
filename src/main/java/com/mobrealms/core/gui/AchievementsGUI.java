package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AchievementsGUI {
    private final MobRealmsCore plugin;
    private static final String INVENTORY_TITLE = "§6§lAchievements";

    public AchievementsGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, INVENTORY_TITLE);

        ItemStack glass = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glass);
            inv.setItem(i + 45, glass);
        }
        for (int i = 9; i < 45; i += 9) {
            inv.setItem(i, glass);
            inv.setItem(i + 8, glass);
        }

        List<Achievement> achievements = plugin.getAchievementManager().getPlayerAchievementsSorted(player.getUniqueId());
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

        for (int i = 0; i < Math.min(achievements.size(), slots.length); i++) {
            Achievement achievement = achievements.get(i);
            inv.setItem(slots[i], createAchievementItem(achievement));
        }

        int unlockedCount = (int) achievements.stream().filter(Achievement::isUnlocked).count();
        inv.setItem(49, createStatsItem(unlockedCount, achievements.size()));

        player.openInventory(inv);
    }

    private ItemStack createAchievementItem(Achievement achievement) {
        Material material = convertAchievementMaterial(achievement.getIcon(), achievement.isUnlocked());

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (achievement.isUnlocked()) {
            meta.setDisplayName(achievement.getName());
        } else {
            meta.setDisplayName("§8§k" + achievement.getName());
        }

        List<String> lore = new ArrayList<>();

        if (achievement.isUnlocked()) {
            lore.add("§7" + achievement.getDescription());
            lore.add("");
            lore.add("§a§l✓ UNLOCKED");
            if (achievement.getUnlockedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                lore.add("§7Unlocked: §e" + achievement.getUnlockedAt().format(formatter));
            }
        } else {
            lore.add("§8§o" + achievement.getDescription());
            lore.add("");
            lore.add("§7§oLocked");
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createStatsItem(int unlocked, int total) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lYour Progress");

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Achievements Unlocked:");
        lore.add("  §e" + unlocked + " §7/ §e" + total);
        lore.add("");

        int percentage = total > 0 ? (int) ((unlocked / (double) total) * 100) : 0;
        lore.add("§7Completion: §e" + percentage + "%");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    private Material convertAchievementMaterial(Achievement.Material mat, boolean unlocked) {
        if (!unlocked) {
            return Material.GRAY_DYE;
        }

        switch (mat) {
            case BOOK:
                return Material.BOOK;
            case DIAMOND:
                return Material.DIAMOND;
            case EMERALD:
                return Material.EMERALD;
            case GOLD_INGOT:
                return Material.GOLD_INGOT;
            case IRON_SWORD:
                return Material.IRON_SWORD;
            case DRAGON_HEAD:
                return Material.DRAGON_HEAD;
            case NETHER_STAR:
                return Material.NETHER_STAR;
            default:
                return Material.PAPER;
        }
    }

    public static String getInventoryTitle() {
        return INVENTORY_TITLE;
    }

    public void handleClick(Player player, int slot, ItemStack clicked) {
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }
}
