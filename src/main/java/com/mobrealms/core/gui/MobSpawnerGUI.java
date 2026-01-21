package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class MobSpawnerGUI {

    private final MobRealmsCore plugin;
    private static final String MAIN_MENU_TITLE = ChatColor.DARK_RED + "Mob Spawner Menu";

    public MobSpawnerGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, MAIN_MENU_TITLE);

        ItemStack easy = createTierItem(Material.LIME_WOOL, "§a§lEasy Tier", "§7Levels 1-5", "§8Click to browse");
        ItemStack medium = createTierItem(Material.YELLOW_WOOL, "§e§lMedium Tier", "§7Levels 6-10", "§8Click to browse");
        ItemStack hard = createTierItem(Material.ORANGE_WOOL, "§6§lHard Tier", "§7Levels 12-16", "§8Click to browse");
        ItemStack expert = createTierItem(Material.RED_WOOL, "§c§lExpert Tier", "§7Levels 18-28", "§8Click to browse");
        ItemStack legendary = createTierItem(Material.PURPLE_WOOL, "§5§lLegendary Tier", "§7Levels 30-50", "§8Click to browse");

        inventory.setItem(20, easy);
        inventory.setItem(21, medium);
        inventory.setItem(22, hard);
        inventory.setItem(23, expert);
        inventory.setItem(24, legendary);

        ItemStack info = createInfoItem();
        inventory.setItem(49, info);

        player.openInventory(inventory);
    }

    public void openTierMenu(Player player, String tier) {
        String title = ChatColor.DARK_RED + tier + " Tier Mobs";
        Inventory inventory = Bukkit.createInventory(null, 54, title);

        Map<String, CustomMob> allMobs = plugin.getCustomMobManager().getAllCustomMobs();
        List<Map.Entry<String, CustomMob>> sortedMobs = allMobs.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().getLevel()))
                .collect(Collectors.toList());

        int minLevel = getTierMinLevel(tier);
        int maxLevel = getTierMaxLevel(tier);

        int slot = 0;
        for (Map.Entry<String, CustomMob> entry : sortedMobs) {
            CustomMob mob = entry.getValue();
            if (mob.getLevel() >= minLevel && mob.getLevel() <= maxLevel) {
                if (slot >= 45) break;

                ItemStack eggItem = createMobEgg(mob, entry.getKey());
                ItemStack spawnerItem = createMobSpawner(mob, entry.getKey());

                inventory.setItem(slot, eggItem);
                inventory.setItem(slot + 9, spawnerItem);

                slot++;
            }
        }

        ItemStack back = createBackButton();
        inventory.setItem(49, back);

        player.openInventory(inventory);
    }

    private int getTierMinLevel(String tier) {
        switch (tier.toLowerCase()) {
            case "easy": return 1;
            case "medium": return 6;
            case "hard": return 12;
            case "expert": return 18;
            case "legendary": return 30;
            default: return 1;
        }
    }

    private int getTierMaxLevel(String tier) {
        switch (tier.toLowerCase()) {
            case "easy": return 5;
            case "medium": return 10;
            case "hard": return 16;
            case "expert": return 28;
            case "legendary": return 50;
            default: return 100;
        }
    }

    private ItemStack createTierItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createInfoItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "§lHow to Use");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Select a difficulty tier");
            lore.add(ChatColor.GRAY + "to view available mobs.");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Top Row: " + ChatColor.WHITE + "Spawn Eggs");
            lore.add(ChatColor.YELLOW + "Bottom Row: " + ChatColor.WHITE + "Spawners");
            lore.add("");
            lore.add(ChatColor.GREEN + "Spawn Eggs: " + ChatColor.GRAY + "Single use");
            lore.add(ChatColor.GREEN + "Spawners: " + ChatColor.GRAY + "Continuous spawning");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "« Back to Main Menu");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createMobEgg(CustomMob mob, String mobId) {
        Material eggMaterial = getMobEggMaterial(mob.getEntityType().name());
        ItemStack item = new ItemStack(eggMaterial);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', mob.getDisplayName() + " §7Egg"));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━");
            lore.add(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + mob.getLevel());
            lore.add(ChatColor.RED + "Health: " + ChatColor.WHITE + (int)mob.getHealth() + " HP");
            lore.add(ChatColor.AQUA + "XP Reward: " + ChatColor.WHITE + mob.getXPReward());
            lore.add(ChatColor.GOLD + "Coin Reward: " + ChatColor.WHITE + mob.getCoinReward());
            lore.add(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━");
            lore.add("");
            lore.add(ChatColor.GREEN + "» Click to get spawn egg!");
            lore.add(ChatColor.GRAY + "Right-click to spawn");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createMobSpawner(CustomMob mob, String mobId) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', mob.getDisplayName() + " §7Spawner"));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━");
            lore.add(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + mob.getLevel());
            lore.add(ChatColor.RED + "Health: " + ChatColor.WHITE + (int)mob.getHealth() + " HP");
            lore.add(ChatColor.AQUA + "XP Reward: " + ChatColor.WHITE + mob.getXPReward());
            lore.add(ChatColor.GOLD + "Coin Reward: " + ChatColor.WHITE + mob.getCoinReward());
            lore.add(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━");
            lore.add("");
            lore.add(ChatColor.LIGHT_PURPLE + "Spawn Rate: " + ChatColor.WHITE + "1 mob/30s");
            lore.add(ChatColor.LIGHT_PURPLE + "Max Mobs: " + ChatColor.WHITE + "3 nearby");
            lore.add("");
            lore.add(ChatColor.GREEN + "» Click to get spawner!");
            lore.add(ChatColor.GRAY + "Place to activate");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private Material getMobEggMaterial(String entityTypeName) {
        try {
            return Material.valueOf(entityTypeName + "_SPAWN_EGG");
        } catch (IllegalArgumentException e) {
            return Material.EGG;
        }
    }

    public void handleClick(Player player, int slot, ItemStack clickedItem, String inventoryTitle) {
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = clickedItem.getItemMeta().getDisplayName();

        if (inventoryTitle.equals(MAIN_MENU_TITLE)) {
            if (displayName.contains("Easy")) {
                openTierMenu(player, "Easy");
            } else if (displayName.contains("Medium")) {
                openTierMenu(player, "Medium");
            } else if (displayName.contains("Hard")) {
                openTierMenu(player, "Hard");
            } else if (displayName.contains("Expert")) {
                openTierMenu(player, "Expert");
            } else if (displayName.contains("Legendary")) {
                openTierMenu(player, "Legendary");
            }
            return;
        }

        if (displayName.contains("« Back")) {
            openMainMenu(player);
            return;
        }

        Map<String, CustomMob> mobs = plugin.getCustomMobManager().getAllCustomMobs();
        for (Map.Entry<String, CustomMob> entry : mobs.entrySet()) {
            CustomMob mob = entry.getValue();
            String mobNameEgg = ChatColor.translateAlternateColorCodes('&', mob.getDisplayName() + " §7Egg");
            String mobNameSpawner = ChatColor.translateAlternateColorCodes('&', mob.getDisplayName() + " §7Spawner");

            if (displayName.equals(mobNameEgg)) {
                ItemStack giveItem = createMobEggItem(entry.getKey(), mob);
                player.getInventory().addItem(giveItem);
                player.sendMessage(ChatColor.GREEN + "✓ Received " + mob.getDisplayName() + ChatColor.GREEN + " Spawn Egg!");
                player.closeInventory();
                return;
            } else if (displayName.equals(mobNameSpawner)) {
                ItemStack giveItem = createMobSpawnerItem(entry.getKey(), mob);
                player.getInventory().addItem(giveItem);
                player.sendMessage(ChatColor.GREEN + "✓ Received " + mob.getDisplayName() + ChatColor.GREEN + " Spawner!");
                player.closeInventory();
                return;
            }
        }
    }

    private ItemStack createMobEggItem(String mobId, CustomMob mob) {
        Material eggMaterial = getMobEggMaterial(mob.getEntityType().name());
        ItemStack item = new ItemStack(eggMaterial);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', mob.getDisplayName() + " §7Egg"));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Right-click to spawn!");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + mob.getLevel());
            lore.add(ChatColor.AQUA + "XP: " + ChatColor.WHITE + mob.getXPReward());
            lore.add(ChatColor.GOLD + "Coins: " + ChatColor.WHITE + mob.getCoinReward());
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "ID: " + mobId);

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createMobSpawnerItem(String mobId, CustomMob mob) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', mob.getDisplayName() + " §7Spawner"));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Place to activate!");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + mob.getLevel());
            lore.add(ChatColor.AQUA + "XP: " + ChatColor.WHITE + mob.getXPReward());
            lore.add(ChatColor.GOLD + "Coins: " + ChatColor.WHITE + mob.getCoinReward());
            lore.add("");
            lore.add(ChatColor.LIGHT_PURPLE + "Rate: " + ChatColor.WHITE + "1 mob/30s");
            lore.add(ChatColor.LIGHT_PURPLE + "Max: " + ChatColor.WHITE + "3 nearby mobs");
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "ID: " + mobId);

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}
