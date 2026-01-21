package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final MobRealmsCore plugin;
    private final Map<UUID, Scoreboard> scoreboards;
    private LuckPerms luckPerms;

    public ScoreboardManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.scoreboards = new HashMap<>();
        try {
            this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        } catch (Exception e) {
            plugin.getLogger().info("LuckPerms not found");
        }
        startUpdateTask();
    }

    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("mobrealms", "dummy", c("&b&lMOB&f&lREALMS"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data != null) {
            updateScoreboardLines(objective, player, data);
        }

        player.setScoreboard(scoreboard);
        scoreboards.put(player.getUniqueId(), scoreboard);
    }

    private void updateScoreboardLines(Objective objective, Player player, PlayerData data) {
        clearScoreboard(objective);

        int line = 15;
        objective.getScore(c("")).setScore(line--);

        String rank = getRank(player);
        if (!rank.isEmpty()) {
            objective.getScore(c(" &fRank: " + rank)).setScore(line--);
        }

        String className = data.hasClass() ? capitalize(data.getPlayerClass()) : "None";
        objective.getScore(c(" &fClass: &e" + className)).setScore(line--);

        objective.getScore(c(" &fLevel: &b" + data.getLevel())).setScore(line--);

        objective.getScore(c("  ")).setScore(line--);

        objective.getScore(c(" &fKills: &a" + formatNumber(data.getKills()))).setScore(line--);
        objective.getScore(c(" &fDeaths: &c" + data.getDeaths())).setScore(line--);

        double kdr = data.getDeaths() > 0 ? (double) data.getKills() / data.getDeaths() : data.getKills();
        objective.getScore(c(" &fKDR: &7" + String.format("%.2f", kdr))).setScore(line--);

        objective.getScore(c("   ")).setScore(line--);

        objective.getScore(c(" &6Coins: &f" + formatNumber(data.getCoins()))).setScore(line--);

        if (plugin.getCombatLogManager().isInCombat(player)) {
            objective.getScore(c("    ")).setScore(line--);
            int remaining = plugin.getCombatLogManager().getRemainingCombatTime(player);
            objective.getScore(c(" &c⚔ Combat: &f" + remaining + "s")).setScore(line--);
        }

        objective.getScore(c("     ")).setScore(line--);
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

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            createScoreboard(player);
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) return;

        Objective objective = scoreboard.getObjective("mobrealms");
        if (objective != null) {
            updateScoreboardLines(objective, player, data);
        }
    }

    public void removeScoreboard(Player player) {
        scoreboards.remove(player.getUniqueId());
    }

    public void forceUpdate(Player player) {
        updateScoreboard(player);
    }

    public void forceUpdateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateScoreboard(player);
            }
        }, 20L, 20L);
    }

    private void clearScoreboard(Objective objective) {
        for (String entry : objective.getScoreboard().getEntries()) {
            objective.getScoreboard().resetScores(entry);
        }
    }

    private String formatNumber(long number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%,d", number);
        }
        return String.valueOf(number);
    }

    private String c(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
