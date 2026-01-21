package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    private final MobRealmsCore plugin;

    public PlayerDamageListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data == null) {
            event.setCancelled(true);
            return;
        }

        if (!data.hasClass()) {
            event.setCancelled(true);
            plugin.getMessageManager().sendMessage(player, "class.locked");
        }
    }
}
