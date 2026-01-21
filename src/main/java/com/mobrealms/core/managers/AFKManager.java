package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class AFKManager {

    private final MobRealmsCore plugin;
    private BukkitTask afkTask;

    // 3 minutes
    private static final long AFK_TIMEOUT = 180_000L;

    // Check every 10 seconds for faster response
    private static final long CHECK_INTERVAL = 20L * 10;

    public AFKManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    /* ===================== LIFECYCLE ===================== */

    public void start() {
        if (afkTask != null) return;

        afkTask = plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                this::checkAFKStates,
                CHECK_INTERVAL,
                CHECK_INTERVAL
        );
    }

    public void stop() {
        if (afkTask != null) {
            afkTask.cancel();
            afkTask = null;
        }
    }

    /* ===================== CORE ===================== */

    private void checkAFKStates() {
        long now = System.currentTimeMillis();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
            if (data == null) continue;

            if (!data.isAfk() && now - data.getLastActionTime() >= AFK_TIMEOUT) {
                setAfk(player, data, true);
            }
        }
    }

    /**
     * NYT navn (bruges internt / fremover)
     */
    public void markActive(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) return;

        data.updateLastActionTime();

        if (data.isAfk()) {
            setAfk(player, data, false);
        }
    }

    /**
     * GAMMELT navn – beholdt så eksisterende listeners ikke crasher
     */
    public void updateActivity(Player player) {
        markActive(player);
    }

    /* ===================== STATE ===================== */

    private void setAfk(Player player, PlayerData data, boolean afk) {
        if (data.isAfk() == afk) return;

        data.setAfk(afk);

        if (afk) {
            player.sendMessage("§7§l━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§6§lAFK §8» §7You are now marked as §eaway");
            player.sendMessage("§7Move to return!");
            player.sendMessage("§7§l━━━━━━━━━━━━━━━━━━━━━━━━━━");
            plugin.getServer().broadcast(net.kyori.adventure.text.Component.text("§6" + player.getName() + " §7is now §eAFK"));
        } else {
            player.sendMessage("§7§l━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§a§lWelcome Back §8» §7You are no longer AFK!");
            player.sendMessage("§7§l━━━━━━━━━━━━━━━━━━━━━━━━━━");
            plugin.getServer().broadcast(net.kyori.adventure.text.Component.text("§6" + player.getName() + " §7is no longer AFK"));
        }

        plugin.getTablistManager().updateTablist(player);
        plugin.getNametagManager().updateNametag(player);
    }

    /* ===================== PLAYER HOOKS ===================== */

    public void handleJoin(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) return;

        data.setAfk(false);
        data.updateLastActionTime();
    }

    public void handleQuit(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data != null) {
            data.setAfk(false);
        }
    }

    /* ===================== UTIL ===================== */

    public boolean isAfk(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        return data != null && data.isAfk();
    }

    public long getTimeUntilAfk(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) return 0;

        long remaining = AFK_TIMEOUT - (System.currentTimeMillis() - data.getLastActionTime());
        return Math.max(0, remaining);
    }
}
