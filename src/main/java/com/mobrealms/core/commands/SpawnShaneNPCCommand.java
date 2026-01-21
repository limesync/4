package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnShaneNPCCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public SpawnShaneNPCCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("error.player_only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("mobrealms.spawnshanenpc")) {
            plugin.getMessageManager().sendMessage(player, "command.no_permission");
            return true;
        }

        plugin.getNPCManager().spawnShaneNPC(player.getLocation());
        player.sendMessage(plugin.getMessageManager().getPrefix() + " §aShane NPC spawned at your location!");

        return true;
    }
}
