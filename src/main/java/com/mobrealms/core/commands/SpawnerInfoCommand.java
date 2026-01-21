package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.MobSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SpawnerInfoCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public SpawnerInfoCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && !sender.hasPermission("mobrealms.spawnerinfo")) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " §cYou don't have permission!");
            return true;
        }

        Collection<MobSpawner> spawners = plugin.getSpawnerManager().getAllSpawners();

        if (spawners.isEmpty()) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " §eNo spawners are currently placed.");
            sender.sendMessage("§7Place spawners using §a/mobspawner§7!");
            return true;
        }

        sender.sendMessage(plugin.getMessageManager().getPrefix() + " §6=== Spawner Info ===");
        sender.sendMessage("§7Total spawners: §e" + spawners.size());
        sender.sendMessage("");

        int index = 1;
        for (MobSpawner spawner : spawners) {
            String mobName = plugin.getCustomMobManager().getCustomMob(spawner.getMobId()).getDisplayName();
            String shortId = spawner.getId().toString().substring(0, 8);

            sender.sendMessage("§e" + index + ". " + mobName + " §8(ID: " + shortId + ")");
            sender.sendMessage("  §7Location: §f" + spawner.getWorld() + " " + spawner.getX() + ", " + spawner.getY() + ", " + spawner.getZ());
            sender.sendMessage("  §7Status: " + (spawner.isActive() ? "§aActive" : "§cInactive"));
            sender.sendMessage("  §7Interval: §f" + spawner.getSpawnInterval() + "s §7| Max Mobs: §f" + spawner.getMaxNearbyMobs() + " §7| Radius: §f" + spawner.getSpawnRadius());

            long timeSinceLastSpawn = (System.currentTimeMillis() - spawner.getLastSpawn()) / 1000;
            long timeUntilNext = Math.max(0, spawner.getSpawnInterval() - timeSinceLastSpawn);
            sender.sendMessage("  §7Next spawn in: §f" + timeUntilNext + "s");

            if (sender instanceof Player) {
                Player player = (Player) sender;
                double distance = player.getLocation().distance(spawner.getLocation(player.getWorld()));
                sender.sendMessage("  §7Distance from you: §f" + String.format("%.1f", distance) + " blocks");
            }

            sender.sendMessage("  §7Remove: §c/spawner remove " + shortId);
            sender.sendMessage("");
            index++;
        }

        return true;
    }
}
