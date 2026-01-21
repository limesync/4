package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SetSpawnCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public SetSpawnCommand(MobRealmsCore plugin) {
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

        if (args.length < 1) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("usage", "/setspawn <realm>");
            plugin.getMessageManager().sendMessage(player, "command.usage", replacements);
            return true;
        }

        String realm = args[0];
        plugin.getRealmManager().setRealmSpawn(realm, player.getLocation());

        Map<String, String> replacements = new HashMap<>();
        replacements.put("realm", realm);
        plugin.getMessageManager().sendMessage(player, "realm.spawn_set", replacements);

        return true;
    }
}
