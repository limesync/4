package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import com.mobrealms.core.models.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private final MobRealmsCore plugin;

    public ShopGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§6§lShop Menu");

        inv.setItem(10, createCategoryItem(ShopItem.ShopCategory.WEAPONS));
        inv.setItem(11, createCategoryItem(ShopItem.ShopCategory.ARMOR));
        inv.setItem(12, createCategoryItem(ShopItem.ShopCategory.TOOLS));
        inv.setItem(13, createCategoryItem(ShopItem.ShopCategory.FOOD));
        inv.setItem(14, createCategoryItem(ShopItem.ShopCategory.BLOCKS));
        inv.setItem(15, createCategoryItem(ShopItem.ShopCategory.SPECIAL));

        inv.setItem(22, createCategoryItem(ShopItem.ShopCategory.CLASSES));

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        inv.setItem(44, createInfoItem(data));

        player.openInventory(inv);
    }

    public void openCategory(Player player, ShopItem.ShopCategory category) {
        List<ShopItem> items = plugin.getShopManager().getItemsByCategory(category);
        int size = Math.min(54, ((items.size() + 8) / 9) * 9 + 9);
        Inventory inv = Bukkit.createInventory(null, size, "§6§l" + category.getDisplayName());

        for (int i = 0; i < items.size() && i < size - 9; i++) {
            inv.setItem(i, items.get(i).createItemStack());
        }

        inv.setItem(size - 5, createBackButton());

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        inv.setItem(size - 1, createInfoItem(data));

        player.openInventory(inv);
    }

    private ItemStack createCategoryItem(ShopItem.ShopCategory category) {
        ItemStack item = new ItemStack(category.getIcon());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e§l" + category.getDisplayName());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Click to browse");
            lore.add("§7" + category.getDisplayName().toLowerCase());
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lBack");
            List<String> lore = new ArrayList<>();
            lore.add("§7Return to shop menu");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createInfoItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6§lYour Balance");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Coins: §6" + data.getCoins());
            lore.add("§7Level: §e" + data.getLevel());
            lore.add("§7Class: §b" + (data.getPlayerClass() != null ? data.getPlayerClass() : "None"));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public void handleClick(Player player, int slot, ItemStack clicked, String title) {
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (title.equals("§6§lShop Menu")) {
            handleMainMenuClick(player, slot);
        } else if (title.startsWith("§6§l")) {
            handleCategoryClick(player, clicked, title);
        }
    }

    private void handleMainMenuClick(Player player, int slot) {
        ShopItem.ShopCategory category = null;

        switch (slot) {
            case 10:
                category = ShopItem.ShopCategory.WEAPONS;
                break;
            case 11:
                category = ShopItem.ShopCategory.ARMOR;
                break;
            case 12:
                category = ShopItem.ShopCategory.TOOLS;
                break;
            case 13:
                category = ShopItem.ShopCategory.FOOD;
                break;
            case 14:
                category = ShopItem.ShopCategory.BLOCKS;
                break;
            case 15:
                category = ShopItem.ShopCategory.SPECIAL;
                break;
            case 22:
                category = ShopItem.ShopCategory.CLASSES;
                break;
        }

        if (category != null) {
            openCategory(player, category);
        }
    }

    private void handleCategoryClick(Player player, ItemStack clicked, String title) {
        if (clicked.getType() == Material.ARROW) {
            openMainMenu(player);
            return;
        }

        if (clicked.getType() == Material.GOLD_INGOT) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String itemName = meta.getDisplayName();
        ShopItem shopItem = findShopItemByName(itemName);

        if (shopItem != null) {
            purchaseItem(player, shopItem);
        }
    }

    private ShopItem findShopItemByName(String displayName) {
        for (ShopItem item : plugin.getShopManager().getAllItems()) {
            if (item.getName().equals(displayName)) {
                return item;
            }
        }
        return null;
    }

    private void purchaseItem(Player player, ShopItem shopItem) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (data.getLevel() < shopItem.getRequiredLevel()) {
            player.sendMessage("§c§lShop §8» §cYou need level " + shopItem.getRequiredLevel() + " to purchase this item!");
            player.closeInventory();
            return;
        }

        if (data.getCoins() < shopItem.getPrice()) {
            player.sendMessage("§c§lShop §8» §cYou don't have enough coins! Need " + shopItem.getPrice() + " coins.");
            player.closeInventory();
            return;
        }

        if (shopItem.getCategory() == ShopItem.ShopCategory.CLASSES) {
            handleClassPurchase(player, shopItem, data);
            return;
        }

        data.removeCoins(shopItem.getPrice());
        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());

        ItemStack item = shopItem.createItemStack();
        if (shopItem.getId().contains("steak")) {
            item.setAmount(16);
        } else if (shopItem.getId().contains("golden_apple") && !shopItem.getId().contains("enchanted")) {
            item.setAmount(4);
        } else if (shopItem.getCategory() == ShopItem.ShopCategory.BLOCKS) {
            if (shopItem.getId().contains("obsidian")) {
                item.setAmount(16);
            } else if (shopItem.getId().contains("quartz") || shopItem.getId().contains("glowstone")) {
                item.setAmount(32);
            } else {
                item.setAmount(64);
            }
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = shopItem.getLore();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
        player.sendMessage("§a§lShop §8» §aYou purchased " + shopItem.getName() + " §afor §6" + shopItem.getPrice() + " coins§a!");
        player.closeInventory();

        openCategory(player, shopItem.getCategory());
    }

    private void handleClassPurchase(Player player, ShopItem shopItem, PlayerData data) {
        String className = null;

        if (shopItem.getId().equals("warrior_unlock")) {
            className = "Warrior";
        } else if (shopItem.getId().equals("mage_unlock")) {
            className = "Mage";
        } else if (shopItem.getId().equals("archer_unlock")) {
            className = "Archer";
        } else if (shopItem.getId().equals("rogue_unlock")) {
            className = "Rogue";
        }

        if (className == null) {
            return;
        }

        if (className.equals(data.getPlayerClass())) {
            player.sendMessage("§c§lShop §8» §cYou already own this class!");
            player.closeInventory();
            return;
        }

        data.removeCoins(shopItem.getPrice());
        data.setPlayerClass(className);
        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());

        player.sendMessage("§a§lShop §8» §aYou unlocked the " + shopItem.getName() + " §aclass for §6" + shopItem.getPrice() + " coins§a!");
        player.closeInventory();
    }
}
