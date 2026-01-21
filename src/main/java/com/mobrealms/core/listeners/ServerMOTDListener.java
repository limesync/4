package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerMOTDListener implements Listener {

    public ServerMOTDListener(MobRealmsCore plugin) {
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        String motd = c("            &b&lMOB&f&lREALMS &7| &f1.20\n        &7Conquer the Realms &a&lNEW!");
        event.setMotd(motd);
    }

    private String c(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }
}
