package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.gui.*;
import com.mobrealms.core.commands.AchievementsCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final MobRealmsCore plugin;
    private final ClassSelectionGUI classSelectionGUI;
    private final MobSpawnerGUI mobSpawnerGUI;
    private final StatsGUI statsGUI;
    private final LeaderboardGUI leaderboardGUI;
    private final QuestsGUI questsGUI;
    private final BaldricQuestsGUI baldricQuestsGUI;
    private final ShaneShopGUI shaneShopGUI;
    private final KendraShopGUI kendraShopGUI;
    private final AchievementsGUI achievementsGUI;
    private ShopGUI shopGUI;

    public InventoryClickListener(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.classSelectionGUI = new ClassSelectionGUI(plugin);
        this.mobSpawnerGUI = new MobSpawnerGUI(plugin);
        this.statsGUI = new StatsGUI(plugin);
        this.leaderboardGUI = new LeaderboardGUI(plugin);
        this.questsGUI = new QuestsGUI(plugin);
        this.baldricQuestsGUI = new BaldricQuestsGUI(plugin);
        this.shaneShopGUI = new ShaneShopGUI(plugin);
        this.kendraShopGUI = new KendraShopGUI(plugin);
        this.achievementsGUI = new AchievementsGUI(plugin);
    }

    public void setShopGUI(ShopGUI shopGUI) {
        this.shopGUI = shopGUI;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        String classSelectionTitle = plugin.getMessageManager().getMessage("gui.class_selection.title");

        plugin.getAFKManager().updateActivity(player);

        // Check if the inventory is a custom GUI
        if (isCustomGUI(title, classSelectionTitle)) {
            // Cancel all clicks in custom GUIs to prevent item extraction
            event.setCancelled(true);

            // Get the clicked item
            ItemStack clicked = event.getCurrentItem();

            // Route to appropriate handler based on title
            if (title.equals(classSelectionTitle)) {
                handleClassSelectionGUI(player, event.getSlot(), clicked);
            } else if (title.contains("Mob") || title.contains("Tier")) {
                handleMobSpawnerGUI(player, event.getSlot(), clicked, title);
            } else if (title.contains(ChatColor.stripColor(LeaderboardGUI.getInventoryTitle(LeaderboardGUI.LeaderboardType.LEVEL).substring(0, 15)))) {
                handleLeaderboardGUI(player, event.getSlot(), clicked, title);
            } else if (title.contains(ChatColor.stripColor(StatsGUI.getInventoryTitle()))) {
                handleStatsGUI(player, event.getSlot(), clicked);
            } else if (title.contains(ChatColor.stripColor(QuestsGUI.getInventoryTitle()))) {
                handleQuestsGUI(player, event.getSlot(), clicked);
            } else if (title.contains(ChatColor.stripColor(BaldricQuestsGUI.getInventoryTitle()))) {
                handleBaldricQuestsGUI(player, event.getSlot(), clicked);
            } else if (title.contains(ChatColor.stripColor(ShaneShopGUI.getInventoryTitle()))) {
                handleShaneShopGUI(player, event.getSlot(), clicked);
            } else if (title.contains(ChatColor.stripColor(KendraShopGUI.getInventoryTitle()))) {
                handleKendraShopGUI(player, event.getSlot(), clicked);
            } else if (title.contains(ChatColor.stripColor(AchievementsGUI.getInventoryTitle()))) {
                handleAchievementsGUI(player, event.getSlot(), clicked);
            } else if (title.startsWith("§6§l") && shopGUI != null) {
                shopGUI.handleClick(player, event.getSlot(), clicked, title);
            }
        }
    }

    /**
     * Checks if the inventory title matches any custom GUI title
     */
    private boolean isCustomGUI(String title, String classSelectionTitle) {
        // Strip color codes for more reliable matching
        String strippedTitle = ChatColor.stripColor(title);

        // Check each custom GUI title
        return title.equals(classSelectionTitle) ||
               title.contains("Mob") ||
               title.contains("Tier") ||
               strippedTitle.contains("Leaderboard") ||
               strippedTitle.contains("Statistics") ||
               strippedTitle.contains("Quests") ||
               strippedTitle.contains("Baldric") ||
               strippedTitle.contains("Shane") ||
               strippedTitle.contains("Kendra") ||
               strippedTitle.contains("Achievements") ||
               title.startsWith("§6§l");
    }

    /**
     * Handle clicks in Class Selection GUI
     */
    private void handleClassSelectionGUI(Player player, int slot, ItemStack clicked) {
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }
        classSelectionGUI.handleClick(player, slot);
    }

    /**
     * Handle clicks in Mob Spawner GUI
     */
    private void handleMobSpawnerGUI(Player player, int slot, ItemStack clicked, String title) {
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }
        mobSpawnerGUI.handleClick(player, slot, clicked, title);
    }

    /**
     * Handle clicks in Leaderboard GUI
     */
    private void handleLeaderboardGUI(Player player, int slot, ItemStack clicked, String title) {
        if (clicked == null) {
            return;
        }

        leaderboardGUI.handleClick(player, slot, clicked, title);
    }

    /**
     * Handle clicks in Stats GUI
     */
    private void handleStatsGUI(Player player, int slot, ItemStack clicked) {
        if (clicked == null) {
            return;
        }

        statsGUI.handleClick(player, slot);
    }

    /**
     * Handle clicks in Quests GUI
     */
    private void handleQuestsGUI(Player player, int slot, ItemStack clicked) {
        if (clicked == null) {
            return;
        }

        questsGUI.handleClick(player, slot, clicked);
    }

    /**
     * Handle clicks in Shane Shop GUI
     */
    private void handleShaneShopGUI(Player player, int slot, ItemStack clicked) {
        if (clicked == null) {
            return;
        }

        shaneShopGUI.handleClick(player, slot, clicked);
    }

    /**
     * Handle clicks in Kendra Shop GUI
     */
    private void handleKendraShopGUI(Player player, int slot, ItemStack clicked) {
        if (clicked == null) {
            return;
        }

        kendraShopGUI.handleClick(player, slot, clicked);
    }

    /**
     * Handle clicks in Baldric Quests GUI
     */
    private void handleBaldricQuestsGUI(Player player, int slot, ItemStack clicked) {
        if (clicked == null) {
            return;
        }

        baldricQuestsGUI.handleClick(player, slot, clicked);
    }

    /**
     * Handle clicks in Achievements GUI
     */
    private void handleAchievementsGUI(Player player, int slot, ItemStack clicked) {
        if (clicked == null) {
            return;
        }

        achievementsGUI.handleClick(player, slot, clicked);
    }
}
