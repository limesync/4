package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NametagManager {

    private final MobRealmsCore plugin;
    private LuckPerms luckPerms;

    public NametagManager(MobRealmsCore plugin) {
        this.plugin = plugin;

        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                this.luckPerms = LuckPermsProvider.get();
            } catch (Exception e) {
                plugin.getLogger().info("LuckPerms not found");
            }
        }
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateNametag(player);
            }
        }, 0L, 40L);
    }

    public void stop() {
    }

    public void updateNametag(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            String teamName = "nt_" + target.getUniqueId().toString().substring(0, 14);
            Team team = scoreboard.getTeam(teamName);

            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }

            if (!team.hasEntry(target.getName())) {
                team.addEntry(target.getName());
            }

            PlayerData data = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
            if (data == null) continue;

            String prefix = getPrefix(target, data);
            if (prefix.length() > 16) prefix = prefix.substring(0, 16);

            team.setPrefix(c(prefix));
            team.setColor(org.bukkit.ChatColor.WHITE);
        }
    }

    private String getPrefix(Player player, PlayerData data) {
        StringBuilder prefix = new StringBuilder();

        if (plugin.getCombatLogManager().isInCombat(player)) {
            prefix.append("&c⚔ ");
        }

        prefix.append("&7[&b").append(data.getLevel()).append("&7] ");

        String rank = getRank(player);
        if (!rank.isEmpty()) {
            prefix.append(rank).append(" ");
        }

        return prefix.toString();
    }

    private String getRank(Player player) {
        if (luckPerms == null) return "";

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                String prefix = user.getCachedData().getMetaData().getPrefix();
                if (prefix != null && !prefix.isEmpty()) {
                    return prefix;
                }
            }
        } catch (Exception e) {
        }

        return "";
    }

    private String c(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

    public void forceUpdate(Player player) {
        updateNametag(player);
    }

    public void forceUpdateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateNametag(player);
        }
    }
}
