package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SetLevelCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public SetLevelCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mobrealms.admin")) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                    plugin.getMessageManager().getMessage("command.no_permission"));
            return true;
        }

        if (args.length < 2) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("usage", "/setlevel <player> <level>");
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                    plugin.getMessageManager().getMessage("command.usage", replacements));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                    plugin.getMessageManager().getMessage("command.player_not_found"));
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                    plugin.getMessageManager().getMessage("command.invalid_amount"));
            return true;
        }

        plugin.getLevelManager().setLevel(target, level);

        sender.sendMessage(plugin.getMessageManager().getPrefix() + " §aSet " + target.getName() + "'s level to " + level + "!");

        return true;
    }
}
