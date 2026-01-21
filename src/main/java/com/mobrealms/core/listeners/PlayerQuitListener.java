package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final MobRealmsCore plugin;

    public PlayerQuitListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getCombatLogManager().handleLogout(player);
        plugin.getAFKManager().handleQuit(player);
        plugin.getHealthBarManager().removeHealthBar(player);
        plugin.getScoreboardManager().removeScoreboard(player);
        plugin.getQuestManager().unloadPlayerQuests(player.getUniqueId());
        plugin.getBaldricQuestManager().unloadPlayerQuests(player.getUniqueId());
        plugin.getAchievementManager().unloadPlayerAchievements(player.getUniqueId());
        plugin.getPlayerDataManager().unloadPlayerData(player);
    }
}
