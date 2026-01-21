package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class MobGriefingListener implements Listener {

    private final MobRealmsCore plugin;

    public MobGriefingListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (plugin.getCustomMobManager().isCustomMob(entity)) {
            event.blockList().clear();
        }

        if (entity.getType() == EntityType.ENDER_DRAGON ||
            entity.getType() == EntityType.WITHER ||
            entity.getType() == EntityType.CREEPER) {
            if (plugin.getCustomMobManager().isCustomMob(entity)) {
                event.blockList().clear();
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (plugin.getCustomMobManager().isCustomMob(entity)) {
            event.setCancelled(true);
            return;
        }

        if (entity.getType() == EntityType.ENDERMAN ||
            entity.getType() == EntityType.ENDER_DRAGON ||
            entity.getType() == EntityType.WITHER ||
            entity.getType() == EntityType.IRON_GOLEM ||
            entity.getType() == EntityType.RAVAGER) {
            if (entity.hasMetadata("CustomMob")) {
                event.setCancelled(true);
            }
        }
    }
}
