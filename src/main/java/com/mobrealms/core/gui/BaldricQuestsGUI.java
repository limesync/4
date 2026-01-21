package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.BaldricQuest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BaldricQuestsGUI {
    private final MobRealmsCore plugin;
    private static final String INVENTORY_TITLE = "§e§lBaldric's Beginner Quests";

    public BaldricQuestsGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, INVENTORY_TITLE);

        ItemStack glass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
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

        List<BaldricQuest> quests = plugin.getBaldricQuestManager().getPlayerQuestsSorted(player.getUniqueId());
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

        for (int i = 0; i < Math.min(quests.size(), slots.length); i++) {
            BaldricQuest quest = quests.get(i);
            inv.setItem(slots[i], createQuestItem(quest));
        }

        inv.setItem(49, createCloseButton());
        player.openInventory(inv);
    }

    private ItemStack createQuestItem(BaldricQuest quest) {
        Material material;
        if (quest.isClaimed()) {
            material = Material.LIME_STAINED_GLASS_PANE;
        } else if (quest.isCompleted()) {
            material = Material.EMERALD;
        } else {
            material = Material.PAPER;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(quest.getName());

        List<String> lore = new ArrayList<>();
        lore.add("§7" + quest.getDescription());
        lore.add("");
        lore.add("§7Progress: §e" + quest.getProgress() + "§7/§e" + quest.getTargetAmount());

        int percentage = (int) quest.getProgressPercent();
        String progressBar = createProgressBar(percentage);
        lore.add(progressBar);

        lore.add("");
        lore.add("§7Rewards:");
        lore.add("  §6+" + quest.getCoinReward() + " coins");
        lore.add("  §b+" + quest.getXpReward() + " XP");
        lore.add("");

        if (quest.isClaimed()) {
            lore.add("§a§l✓ CLAIMED");
        } else if (quest.isCompleted()) {
            lore.add("§e§l→ Click to claim rewards!");
        } else {
            lore.add("§7§oIn Progress...");
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    private String createProgressBar(int percentage) {
        int bars = 20;
        int filled = (int) ((percentage / 100.0) * bars);

        StringBuilder progressBar = new StringBuilder("§7[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                progressBar.append("§a■");
            } else {
                progressBar.append("§7■");
            }
        }
        progressBar.append("§7] §e").append(percentage).append("%");

        return progressBar.toString();
    }

    private ItemStack createCloseButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lClose");
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to close");
        meta.setLore(lore);
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

        List<BaldricQuest> quests = plugin.getBaldricQuestManager().getPlayerQuestsSorted(player.getUniqueId());
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

        int questIndex = -1;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) {
                questIndex = i;
                break;
            }
        }

        if (questIndex == -1 || questIndex >= quests.size()) return;

        BaldricQuest quest = quests.get(questIndex);

        if (quest.isCompleted() && !quest.isClaimed()) {
            boolean claimed = plugin.getBaldricQuestManager().claimQuest(player, quest.getId());
            if (claimed) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                open(player);
            }
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
        }
    }
}
