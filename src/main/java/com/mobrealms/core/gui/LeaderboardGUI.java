package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardGUI {
    private final MobRealmsCore plugin;
    private static final String INVENTORY_TITLE_PREFIX = "§6§lLeaderboard";

    public enum LeaderboardType {
        LEVEL, COINS, KILLS
    }

    public LeaderboardGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, LeaderboardType type) {
        String title = getInventoryTitle(type);
        Inventory inv = Bukkit.createInventory(null, 54, title);

        fillBorders(inv);

        List<Map.Entry<UUID, PlayerData>> sorted = getSortedPlayers(type);

        // Display top 10 players in designated slots
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21};
        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            Map.Entry<UUID, PlayerData> entry = sorted.get(i);
            OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getKey());
            PlayerData data = entry.getValue();

            inv.setItem(slots[i], createPlayerHead(op, data, i + 1, type));
        }

        // Category selection buttons
        inv.setItem(45, createCategoryButton(Material.EXPERIENCE_BOTTLE, "§b§lLevels",
            LeaderboardType.LEVEL, type));
        inv.setItem(46, createCategoryButton(Material.GOLD_INGOT, "§6§lCoins",
            LeaderboardType.COINS, type));
        inv.setItem(47, createCategoryButton(Material.DIAMOND_SWORD, "§c§lKills",
            LeaderboardType.KILLS, type));

        // Info button
        inv.setItem(49, createInfoButton(type, sorted.size()));

        // Close button
        inv.setItem(53, createCloseButton());

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

    private ItemStack createPlayerHead(OfflinePlayer player, PlayerData data, int rank, LeaderboardType type) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(player);

        String rankColor = getRankColor(rank);
        String rankSymbol = getRankSymbol(rank);
        meta.setDisplayName(rankColor + rankSymbol + " #" + rank + " §f§l" + player.getName());

        List<String> lore = new ArrayList<>();
        lore.add("§8" + getRankTitle(rank));
        lore.add("");

        switch (type) {
            case LEVEL:
                lore.add("§7Level: §e§l" + data.getLevel());
                lore.add("§7Experience: §e" + formatNumber(data.getXp()));
                lore.add("");
                lore.add("§7Total Kills: §a" + formatNumber(data.getKills()));
                lore.add("§7Boss Kills: §6" + formatNumber(data.getBossKills()));
                break;
            case COINS:
                lore.add("§7Balance: §6§l" + formatNumber(data.getCoins()) + " coins");
                lore.add("§7Total Earned: §e" + formatNumber(data.getTotalCoinsEarned()) + " coins");
                lore.add("");
                lore.add("§7Level: §b" + data.getLevel());
                lore.add("§7Kills: §a" + formatNumber(data.getKills()));
                break;
            case KILLS:
                lore.add("§7Total Kills: §c§l" + formatNumber(data.getKills()));
                lore.add("§7Boss Kills: §6" + formatNumber(data.getBossKills()));
                lore.add("§7Deaths: §7" + formatNumber(data.getDeaths()));
                lore.add("");
                double kd = data.getDeaths() > 0 ? (double) data.getKills() / data.getDeaths() : data.getKills();
                lore.add("§7K/D Ratio: §e" + String.format("%.2f", kd));
                lore.add("§7Best Streak: §e" + data.getHighestKillstreak());
                break;
        }

        meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }

    private ItemStack createCategoryButton(Material material, String name, LeaderboardType buttonType, LeaderboardType currentType) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        if (buttonType == currentType) {
            lore.add("§a§l✓ Currently Viewing");
        } else {
            lore.add("§7Click to view this category");
        }
        lore.add("");

        switch (buttonType) {
            case LEVEL:
                lore.add("§7View top players by");
                lore.add("§7their experience level");
                break;
            case COINS:
                lore.add("§7View richest players");
                lore.add("§7by current balance");
                break;
            case KILLS:
                lore.add("§7View deadliest players");
                lore.add("§7by total mob kills");
                break;
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createInfoButton(LeaderboardType type, int totalPlayers) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lLeaderboard Info");

        List<String> lore = new ArrayList<>();
        lore.add("§8Statistics");
        lore.add("");
        lore.add("§7Category: §e" + getTypeName(type));
        lore.add("§7Showing: §aTop 10 Players");
        lore.add("§7Total Players: §e" + totalPlayers);
        lore.add("");
        lore.add("§7Leaderboards update in");
        lore.add("§7real-time based on");
        lore.add("§7player performance!");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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

    private List<Map.Entry<UUID, PlayerData>> getSortedPlayers(LeaderboardType type) {
        Map<UUID, PlayerData> allData = plugin.getPlayerDataManager().getAllPlayerData();

        Comparator<Map.Entry<UUID, PlayerData>> comparator;
        if (type == LeaderboardType.LEVEL) {
            comparator = (a, b) -> Integer.compare(b.getValue().getLevel(), a.getValue().getLevel());
        } else if (type == LeaderboardType.COINS) {
            comparator = (a, b) -> Long.compare(b.getValue().getCoins(), a.getValue().getCoins());
        } else {
            comparator = (a, b) -> Integer.compare(b.getValue().getKills(), a.getValue().getKills());
        }

        return allData.entrySet().stream()
            .sorted(comparator)
            .limit(10)
            .collect(Collectors.toList());
    }

    private String getRankColor(int rank) {
        switch (rank) {
            case 1: return "§6";
            case 2: return "§7";
            case 3: return "§c";
            default: return "§e";
        }
    }

    private String getRankSymbol(int rank) {
        switch (rank) {
            case 1: return "§l★";
            case 2: return "§l★";
            case 3: return "§l★";
            default: return "§l▸";
        }
    }

    private String getRankTitle(int rank) {
        switch (rank) {
            case 1: return "Champion";
            case 2: return "Runner-up";
            case 3: return "Third Place";
            default: return "Top 10";
        }
    }

    private String getTypeName(LeaderboardType type) {
        switch (type) {
            case LEVEL: return "Levels";
            case COINS: return "Coins";
            case KILLS: return "Kills";
            default: return "Unknown";
        }
    }

    private String formatNumber(long number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000.0);
        }
        return String.valueOf(number);
    }

    public static String getInventoryTitle(LeaderboardType type) {
        return INVENTORY_TITLE_PREFIX + " - " +
               (type == LeaderboardType.LEVEL ? "Levels" :
                type == LeaderboardType.COINS ? "Coins" : "Kills");
    }

    public void handleClick(Player player, int slot, ItemStack clicked, String title) {
        if (slot == 53) {
            player.closeInventory();
            return;
        }

        if (slot == 45) {
            open(player, LeaderboardType.LEVEL);
        } else if (slot == 46) {
            open(player, LeaderboardType.COINS);
        } else if (slot == 47) {
            open(player, LeaderboardType.KILLS);
        }
    }
}
