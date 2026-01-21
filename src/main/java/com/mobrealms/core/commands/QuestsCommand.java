package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.gui.QuestsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestsCommand implements CommandExecutor {

    private final MobRealmsCore plugin;
    private final QuestsGUI questsGUI;

    public QuestsCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.questsGUI = new QuestsGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        questsGUI.open(player);
        return true;
    }
}
