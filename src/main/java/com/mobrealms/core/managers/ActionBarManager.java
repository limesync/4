package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarManager {

    private final MobRealmsCore plugin;
    private BukkitTask updateTask;

    public ActionBarManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void start() {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateActionBar(player);
            }
        }, 0L, 10L);
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel();
        }
    }

    private void updateActionBar(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        String actionBar = getActionBarText(player, data);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colorize(actionBar)));
    }

    private String getActionBarText(Player player, PlayerData data) {
        if (plugin.getCombatLogManager().isInCombat(player)) {
            int remaining = plugin.getCombatLogManager().getRemainingCombatTime(player);
            return "&c⚔ Combat: &f" + remaining + "s &8| &7Level &b" + data.getLevel() + " &8| &6" + formatNumber(data.getCoins()) + " coins";
        }

        int killstreak = data.getCurrentKillstreak();
        if (killstreak >= 5) {
            if (killstreak >= 20) {
                return "&5&l★ GODLIKE " + killstreak + " ★ &8| &7Level &b" + data.getLevel();
            } else if (killstreak >= 15) {
                return "&4&l★ UNSTOPPABLE " + killstreak + " ★ &8| &7Level &b" + data.getLevel();
            } else if (killstreak >= 10) {
                return "&c&l★ RAMPAGE " + killstreak + " ★ &8| &7Level &b" + data.getLevel();
            } else {
                return "&6★ Killstreak: " + killstreak + " ★ &8| &7Level &b" + data.getLevel();
            }
        }

        long xpRequired = plugin.getLevelManager().getXPRequired(data.getLevel() + 1);
        long currentXP = data.getXP();
        double progress = (double) currentXP / xpRequired;
        String progressBar = createProgressBar(progress, 20);

        return "&7Level &b" + data.getLevel() + " &8" + progressBar + " &b" + formatNumber(currentXP) + "&7/&f" + formatNumber(xpRequired) + " &8| &6" + formatNumber(data.getCoins()) + " coins";
    }
    private String createProgressBar(double progress, int bars) {
        int filled = (int) Math.min(bars, Math.round(bars * progress));
        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("&a▮");
            } else {
                bar.append("&7▯");
            }
        }
        bar.append("&8]");

        return colorize(bar.toString());
    }

    private String formatNumber(long number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000.0);
        }
        return String.valueOf(number);
    }

    private String colorize(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

    public void forceUpdate(Player player) {
        updateActionBar(player);
    }

    public void forceUpdateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateActionBar(player);
        }
    }
}
