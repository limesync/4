package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.gui.BaldricQuestsGUI;
import com.mobrealms.core.gui.ClassSelectionGUI;
import com.mobrealms.core.gui.QuestsGUI;
import com.mobrealms.core.gui.ShaneShopGUI;
import com.mobrealms.core.gui.KendraShopGUI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractListener implements Listener {

    private final MobRealmsCore plugin;
    private final ClassSelectionGUI classSelectionGUI;
    private final QuestsGUI questsGUI;
    private final BaldricQuestsGUI baldricQuestsGUI;
    private final ShaneShopGUI shaneShopGUI;
    private final KendraShopGUI kendraShopGUI;

    public PlayerInteractListener(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.classSelectionGUI = new ClassSelectionGUI(plugin);
        this.questsGUI = new QuestsGUI(plugin);
        this.baldricQuestsGUI = new BaldricQuestsGUI(plugin);
        this.shaneShopGUI = new ShaneShopGUI(plugin);
        this.kendraShopGUI = new KendraShopGUI(plugin);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        plugin.getAFKManager().updateActivity(player);

        if (plugin.getNPCManager().isBeginAdventureNPC(entity)) {
            event.setCancelled(true);
            classSelectionGUI.open(player);
        } else if (plugin.getNPCManager().isBaldricNPC(entity)) {
            event.setCancelled(true);
            baldricQuestsGUI.open(player);
        } else if (plugin.getNPCManager().isQuestNPC(entity)) {
            event.setCancelled(true);
            questsGUI.open(player);
        } else if (plugin.getNPCManager().isShaneNPC(entity)) {
            event.setCancelled(true);
            shaneShopGUI.open(player);
        } else if (plugin.getNPCManager().isKendraNPC(entity)) {
            event.setCancelled(true);
            kendraShopGUI.open(player);
        }
    }
}
