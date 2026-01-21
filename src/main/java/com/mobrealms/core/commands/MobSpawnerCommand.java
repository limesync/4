package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.gui.MobSpawnerGUI;
import com.mobrealms.core.models.MobSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MobSpawnerCommand implements CommandExecutor {

    private final MobRealmsCore plugin;
    private final MobSpawnerGUI mobSpawnerGUI;

    public MobSpawnerCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.mobSpawnerGUI = new MobSpawnerGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("error.player_only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp() && !player.hasPermission("mobrealms.mobspawner")) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.no_permission"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                player.sendMessage(plugin.getMessageManager().getPrefix() + " §cUsage: /spawner remove <ID>");
                player.sendMessage("§7Use §e/spawnerinfo §7to see all spawner IDs.");
                return true;
            }

            String shortId = args[1];
            MobSpawner spawner = plugin.getSpawnerManager().getSpawnerByShortId(shortId);

            if (spawner == null) {
                player.sendMessage(plugin.getMessageManager().getPrefix() + " §cSpawner with ID '" + shortId + "' not found!");
                player.sendMessage("§7Use §e/spawnerinfo §7to see all spawner IDs.");
                return true;
            }

            String mobName = plugin.getCustomMobManager().getCustomMob(spawner.getMobId()).getDisplayName();
            plugin.getSpawnerManager().removeSpawner(spawner.getId());

            player.sendMessage(plugin.getMessageManager().getPrefix() + " §aSuccessfully removed spawner!");
            player.sendMessage("§7Type: §f" + mobName);
            player.sendMessage("§7Location: §f" + spawner.getWorld() + " " + spawner.getX() + ", " + spawner.getY() + ", " + spawner.getZ());
            return true;
        }

        mobSpawnerGUI.openMainMenu(player);
        return true;
    }
}
