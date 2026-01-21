package com.mobrealms.core.models;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItem {

    private final String id;
    private final String name;
    private final Material material;
    private final int price;
    private final int requiredLevel;
    private final ShopCategory category;
    private final Map<Enchantment, Integer> enchantments;
    private final List<String> lore;

    public ShopItem(String id, String name, Material material, int price, int requiredLevel,
                   ShopCategory category, Map<Enchantment, Integer> enchantments, List<String> lore) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.price = price;
        this.requiredLevel = requiredLevel;
        this.category = category;
        this.enchantments = enchantments != null ? enchantments : new HashMap<>();
        this.lore = lore != null ? lore : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public int getPrice() {
        return price;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public ShopCategory getCategory() {
        return category;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);

            List<String> fullLore = new ArrayList<>();
            fullLore.addAll(lore);
            fullLore.add("");
            fullLore.add("§7Price: §6" + price + " coins");
            fullLore.add("§7Required Level: §e" + requiredLevel);

            if (!enchantments.isEmpty()) {
                fullLore.add("");
                fullLore.add("§dEnchantments:");
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    fullLore.add("§7- " + formatEnchantment(entry.getKey()) + " " + entry.getValue());
                }
            }

            meta.setLore(fullLore);
            item.setItemMeta(meta);
        }

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }

        return item;
    }

    private String formatEnchantment(Enchantment enchantment) {
        String name = enchantment.getKey().getKey();
        return name.substring(0, 1).toUpperCase() + name.substring(1).replace("_", " ");
    }

    public enum ShopCategory {
        WEAPONS("Weapons", Material.DIAMOND_SWORD),
        ARMOR("Armor", Material.DIAMOND_CHESTPLATE),
        TOOLS("Tools", Material.DIAMOND_PICKAXE),
        FOOD("Food", Material.COOKED_BEEF),
        BLOCKS("Blocks", Material.STONE),
        CLASSES("Classes", Material.BOOK),
        SPECIAL("Special Items", Material.NETHER_STAR);

        private final String displayName;
        private final Material icon;

        ShopCategory(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getIcon() {
            return icon;
        }
    }
}
