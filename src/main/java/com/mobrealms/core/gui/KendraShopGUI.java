package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KendraShopGUI {
    private final MobRealmsCore plugin;
    private static final String INVENTORY_TITLE = "§e§lKendra's Building Materials";
    private static final int ITEMS_PER_PAGE = 28;

    public KendraShopGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        openPage(player, 1);
    }

    public void openPage(Player player, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, INVENTORY_TITLE + " §7(Page " + page + ")");

        fillBorders(inv);

        List<ShopItemData> allItems = getAllItems();
        int totalPages = (int) Math.ceil((double) allItems.size() / ITEMS_PER_PAGE);
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allItems.size());

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int slotIndex = 0;

        for (int i = startIndex; i < endIndex && slotIndex < slots.length; i++) {
            ShopItemData item = allItems.get(i);
            inv.setItem(slots[slotIndex++], createShopItem(item.material, item.name, item.price, item.reqLevel, item.amount));
        }

        if (page > 1) {
            inv.setItem(48, createPreviousButton());
        }
        inv.setItem(49, createCloseButton());
        if (page < totalPages) {
            inv.setItem(50, createNextButton());
        }

        player.openInventory(inv);
    }

    private void fillBorders(Inventory inv) {
        ItemStack glass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glass);
            inv.setItem(i + 45, glass);
        }

        for (int i = 9; i < 45; i += 9) {
            inv.setItem(i, glass);
            inv.setItem(i + 8, glass);
        }
    }

    private ItemStack createShopItem(Material material, String name, long price, int reqLevel, int amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add("§7Building material");
        lore.add("");
        lore.add("§7Price: §6" + formatNumber(price) + " coins");
        lore.add("§7Required Level: §b" + reqLevel);
        if (amount > 1) {
            lore.add("§7Amount: §e" + amount);
        }
        lore.add("");
        lore.add("§e§l→ Click to purchase!");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createPreviousButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§l← Previous Page");
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to go to previous page");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNextButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lNext Page →");
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to go to next page");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
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

    private String formatNumber(long number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000.0);
        }
        return String.valueOf(number);
    }

    public static String getInventoryTitle() {
        return INVENTORY_TITLE;
    }

    private int getCurrentPage(String title) {
        if (!title.contains("Page")) return 1;
        String pageStr = title.substring(title.indexOf("Page") + 5, title.length() - 1);
        try {
            return Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public void handleClick(Player player, int slot, ItemStack clicked) {
        String title = player.getOpenInventory().getTitle();
        int currentPage = getCurrentPage(title);

        if (slot == 48) {
            openPage(player, currentPage - 1);
            return;
        } else if (slot == 49) {
            player.closeInventory();
            return;
        } else if (slot == 50) {
            openPage(player, currentPage + 1);
            return;
        }

        if (clicked == null || !clicked.hasItemMeta()) return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int slotIndex = -1;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) {
                slotIndex = i;
                break;
            }
        }

        if (slotIndex == -1) return;

        List<ShopItemData> allItems = getAllItems();
        int itemIndex = (currentPage - 1) * ITEMS_PER_PAGE + slotIndex;

        if (itemIndex >= allItems.size()) return;

        ShopItemData itemData = allItems.get(itemIndex);

        if (data.getLevel() < itemData.reqLevel) {
            player.sendMessage(plugin.getMessageManager().getPrefix() + " §cYou need to be level §e" + itemData.reqLevel + " §cto buy this!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        if (data.getCoins() < itemData.price) {
            player.sendMessage(plugin.getMessageManager().getPrefix() + " §cYou need §6" + formatNumber(itemData.price) + " coins §cto buy this!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        data.removeCoins(itemData.price);
        plugin.getDatabaseManager().savePlayerData(data);

        ItemStack purchasedItem = new ItemStack(itemData.material, itemData.amount);
        player.getInventory().addItem(purchasedItem);

        player.sendMessage(plugin.getMessageManager().getPrefix() + " §aSuccessfully purchased §f" + clicked.getItemMeta().getDisplayName() + " §afor §6" + formatNumber(itemData.price) + " coins§a!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        openPage(player, currentPage);
    }

    private List<ShopItemData> getAllItems() {
        List<ShopItemData> items = new ArrayList<>();

        items.add(new ShopItemData(Material.OAK_PLANKS, "§fOak Planks x64", 80, 1, 64));
        items.add(new ShopItemData(Material.SPRUCE_PLANKS, "§fSpruce Planks x64", 80, 1, 64));
        items.add(new ShopItemData(Material.BIRCH_PLANKS, "§fBirch Planks x64", 80, 1, 64));
        items.add(new ShopItemData(Material.JUNGLE_PLANKS, "§fJungle Planks x64", 100, 2, 64));
        items.add(new ShopItemData(Material.ACACIA_PLANKS, "§fAcacia Planks x64", 100, 2, 64));
        items.add(new ShopItemData(Material.DARK_OAK_PLANKS, "§fDark Oak Planks x64", 100, 2, 64));
        items.add(new ShopItemData(Material.CRIMSON_PLANKS, "§fCrimson Planks x64", 200, 5, 64));

        items.add(new ShopItemData(Material.COBBLESTONE, "§fCobblestone x64", 40, 1, 64));
        items.add(new ShopItemData(Material.STONE, "§fStone x64", 60, 1, 64));
        items.add(new ShopItemData(Material.STONE_BRICKS, "§fStone Bricks x64", 100, 2, 64));
        items.add(new ShopItemData(Material.ANDESITE, "§fAndesite x64", 80, 1, 64));
        items.add(new ShopItemData(Material.DIORITE, "§fDiorite x64", 80, 1, 64));
        items.add(new ShopItemData(Material.GRANITE, "§fGranite x64", 80, 1, 64));
        items.add(new ShopItemData(Material.BLACKSTONE, "§fBlackstone x64", 150, 3, 64));

        items.add(new ShopItemData(Material.WHITE_WOOL, "§fWhite Wool x64", 100, 1, 64));
        items.add(new ShopItemData(Material.RED_WOOL, "§fRed Wool x64", 100, 1, 64));
        items.add(new ShopItemData(Material.BLUE_WOOL, "§fBlue Wool x64", 100, 1, 64));
        items.add(new ShopItemData(Material.GREEN_WOOL, "§fGreen Wool x64", 100, 1, 64));
        items.add(new ShopItemData(Material.YELLOW_WOOL, "§fYellow Wool x64", 100, 1, 64));
        items.add(new ShopItemData(Material.BLACK_WOOL, "§fBlack Wool x64", 100, 1, 64));
        items.add(new ShopItemData(Material.ORANGE_WOOL, "§fOrange Wool x64", 120, 2, 64));

        items.add(new ShopItemData(Material.GLASS, "§fGlass x64", 120, 1, 64));
        items.add(new ShopItemData(Material.WHITE_STAINED_GLASS, "§fWhite Glass x64", 150, 2, 64));
        items.add(new ShopItemData(Material.BLUE_STAINED_GLASS, "§fBlue Glass x64", 150, 2, 64));
        items.add(new ShopItemData(Material.RED_STAINED_GLASS, "§fRed Glass x64", 150, 2, 64));
        items.add(new ShopItemData(Material.GREEN_STAINED_GLASS, "§fGreen Glass x64", 150, 2, 64));
        items.add(new ShopItemData(Material.BLACK_STAINED_GLASS, "§fBlack Glass x64", 150, 2, 64));
        items.add(new ShopItemData(Material.GLASS_PANE, "§fGlass Panes x64", 100, 2, 64));

        items.add(new ShopItemData(Material.TORCH, "§fTorches x64", 40, 1, 64));
        items.add(new ShopItemData(Material.LANTERN, "§fLanterns x16", 150, 2, 16));
        items.add(new ShopItemData(Material.GLOWSTONE, "§fGlowstone x32", 200, 3, 32));
        items.add(new ShopItemData(Material.SEA_LANTERN, "§fSea Lanterns x16", 250, 4, 16));
        items.add(new ShopItemData(Material.REDSTONE_LAMP, "§fRedstone Lamps x16", 300, 5, 16));
        items.add(new ShopItemData(Material.SHROOMLIGHT, "§fShroomplights x16", 350, 6, 16));

        items.add(new ShopItemData(Material.OAK_DOOR, "§fOak Door x8", 80, 1, 8));
        items.add(new ShopItemData(Material.OAK_FENCE, "§fOak Fence x32", 100, 1, 32));
        items.add(new ShopItemData(Material.LADDER, "§fLadder x16", 80, 1, 16));
        items.add(new ShopItemData(Material.CRAFTING_TABLE, "§fCrafting Table x4", 50, 1, 4));
        items.add(new ShopItemData(Material.CHEST, "§fChest x4", 200, 1, 4));
        items.add(new ShopItemData(Material.FURNACE, "§fFurnace x4", 100, 1, 4));
        items.add(new ShopItemData(Material.BARREL, "§fBarrel x4", 250, 2, 4));
        items.add(new ShopItemData(Material.BOOKSHELF, "§fBookshelf x8", 300, 3, 8));

        return items;
    }

    private static class ShopItemData {
        Material material;
        String name;
        long price;
        int reqLevel;
        int amount;

        ShopItemData(Material material, String name, long price, int reqLevel, int amount) {
            this.material = material;
            this.name = name;
            this.price = price;
            this.reqLevel = reqLevel;
            this.amount = amount;
        }
    }
}
