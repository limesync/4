package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.gui.AchievementsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AchievementsCommand implements CommandExecutor {

    private final MobRealmsCore plugin;
    private final AchievementsGUI achievementsGUI;

    public AchievementsCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.achievementsGUI = new AchievementsGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("error.player_only"));
            return true;
        }

        Player player = (Player) sender;
        achievementsGUI.open(player);
        return true;
    }

    public AchievementsGUI getAchievementsGUI() {
        return achievementsGUI;
    }
}
