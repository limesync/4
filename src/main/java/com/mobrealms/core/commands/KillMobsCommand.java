package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class KillMobsCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public KillMobsCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && !sender.hasPermission("mobrealms.killmobs")) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " §cYou don't have permission to use this command!");
            return true;
        }

        World targetWorld = null;

        if (args.length > 0) {
            targetWorld = Bukkit.getWorld(args[0]);
            if (targetWorld == null) {
                sender.sendMessage(plugin.getMessageManager().getPrefix() + " §cWorld '" + args[0] + "' not found!");
                return true;
            }
        } else if (sender instanceof Player) {
            targetWorld = ((Player) sender).getWorld();
        } else {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " §cPlease specify a world name!");
            return true;
        }

        int customMobsKilled = 0;
        int normalMobsKilled = 0;

        for (Entity entity : targetWorld.getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                LivingEntity livingEntity = (LivingEntity) entity;

                if (plugin.getCustomMobManager().isCustomMob(livingEntity)) {
                    plugin.getCustomMobManager().removeSpawnedMob(entity.getUniqueId());
                    entity.remove();
                    customMobsKilled++;
                } else if (livingEntity instanceof Monster) {
                    entity.remove();
                    normalMobsKilled++;
                }
            }
        }

        int totalKilled = customMobsKilled + normalMobsKilled;

        sender.sendMessage(plugin.getMessageManager().getPrefix() + " §aKilled " + totalKilled + " mobs in world '" + targetWorld.getName() + "'!");
        sender.sendMessage("  §7Custom mobs: §e" + customMobsKilled + " §7| Normal mobs: §e" + normalMobsKilled);

        return true;
    }
}
