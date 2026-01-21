package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    private final MobRealmsCore plugin;

    public EntityDamageByEntityListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (damager instanceof Player) {
            Player player = (Player) damager;
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

            if (data == null) {
                event.setCancelled(true);
                return;
            }

            if (!data.hasClass()) {
                event.setCancelled(true);
                plugin.getMessageManager().sendMessage(player, "class.locked");
                return;
            }

            data.updateLastActionTime();
            plugin.getAFKManager().updateActivity(player);
            plugin.getCombatLogManager().enterCombat(player);

            if (victim instanceof LivingEntity && !(victim instanceof Player)) {
                plugin.getHealthBarManager().showHealthBar(player, (LivingEntity) victim);
            }

            if (victim instanceof Player) {
                Player target = (Player) victim;
                plugin.getCombatLogManager().enterCombat(target);
                plugin.getKillstreakManager().resetStreak(player);
            }
        }

        if (victim instanceof Player) {
            Player player = (Player) victim;
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

            if (data == null) {
                event.setCancelled(true);
                return;
            }

            if (!data.hasClass()) {
                event.setCancelled(true);
                return;
            }

            data.updateLastActionTime();
            plugin.getAFKManager().updateActivity(player);
            plugin.getCombatLogManager().enterCombat(player);
            plugin.getKillstreakManager().resetStreak(player);
        }
    }
}
