package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.Quest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class QuestsGUI {
    private final MobRealmsCore plugin;
    private static final String INVENTORY_TITLE = "§6§lQuests";

    public QuestsGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, INVENTORY_TITLE);

        fillBorders(inv);

        Map<String, Quest> quests = plugin.getQuestManager().getPlayerQuests(player.getUniqueId());
        List<Quest> sortedQuests = new ArrayList<>(quests.values());
        sortedQuests.sort((q1, q2) -> {
            if (q1.isCompleted() != q2.isCompleted()) {
                return q1.isCompleted() ? -1 : 1;
            }
            return q1.getDifficulty().compareTo(q2.getDifficulty());
        });

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int index = 0;

        for (Quest quest : sortedQuests) {
            if (index >= slots.length) break;
            inv.setItem(slots[index], createQuestItem(quest));
            index++;
        }

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

    private ItemStack createQuestItem(Quest quest) {
        Material material = getQuestMaterial(quest);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String statusColor = quest.isClaimed() ? "§7" : quest.isCompleted() ? "§a" : "§e";
        meta.setDisplayName(statusColor + "§l" + ChatColor.stripColor(quest.getName()));

        List<String> lore = new ArrayList<>();
        lore.add("§8" + getDifficultyString(quest.getDifficulty()));
        lore.add("");
        lore.add("§7" + quest.getDescription());
        lore.add("");
        lore.add(getProgressBar(quest));
        lore.add("§7Progress: §e" + quest.getProgress() + " §8/§e " + quest.getTargetAmount());
        lore.add("");
        lore.add("§7Rewards:");
        lore.add("  §8▸ §6" + formatNumber(quest.getCoinReward()) + " coins");
        lore.add("  §8▸ §b" + formatNumber(quest.getXpReward()) + " XP");
        lore.add("");

        if (quest.isClaimed()) {
            lore.add("§a§l✓ CLAIMED");
        } else if (quest.isCompleted()) {
            lore.add("§e§l→ Click to claim rewards!");
        } else {
            lore.add("§7Status: §eIn Progress");
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    private Material getQuestMaterial(Quest quest) {
        if (quest.isClaimed()) return Material.GRAY_DYE;
        if (quest.isCompleted()) return Material.LIME_DYE;

        switch (quest.getDifficulty()) {
            case EASY: return Material.GREEN_DYE;
            case MEDIUM: return Material.YELLOW_DYE;
            case HARD: return Material.ORANGE_DYE;
            case EXTREME: return Material.RED_DYE;
            default: return Material.PAPER;
        }
    }

    private String getDifficultyString(Quest.QuestDifficulty difficulty) {
        switch (difficulty) {
            case EASY: return "§a✦ Easy";
            case MEDIUM: return "§e✦✦ Medium";
            case HARD: return "§6✦✦✦ Hard";
            case EXTREME: return "§c✦✦✦✦ Extreme";
            default: return "§7Unknown";
        }
    }

    private String getProgressBar(Quest quest) {
        int bars = 20;
        double percent = quest.getProgress() / (double) quest.getTargetAmount();
        int filled = (int) Math.min(bars, Math.round(bars * percent));

        StringBuilder bar = new StringBuilder("§8[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("§a▮");
            } else {
                bar.append("§7▯");
            }
        }
        bar.append("§8] §e").append(String.format("%.1f", percent * 100)).append("%");

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

    private ItemStack createCloseButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lClose");
        item.setItemMeta(meta);
        return item;
    }

    public static String getInventoryTitle() {
        return INVENTORY_TITLE;
    }

    public void handleClick(Player player, int slot, ItemStack clicked) {
        if (slot == 49) {
            player.closeInventory();
            return;
        }

        if (clicked == null || !clicked.hasItemMeta()) return;

        String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        Map<String, Quest> quests = plugin.getQuestManager().getPlayerQuests(player.getUniqueId());

        for (Quest quest : quests.values()) {
            if (ChatColor.stripColor(quest.getName()).equals(displayName)) {
                if (quest.isCompleted() && !quest.isClaimed()) {
                    if (plugin.getQuestManager().claimQuest(player, quest.getId())) {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                        open(player);
                    }
                }
                return;
            }
        }
    }
}
