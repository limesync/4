package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SetCoinsCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public SetCoinsCommand(MobRealmsCore plugin) {
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
            replacements.put("usage", "/setcoins <player> <amount>");
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

        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                    plugin.getMessageManager().getMessage("command.invalid_amount"));
            return true;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        if (data == null) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " §cPlayer data not loaded!");
            return true;
        }

        data.setCoins(amount);

        plugin.getPlayerDataManager().forceUpdate(target.getUniqueId());
        plugin.getScoreboardManager().forceUpdate(target);
        plugin.getTablistManager().forceUpdate(target);
        plugin.getActionBarManager().forceUpdate(target);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("player", target.getName());
        replacements.put("amount", String.valueOf(amount));
        sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                plugin.getMessageManager().getMessage("coins.set", replacements));

        return true;
    }
}
