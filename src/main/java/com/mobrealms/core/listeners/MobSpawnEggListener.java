package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MobSpawnEggListener implements Listener {

    private final MobRealmsCore plugin;

    public MobSpawnEggListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseEgg(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }

        if (item.getType().toString().contains("SPAWNER")) {
            return;
        }

        List<String> lore = item.getItemMeta().getLore();
        String mobId = null;

        for (String line : lore) {
            if (line.startsWith(ChatColor.DARK_GRAY + "ID: ")) {
                mobId = ChatColor.stripColor(line).replace("ID: ", "").trim();
                break;
            }
        }

        if (mobId != null && plugin.getCustomMobManager().getCustomMob(mobId) != null) {
            if (event.getClickedBlock() != null) {
                plugin.getCustomMobManager().spawnCustomMob(mobId, event.getClickedBlock().getLocation().add(0, 1, 0));
                item.setAmount(item.getAmount() - 1);
                event.getPlayer().sendMessage(ChatColor.GREEN + "Spawned custom mob!");
                event.setCancelled(true);
            }
        }
    }
}
