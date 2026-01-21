package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealthBarManager {
    private final MobRealmsCore plugin;
    private final Map<UUID, BossBar> healthBars = new HashMap<>();
    private final Map<UUID, BukkitTask> removeTasks = new HashMap<>();

    public HealthBarManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void start() {}

    public void stop() {
        for (BossBar bar : healthBars.values()) {
            bar.removeAll();
        }
        healthBars.clear();
        removeTasks.values().forEach(BukkitTask::cancel);
        removeTasks.clear();
    }

    public void showHealthBar(Player player, LivingEntity entity) {
        UUID uuid = player.getUniqueId();

        if (removeTasks.containsKey(uuid)) {
            removeTasks.get(uuid).cancel();
            removeTasks.remove(uuid);
        }

        BossBar bar = healthBars.get(uuid);
        if (bar == null) {
            bar = plugin.getServer().createBossBar("", BarColor.RED, BarStyle.SOLID);
            bar.addPlayer(player);
            healthBars.put(uuid, bar);
        }

        String mobName = entity.getCustomName() != null ? entity.getCustomName() : entity.getType().name();
        double health = entity.getHealth();
        double maxHealth = entity.getMaxHealth();
        double progress = Math.max(0.0, Math.min(1.0, health / maxHealth));

        bar.setTitle(mobName + " §c❤ " + (int)health + "/" + (int)maxHealth);
        bar.setProgress(progress);
        bar.setVisible(true);

        final BossBar finalBar = bar;
        BukkitTask removeTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            finalBar.setVisible(false);
            removeTasks.remove(uuid);
        }, 60L);
        removeTasks.put(uuid, removeTask);
    }

    public void removeHealthBar(Player player) {
        UUID uuid = player.getUniqueId();
        if (healthBars.containsKey(uuid)) {
            healthBars.get(uuid).removeAll();
            healthBars.remove(uuid);
        }
        if (removeTasks.containsKey(uuid)) {
            removeTasks.get(uuid).cancel();
            removeTasks.remove(uuid);
        }
    }
}
