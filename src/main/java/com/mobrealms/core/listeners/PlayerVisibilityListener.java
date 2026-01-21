package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerVisibilityListener implements Listener {

    private final MobRealmsCore plugin;

    public PlayerVisibilityListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerVisibility(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        updatePlayerVisibility(event.getPlayer());
    }

    private void updatePlayerVisibility(Player player) {
        String lobbyWorld = plugin.getConfigManager().getLobbyWorld();

        if (lobbyWorld == null) {
            return;
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Player other : plugin.getServer().getOnlinePlayers()) {
                if (player.equals(other)) {
                    continue;
                }

                if (player.getWorld().getName().equals(lobbyWorld)) {
                    player.hidePlayer(plugin, other);
                } else {
                    player.showPlayer(plugin, other);
                }

                if (other.getWorld().getName().equals(lobbyWorld)) {
                    other.hidePlayer(plugin, player);
                } else {
                    other.showPlayer(plugin, player);
                }
            }
        }, 5L);
    }
}
