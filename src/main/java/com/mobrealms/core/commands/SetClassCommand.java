package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetClassCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public SetClassCommand(MobRealmsCore plugin) {
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
            replacements.put("usage", "/setclass <player> <class>");
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

        String className = args[1];
        List<String> validClasses = plugin.getConfigManager().getClasses();

        if (!validClasses.contains(className)) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                    plugin.getMessageManager().getMessage("command.invalid_class"));
            return true;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        if (data == null) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " §cPlayer data not loaded!");
            return true;
        }

        data.setPlayerClass(className);

        sender.sendMessage(plugin.getMessageManager().getPrefix() + " §aSet " + target.getName() + "'s class to " + className + "!");

        return true;
    }
}
