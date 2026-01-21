package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistManager {

    private final MobRealmsCore plugin;
    private LuckPerms luckPerms;

    public TablistManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        try {
            this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        } catch (Exception e) {
            plugin.getLogger().info("LuckPerms not found");
        }
        startUpdateTask();
    }

    public void updateTablist(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) return;

        String header = c("\n&b&lMOB&f&lREALMS\n");

        String className = data.hasClass() ? capitalize(data.getPlayerClass()) : "None";
        String footer = c("\n&fClass: &e" + className + " &8| &fLevel: &b" + data.getLevel() + " &8| &6Coins: &f" + formatNumber(data.getCoins()) + "\n");

        player.setPlayerListHeaderFooter(header, footer);

        String rank = getRank(player);
        String displayName = c("&8[&b" + data.getLevel() + "&8] " + rank + "&f" + player.getName());
        player.setPlayerListName(displayName);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String getRank(Player player) {
        if (luckPerms == null) return "";

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                String prefix = user.getCachedData().getMetaData().getPrefix();
                if (prefix != null && !prefix.isEmpty()) {
                    return prefix + " ";
                }
            }
        } catch (Exception e) {
        }

        return "";
    }

    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateTablist(player);
            }
        }, 20L, 40L);
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

    public void forceUpdate(Player player) {
        updateTablist(player);
    }

    public void forceUpdateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTablist(player);
        }
    }
}
