package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.MobSpawner;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpawnerPlacementListener implements Listener {

    private final MobRealmsCore plugin;

    public SpawnerPlacementListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = event.getItemInHand();

        if (block.getType() != Material.SPAWNER) return;

        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }

        String mobId = getMobIdFromItem(item);
        if (mobId == null) {
            plugin.getLogger().warning(player.getName() + " placerede en spawner uden korrekt mob ID!");
            return;
        }

        // Tjek at custom mob findes
        if (plugin.getCustomMobManager().getCustomMob(mobId) == null) {
            plugin.getLogger().warning("MobId '" + mobId + "' eksisterer ikke i CustomMobManager!");
            return;
        }

        registerSpawner(player, block, mobId);
    }

    private String getMobIdFromItem(ItemStack item) {
        List<String> lore = item.getItemMeta().getLore();
        for (String line : lore) {
            if (line.startsWith(ChatColor.DARK_GRAY + "ID: ")) {
                return ChatColor.stripColor(line).replace("ID: ", "").trim();
            }
        }
        return null;
    }

    private void registerSpawner(Player player, Block block, String mobId) {
        Location loc = block.getLocation();

        MobSpawner spawner = new MobSpawner(
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ(),
                mobId
        );

        plugin.getSpawnerManager().addSpawner(spawner);
        plugin.getQuestManager().handleSpawnerPlaced(player);

        String mobName = plugin.getCustomMobManager().getCustomMob(mobId).getDisplayName();
        player.sendMessage(ChatColor.GREEN + "✓ Spawner activated! " + mobName + ChatColor.GREEN + " will spawn every 30 seconds.");
        player.sendMessage(ChatColor.GRAY + "Max 3 mobs within 16 blocks.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        MobSpawner spawner = plugin.getSpawnerManager().getSpawnerAt(block.getLocation());
        if (spawner == null) return;

        plugin.getSpawnerManager().removeSpawner(block.getLocation());

        Player player = event.getPlayer();
        if (player.hasPermission("mobrealms.mobspawner")) {
            String mobName = plugin.getCustomMobManager().getCustomMob(spawner.getMobId()).getDisplayName();
            player.sendMessage(ChatColor.YELLOW + "✓ Removed " + mobName + ChatColor.YELLOW + " spawner.");
        }
    }
}
