package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StatsGUI {
    private final MobRealmsCore plugin;
    private static final String INVENTORY_TITLE = "§6§lYour Statistics";

    public StatsGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, INVENTORY_TITLE);
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (data == null) {
            player.sendMessage("§cFailed to load your data!");
            return;
        }

        fillBorders(inv);

        // Level & XP Stats (Top Left)
        inv.setItem(11, createLevelItem(data));

        // Coins Stats (Top Center)
        inv.setItem(13, createCoinsItem(data));

        // Combat Stats (Top Right)
        inv.setItem(15, createCombatItem(data));

        // Killstreak Stats (Bottom Left)
        inv.setItem(29, createKillstreakItem(data));

        // Additional Stats (Bottom Center)
        inv.setItem(31, createGeneralStatsItem(data));

        // Player Head (Bottom Right)
        inv.setItem(33, createPlayerSummaryItem(player, data));

        // Close Button
        inv.setItem(49, createCloseButton());

        player.openInventory(inv);
    }

    private void fillBorders(Inventory inv) {
        ItemStack glass = createGlassPane();

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glass);
            inv.setItem(i + 45, glass);
        }

        for (int i = 9; i < 45; i += 9) {
            inv.setItem(i, glass);
            inv.setItem(i + 8, glass);
        }
    }

    private ItemStack createGlassPane() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        return glass;
    }

    private ItemStack createLevelItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lLevel & Experience");

        List<String> lore = new ArrayList<>();
        lore.add("§8Progression");
        lore.add("");
        lore.add("§7Current Level: §e§l" + data.getLevel());

        long currentXp = data.getXp();
        long requiredXp = plugin.getLevelManager().getXPRequired(data.getLevel() + 1);
        double percent = currentXp / (double) requiredXp;

        lore.add("§7Experience: §e" + formatNumber(currentXp) + " §8/ §e" + formatNumber(requiredXp));
        lore.add("");
        lore.add(getProgressBar(percent));
        lore.add("§7Progress: §e" + String.format("%.1f", percent * 100) + "%");
        lore.add("");
        lore.add("§7Next Level: §a" + (data.getLevel() + 1));

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCoinsItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lCoins & Economy");

        List<String> lore = new ArrayList<>();
        lore.add("§8Financial Statistics");
        lore.add("");
        lore.add("§7Current Balance:");
        lore.add("  §8▸ §6" + formatNumber(data.getCoins()) + " coins");
        lore.add("");
        lore.add("§7Total Earned:");
        lore.add("  §8▸ §e" + formatNumber(data.getTotalCoinsEarned()) + " coins");
        lore.add("");

        long spent = data.getTotalCoinsEarned() - data.getCoins();
        lore.add("§7Total Spent:");
        lore.add("  §8▸ §c" + formatNumber(spent) + " coins");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCombatItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lCombat Statistics");

        List<String> lore = new ArrayList<>();
        lore.add("§8Battle Performance");
        lore.add("");
        lore.add("§7Total Kills:");
        lore.add("  §8▸ §a" + formatNumber(data.getKills()));
        lore.add("");
        lore.add("§7Boss Kills:");
        lore.add("  §8▸ §6" + formatNumber(data.getBossKills()));
        lore.add("");
        lore.add("§7Deaths:");
        lore.add("  §8▸ §c" + formatNumber(data.getDeaths()));
        lore.add("");

        double kd = data.getDeaths() > 0 ? (double) data.getKills() / data.getDeaths() : data.getKills();
        lore.add("§7K/D Ratio:");
        lore.add("  §8▸ §e" + String.format("%.2f", kd));

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createKillstreakItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lKillstreak");

        List<String> lore = new ArrayList<>();
        lore.add("§8Consecutive Kills");
        lore.add("");
        lore.add("§7Current Streak:");
        lore.add("  §8▸ §e§l" + data.getCurrentKillstreak());
        lore.add("");
        lore.add("§7Best Streak:");
        lore.add("  §8▸ §6§l" + data.getHighestKillstreak());
        lore.add("");

        if (data.getCurrentKillstreak() > 0) {
            lore.add("§a§l✓ §7Streak Active!");
        } else {
            lore.add("§7Kill mobs to start a streak");
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createGeneralStatsItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d§lGeneral Statistics");

        List<String> lore = new ArrayList<>();
        lore.add("§8Additional Information");
        lore.add("");
        lore.add("§7Account Status:");
        lore.add("  §8▸ §aActive");
        lore.add("");
        lore.add("§7Total Achievements:");
        lore.add("  §8▸ §e" + calculateTotalAchievements(data));
        lore.add("");
        lore.add("§7Rank Progress:");
        lore.add("  §8▸ " + getRankName(data.getLevel()));

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPlayerSummaryItem(Player player, PlayerData data) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName("§a§l" + player.getName());

        List<String> lore = new ArrayList<>();
        lore.add("§8Player Profile");
        lore.add("");
        lore.add("§7Level: §e§l" + data.getLevel());
        lore.add("§7Balance: §6" + formatNumber(data.getCoins()) + " coins");
        lore.add("");
        lore.add("§7Status: §aOnline");
        lore.add("§7Rank: " + getRankName(data.getLevel()));

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCloseButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lClose");
        item.setItemMeta(meta);
        return item;
    }

    private String getProgressBar(double percent) {
        int bars = 20;
        int filled = (int) Math.min(bars, Math.round(bars * percent));

        StringBuilder bar = new StringBuilder("§8[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("§a▮");
            } else {
                bar.append("§7▯");
            }
        }
        bar.append("§8]");

        return bar.toString();
    }

    private String formatNumber(long number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000.0);
        }
        return String.valueOf(number);
    }

    private int calculateTotalAchievements(PlayerData data) {
        int count = 0;
        if (data.getLevel() >= 10) count++;
        if (data.getKills() >= 100) count++;
        if (data.getBossKills() >= 10) count++;
        if (data.getHighestKillstreak() >= 10) count++;
        return count;
    }

    private String getRankName(int level) {
        if (level >= 50) return "§4§lLegend";
        if (level >= 40) return "§c§lMaster";
        if (level >= 30) return "§6§lExpert";
        if (level >= 20) return "§e§lAdept";
        if (level >= 10) return "§a§lNovice";
        return "§7Beginner";
    }

    public static String getInventoryTitle() {
        return INVENTORY_TITLE;
    }

    public void handleClick(Player player, int slot) {
        if (slot == 49) {
            player.closeInventory();
        }
    }
}
