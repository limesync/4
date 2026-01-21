package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;

public class SlimeSplitListener implements Listener {

    private final MobRealmsCore plugin;

    public SlimeSplitListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSlimeSplit(SlimeSplitEvent event) {
        Slime slime = event.getEntity();

        if (plugin.getCustomMobManager().isCustomMob(slime)) {
            event.setCancelled(true);
        }
    }
}
