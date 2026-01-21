package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatManager {

    private final MobRealmsCore plugin;
    private LuckPerms luckPerms;

    public ChatManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        try {
            this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        } catch (Exception e) {
            plugin.getLogger().warning("LuckPerms not found! Chat formatting will be limited.");
        }
    }

    public String formatChatMessage(Player player, String message) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (data == null) {
            return c("&7" + player.getName() + "&7: " + message);
        }

        String rank = getRank(player);
        return c("&8[&b" + data.getLevel() + "&8] " + rank + "&f" + player.getName() + "&7: " + message);
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

    private String c(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }
}
