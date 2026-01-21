package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final MobRealmsCore plugin;

    public PlayerJoinListener(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getPlayerDataManager().loadPlayerData(player);
        plugin.getQuestManager().loadPlayerQuests(player.getUniqueId());
        plugin.getBaldricQuestManager().loadPlayerQuests(player.getUniqueId());
        plugin.getAchievementManager().loadPlayerAchievements(player.getUniqueId());

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getScoreboardManager().createScoreboard(player);
            plugin.getTablistManager().updateTablist(player);

            plugin.getAFKManager().handleJoin(player);
            plugin.getDailyRewardManager().handleLogin(player);

            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
            if (data != null && data.hasClass()) {
                plugin.getQuestManager().handleLevelUp(player, data.getLevel());
                plugin.getBaldricQuestManager().handleLevelUp(player, data.getLevel());
            }
        }, 10L);
    }
}
