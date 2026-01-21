package com.mobrealms.core.commands;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.gui.ShopGUI;
import com.mobrealms.core.listeners.InventoryClickListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ShopCommand implements CommandExecutor {

    private final MobRealmsCore plugin;
    private final ShopGUI shopGUI;

    public ShopCommand(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(plugin);

        for (var handler : HandlerList.getRegisteredListeners(plugin)) {
            if (handler.getListener() instanceof InventoryClickListener) {
                ((InventoryClickListener) handler.getListener()).setShopGUI(shopGUI);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        shopGUI.openMainMenu(player);
        return true;
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }
}
