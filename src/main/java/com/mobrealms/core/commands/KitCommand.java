package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public KitCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("error.player_only"));
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (data == null || !data.hasClass()) {
            player.sendMessage(plugin.getMessageManager().getMessage("kit.no_class"));
            return true;
        }

        if (!plugin.getKitManager().canClaimKit(player)) {
            long remainingCooldown = plugin.getKitManager().getRemainingCooldown(player);
            String formattedTime = plugin.getKitManager().formatTime(remainingCooldown);

            player.sendMessage(plugin.getMessageManager().getMessage("kit.cooldown")
                    .replace("{time}", formattedTime));
            return true;
        }

        plugin.getKitManager().giveKit(player);

        return true;
    }
}
