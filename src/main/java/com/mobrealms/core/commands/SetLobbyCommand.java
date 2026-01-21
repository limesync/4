package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbyCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public SetLobbyCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("mobrealms.admin")) {
            plugin.getMessageManager().sendMessage(player, "command.no_permission");
            return true;
        }

        plugin.getRealmManager().setLobbySpawn(player.getLocation());
        plugin.getMessageManager().sendMessage(player, "command.lobby_set");

        return true;
    }
}
