package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.gui.StatsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private final MobRealmsCore plugin;
    private final StatsGUI statsGUI;

    public StatsCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.statsGUI = new StatsGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        statsGUI.open(player);
        return true;
    }
}
