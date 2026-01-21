package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final MobRealmsCore plugin;

    public PlayerChatListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String formattedMessage = plugin.getChatManager().formatChatMessage(event.getPlayer(), event.getMessage());
        event.setFormat(formattedMessage);
    }
}
