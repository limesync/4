package com.mobrealms.core.gui;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ClassSelectionGUI {

    private final MobRealmsCore plugin;

    public ClassSelectionGUI(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        if (data.hasClass()) {
            plugin.getMessageManager().sendMessage(player, "class.already_selected");
            return;
        }

        String title = plugin.getMessageManager().getMessage("gui.class_selection.title");
        Inventory inventory = Bukkit.createInventory(null, 27, title);

        ItemStack warrior = createClassItem(Material.IRON_SWORD, "gui.class_selection.warrior.name",
                "gui.class_selection.warrior.lore");
        ItemStack mage = createClassItem(Material.ENCHANTED_BOOK, "gui.class_selection.mage.name",
                "gui.class_selection.mage.lore");
        ItemStack archer = createClassItem(Material.BOW, "gui.class_selection.archer.name",
                "gui.class_selection.archer.lore");
        ItemStack rogue = createClassItem(Material.GOLDEN_SWORD, "gui.class_selection.rogue.name",
                "gui.class_selection.rogue.lore");

        inventory.setItem(10, warrior);
        inventory.setItem(12, mage);
        inventory.setItem(14, archer);
        inventory.setItem(16, rogue);

        player.openInventory(inventory);
    }

    private ItemStack createClassItem(Material material, String namePath, String lorePath) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(plugin.getMessageManager().getMessage(namePath));
            meta.setLore(plugin.getMessageManager().getLoreList(lorePath));
            item.setItemMeta(meta);
        }

        return item;
    }

    public void handleClick(Player player, int slot) {
        String className = null;

        switch (slot) {
            case 10:
                className = "Warrior";
                break;
            case 12:
                className = "Mage";
                break;
            case 14:
                className = "Archer";
                break;
            case 16:
                className = "Rogue";
                break;
            default:
                return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        if (data.hasClass()) {
            plugin.getMessageManager().sendMessage(player, "class.already_selected");
            player.closeInventory();
            return;
        }

        data.setPlayerClass(className);
        data.setLevel(1);
        data.setXP(0);
        data.setCoins(0);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("class", className);
        plugin.getMessageManager().sendMessage(player, "class.selected", replacements);

        player.closeInventory();

        plugin.getDatabaseManager().savePlayerData(data);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getDatabaseManager().loadLocation("Spawn", null).thenAccept(spawnLocation -> {
                if (spawnLocation != null) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.teleport(spawnLocation);
                        player.sendMessage(plugin.getMessageManager().getPrefix() + " §aTeleported to spawn!");

                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            plugin.getKitManager().giveKit(player, true);
                        }, 10L);
                    });
                } else {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        plugin.getKitManager().giveKit(player, true);
                    });
                }
            });
        }, 10L);
    }
}
