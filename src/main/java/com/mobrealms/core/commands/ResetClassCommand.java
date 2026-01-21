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

public class ResetClassCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public ResetClassCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

                if (data == null || !data.hasClass()) {
                    sender.sendMessage(plugin.getMessageManager().getPrefix() + " §cYou don't have a class!");
                    return true;
                }

                if (!plugin.getKitManager().canClaimKit(player)) {
                    long remainingCooldown = plugin.getKitManager().getRemainingCooldown(player);
                    String timeLeft = plugin.getKitManager().formatTime(remainingCooldown);
                    sender.sendMessage(plugin.getMessageManager().getPrefix() +
                            " §cYou cannot reset your class while your kit is on cooldown! Time remaining: §e" + timeLeft);
                    return true;
                }

                data.setPlayerClass(null);
                data.setLevel(1);
                data.setXP(0);
                data.setLastKitClaim(0);

                sender.sendMessage(plugin.getMessageManager().getPrefix() + " §aYour class has been reset!");
                return true;
            } else {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("usage", "/resetclass <player>");
                sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                        plugin.getMessageManager().getMessage("command.usage", replacements));
                return true;
            }
        }

        if (!sender.hasPermission("mobrealms.admin")) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                    plugin.getMessageManager().getMessage("command.no_permission"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " " +
                    plugin.getMessageManager().getMessage("command.player_not_found"));
            return true;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        if (data == null) {
            sender.sendMessage(plugin.getMessageManager().getPrefix() + " §cPlayer data not loaded!");
            return true;
        }

        data.setPlayerClass(null);
        data.setLevel(1);
        data.setXP(0);
        data.setLastKitClaim(0);

        sender.sendMessage(plugin.getMessageManager().getPrefix() + " §aReset " + target.getName() + "'s class and removed kit cooldown!");
        target.sendMessage(plugin.getMessageManager().getPrefix() + " §aYour class has been reset by an admin!");

        return true;
    }
}
