package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.gui.LeaderboardGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardCommand implements CommandExecutor {

    private final MobRealmsCore plugin;

    public LeaderboardCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        new LeaderboardGUI(plugin).open(player, LeaderboardGUI.LeaderboardType.LEVEL);
        return true;
    }
}
