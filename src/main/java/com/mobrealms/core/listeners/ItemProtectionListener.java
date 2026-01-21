package com.mobrealms.core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemProtectionListener implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (isSoulbound(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot drop soulbound items!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && isSoulbound(item)) {
            if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isSoulbound(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        for (String line : meta.getLore()) {
            if (ChatColor.stripColor(line).contains("Soulbound")) {
                return true;
            }
        }
        return false;
    }
}
