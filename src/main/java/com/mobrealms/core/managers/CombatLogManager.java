package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLogManager {
    private final MobRealmsCore plugin;
    private final Map<UUID, BukkitTask> combatTasks = new HashMap<>();
    private final Map<UUID, Long> combatEndTimes = new HashMap<>();
    private final int COMBAT_DURATION = 15;

    public int getRemainingCombatTime(Player player) {
        UUID uuid = player.getUniqueId();
        if (!isInCombat(player)) return 0;

        Long endTime = combatEndTimes.get(uuid);
        if (endTime == null) return 0;

        long remaining = (endTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, (int) remaining);
    }

    public int getCombatDuration() {
        return COMBAT_DURATION;
    }

    public CombatLogManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void start() {}

    public void stop() {
        combatTasks.values().forEach(BukkitTask::cancel);
        combatTasks.clear();
        combatEndTimes.clear();
    }

    public void enterCombat(Player player) {
        UUID uuid = player.getUniqueId();
        boolean wasInCombat = combatTasks.containsKey(uuid);

        if (wasInCombat) {
            combatTasks.get(uuid).cancel();
        }

        long endTime = System.currentTimeMillis() + (COMBAT_DURATION * 1000L);
        combatEndTimes.put(uuid, endTime);

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            combatTasks.remove(uuid);
            combatEndTimes.remove(uuid);
        }, COMBAT_DURATION * 20L);
        combatTasks.put(uuid, task);
    }

    public void handleLogout(Player player) {
        UUID uuid = player.getUniqueId();
        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
            combatTasks.remove(uuid);
            combatEndTimes.remove(uuid);
            player.setHealth(0);
        }
    }

    public void handleDeath(Player player) {
        UUID uuid = player.getUniqueId();
        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
            combatTasks.remove(uuid);
            combatEndTimes.remove(uuid);
        }
    }

    public boolean isInCombat(Player player) {
        return combatTasks.containsKey(player.getUniqueId());
    }
}
