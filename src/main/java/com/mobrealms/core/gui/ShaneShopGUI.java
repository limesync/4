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
import java.util.List;

public class ShaneShopGUI {
    private final MobRealmsCore plugin;
    private static final String INVENTORY_TITLE = "§c§lShane's Combat Armory";
    private static final int ITEMS_PER_PAGE = 28;

    public ShaneShopGUI(MobRealmsCore plugin) {
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
            inv.setItem(slots[slotIndex++], createShopItem(item.material, item.name, item.price, item.reqLevel, item.description, item.amount));
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
        ItemStack glass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
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

    private ItemStack createShopItem(Material material, String name, long price, int reqLevel, String description, int amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add("§7" + description);
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

        if (itemData.isArmorSet) {
            giveArmorSet(player, itemData.material);
        } else {
            ItemStack purchasedItem = new ItemStack(itemData.material, itemData.amount);
            player.getInventory().addItem(purchasedItem);
        }

        player.sendMessage(plugin.getMessageManager().getPrefix() + " §aSuccessfully purchased §f" + clicked.getItemMeta().getDisplayName() + " §afor §6" + formatNumber(itemData.price) + " coins§a!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        openPage(player, currentPage);
    }

    private void giveArmorSet(Player player, Material chestplateMaterial) {
        String materialType = chestplateMaterial.toString().replace("_CHESTPLATE", "");

        player.getInventory().addItem(new ItemStack(Material.valueOf(materialType + "_HELMET")));
        player.getInventory().addItem(new ItemStack(Material.valueOf(materialType + "_CHESTPLATE")));
        player.getInventory().addItem(new ItemStack(Material.valueOf(materialType + "_LEGGINGS")));
        player.getInventory().addItem(new ItemStack(Material.valueOf(materialType + "_BOOTS")));
    }

    private List<ShopItemData> getAllItems() {
        List<ShopItemData> items = new ArrayList<>();

        items.add(new ShopItemData(Material.WOODEN_SWORD, "§fWooden Sword", 50, 1, "§7Basic starter weapon", 1, false));
        items.add(new ShopItemData(Material.STONE_SWORD, "§fStone Sword", 150, 3, "§7Reliable combat weapon", 1, false));
        items.add(new ShopItemData(Material.IRON_SWORD, "§fIron Sword", 500, 5, "§7Strong iron blade", 1, false));
        items.add(new ShopItemData(Material.DIAMOND_SWORD, "§bDiamond Sword", 2000, 10, "§7Premium sharp sword", 1, false));
        items.add(new ShopItemData(Material.NETHERITE_SWORD, "§4Netherite Sword", 5000, 15, "§7Ultimate weapon", 1, false));

        items.add(new ShopItemData(Material.WOODEN_AXE, "§fWooden Axe", 50, 1, "§7Basic tool", 1, false));
        items.add(new ShopItemData(Material.STONE_AXE, "§fStone Axe", 120, 2, "§7Sturdy axe", 1, false));
        items.add(new ShopItemData(Material.IRON_AXE, "§fIron Axe", 400, 4, "§7Powerful axe", 1, false));
        items.add(new ShopItemData(Material.DIAMOND_AXE, "§bDiamond Axe", 1800, 8, "§7Premium tool", 1, false));
        items.add(new ShopItemData(Material.BOW, "§fBow", 300, 3, "§7Standard ranged weapon", 1, false));
        items.add(new ShopItemData(Material.CROSSBOW, "§fCrossbow", 800, 7, "§7Powerful crossbow", 1, false));
        items.add(new ShopItemData(Material.ARROW, "§fArrows x64", 100, 1, "§7Stack of arrows", 64, false));

        items.add(new ShopItemData(Material.LEATHER_CHESTPLATE, "§fLeather Armor Set", 200, 2, "§7Basic protection", 1, true));
        items.add(new ShopItemData(Material.CHAINMAIL_CHESTPLATE, "§fChainmail Armor Set", 600, 4, "§7Good defense", 1, true));
        items.add(new ShopItemData(Material.IRON_CHESTPLATE, "§fIron Armor Set", 1500, 8, "§7Strong protection", 1, true));
        items.add(new ShopItemData(Material.DIAMOND_CHESTPLATE, "§bDiamond Armor Set", 4000, 12, "§7Premium defense", 1, true));
        items.add(new ShopItemData(Material.NETHERITE_CHESTPLATE, "§4Netherite Armor Set", 10000, 20, "§7Ultimate protection", 1, true));

        items.add(new ShopItemData(Material.SHIELD, "§fShield", 400, 5, "§7Block incoming attacks", 1, false));
        items.add(new ShopItemData(Material.GOLDEN_APPLE, "§6Golden Apple x5", 500, 3, "§7Healing boost", 5, false));
        items.add(new ShopItemData(Material.ENCHANTED_GOLDEN_APPLE, "§6§lEnchanted Golden Apple", 2000, 10, "§7Powerful regeneration", 1, false));
        items.add(new ShopItemData(Material.TOTEM_OF_UNDYING, "§e§lTotem of Undying", 5000, 15, "§7Second chance at life", 1, false));
        items.add(new ShopItemData(Material.ENDER_PEARL, "§dEnder Pearls x16", 800, 8, "§7Teleport to safety", 16, false));

        items.add(new ShopItemData(Material.COOKED_BEEF, "§fSteak x32", 200, 1, "§7Restore hunger", 32, false));
        items.add(new ShopItemData(Material.COOKED_PORKCHOP, "§fPorkchop x32", 200, 1, "§7Restore hunger", 32, false));
        items.add(new ShopItemData(Material.POTION, "§fHealing Potion x5", 400, 5, "§7Instant health", 5, false));
        items.add(new ShopItemData(Material.EXPERIENCE_BOTTLE, "§aXP Bottles x16", 600, 7, "§7Experience boost", 16, false));
        items.add(new ShopItemData(Material.TNT, "§cTNT x8", 500, 6, "§7Explosive power", 8, false));
        items.add(new ShopItemData(Material.ELYTRA, "§5§lElytra", 15000, 25, "§7Wings of flight", 1, false));

        return items;
    }

    private static class ShopItemData {
        Material material;
        String name;
        long price;
        int reqLevel;
        String description;
        int amount;
        boolean isArmorSet;

        ShopItemData(Material material, String name, long price, int reqLevel, String description, int amount, boolean isArmorSet) {
            this.material = material;
            this.name = name;
            this.price = price;
            this.reqLevel = reqLevel;
            this.description = description;
            this.amount = amount;
            this.isArmorSet = isArmorSet;
        }
    }
}
