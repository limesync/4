package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public HelpCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(colorize("&8&m                                                   "));
        sender.sendMessage(colorize("&a&lMobRealms Help"));
        sender.sendMessage("");

        List<String> commands = new ArrayList<>();

        if (sender.hasPermission("mobrealms.admin")) {
            commands.add("&6Setup Commands:");
            commands.add("&e/setlobby &7- Set the lobby spawn location");
            commands.add("&e/setspawn <realm> &7- Set spawn location for a realm");
            commands.add("&e/spawnadventurenpc &7- Spawn the Begin Adventure NPC");
            commands.add("");
            commands.add("&6Player Management:");
            commands.add("&e/setlevel <player> <level> &7- Set player level");
            commands.add("&e/setxp <player> <amount> &7- Set player XP");
            commands.add("&e/addxp <player> <amount> &7- Add XP to player");
            commands.add("");
            commands.add("&6Economy:");
            commands.add("&e/setcoins <player> <amount> &7- Set player coins");
            commands.add("&e/addcoins <player> <amount> &7- Add coins to player");
            commands.add("&e/givecoins <player> <amount> &7- Give coins to player");
            commands.add("&e/removecoins <player> <amount> &7- Remove coins from player");
            commands.add("");
            commands.add("&6Class Management:");
            commands.add("&e/setclass <player> <class> &7- Set player class");
            commands.add("&e/resetclass <player> &7- Reset player class");
        } else {
            commands.add("&6Player Commands:");
            commands.add("&e/kit &7- Claim your class kit (24h cooldown)");
            commands.add("");
            commands.add("&7Use the &aBegin Adventure NPC &7to select your class!");
        }

        for (String line : commands) {
            sender.sendMessage(colorize(line));
        }

        sender.sendMessage("");
        sender.sendMessage(colorize("&8&m                                                   "));

        return true;
    }

    private String colorize(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }
}
