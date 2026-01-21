package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final MobRealmsCore plugin;
    private final FileConfiguration config;

    private FileConfiguration scoreboardConfig;
    private FileConfiguration tablistConfig;
    private FileConfiguration actionbarConfig;
    private FileConfiguration nametagsConfig;
    private FileConfiguration motdConfig;

    private final Map<String, FileConfiguration> configs = new HashMap<>();

    public ConfigManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        loadAllConfigs();
    }

    private void loadAllConfigs() {
        plugin.saveDefaultConfig();

        scoreboardConfig = loadConfig("scoreboard.yml");
        tablistConfig = loadConfig("tablist.yml");
        actionbarConfig = loadConfig("actionbar.yml");
        nametagsConfig = loadConfig("nametags.yml");
        motdConfig = loadConfig("motd.yml");

        configs.put("main", config);
        configs.put("scoreboard", scoreboardConfig);
        configs.put("tablist", tablistConfig);
        configs.put("actionbar", actionbarConfig);
        configs.put("nametags", nametagsConfig);
        configs.put("motd", motdConfig);
    }

    private FileConfiguration loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig(String name) {
        return configs.getOrDefault(name, config);
    }

    public FileConfiguration getScoreboardConfig() {
        return scoreboardConfig;
    }

    public FileConfiguration getTablistConfig() {
        return tablistConfig;
    }

    public FileConfiguration getActionbarConfig() {
        return actionbarConfig;
    }

    public FileConfiguration getNametagsConfig() {
        return nametagsConfig;
    }

    public FileConfiguration getMotdConfig() {
        return motdConfig;
    }

    public int getBaseXP() {
        return config.getInt("level.base_xp", 100);
    }

    public double getXPMultiplier() {
        return config.getDouble("level.xp_multiplier", 1.5);
    }

    public int getMaxLevel() {
        return config.getInt("level.max_level", 1000);
    }

    public List<String> getClasses() {
        return config.getStringList("classes.list");
    }

    public String getLobbyWorld() {
        return config.getString("lobby_world", "world");
    }

    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }

    public int getRealmRequiredLevel(String realm) {
        return config.getInt("realms." + realm + ".required_level", 1);
    }

    public String getRealmName(String realm) {
        return config.getString("realms." + realm + ".name", realm);
    }

    public String getRealmDescription(String realm) {
        return config.getString("realms." + realm + ".description", "");
    }

    public boolean isShopEnabled() {
        return config.getBoolean("shop.enabled", true);
    }

    public int getAfkTimeout() {
        return config.getInt("afk.timeout_seconds", 180);
    }

    public boolean isAfkBroadcast() {
        return config.getBoolean("afk.broadcast", true);
    }

    public boolean isAfkFarmingPrevented() {
        return config.getBoolean("afk.prevent_farming", true);
    }

    public int getAfkKickMinutes() {
        return config.getInt("afk.kick_after_minutes", -1);
    }

    public boolean isChatEnabled() {
        return config.getBoolean("chat.enabled", true);
    }

    public String getChatFormat() {
        return config.getString("chat.format", "{rank} &7[{level}&7] &f{player}&8: &7{message}");
    }

    public boolean useLuckPermsForChat() {
        return config.getBoolean("chat.use_luckperms", true);
    }

    public int getCombatDuration() {
        return config.getInt("combat.duration", 15);
    }

    public boolean isCombatBlockCommands() {
        return config.getBoolean("combat.block_commands", true);
    }

    public List<String> getCombatBlockedCommands() {
        return config.getStringList("combat.blocked_commands");
    }

    public boolean isCombatBlockTeleport() {
        return config.getBoolean("combat.block_teleport", true);
    }

    public boolean isKillstreakEnabled() {
        return config.getBoolean("killstreak.enabled", true);
    }

    public boolean isKillstreakAnnounce() {
        return config.getBoolean("killstreak.announce", true);
    }

    public int getKillstreakAnnounceMinimum() {
        return config.getInt("killstreak.announce_minimum", 5);
    }

    public int getStartingCoins() {
        return config.getInt("economy.starting_coins", 100);
    }

    public int getMaxCoins() {
        return config.getInt("economy.max_coins", -1);
    }

    public boolean isQuestsEnabled() {
        return config.getBoolean("quests.enabled", true);
    }

    public String getQuestsDailyReset() {
        return config.getString("quests.daily_reset", "00:00");
    }

    public int getQuestsMaxActive() {
        return config.getInt("quests.max_active", 3);
    }

    public boolean isQuestsAutoAccept() {
        return config.getBoolean("quests.auto_accept", false);
    }

    public boolean isDailyRewardsEnabled() {
        return config.getBoolean("daily_rewards.enabled", true);
    }

    public int getDailyRewardCoins() {
        return config.getInt("daily_rewards.coins", 200);
    }

    public boolean isScoreboardEnabled() {
        return scoreboardConfig.getBoolean("enabled", true);
    }

    public int getScoreboardUpdateInterval() {
        return scoreboardConfig.getInt("update_interval", 20);
    }

    public boolean isScoreboardTitleAnimated() {
        return scoreboardConfig.getBoolean("title.animated", true);
    }

    public int getScoreboardTitleAnimationSpeed() {
        return scoreboardConfig.getInt("title.animation_speed", 40);
    }

    public List<String> getScoreboardTitleFrames() {
        return scoreboardConfig.getStringList("title.frames");
    }

    public String getScoreboardTitleStatic() {
        return scoreboardConfig.getString("title.static", "&b&lMOBREALMS");
    }

    public String getScoreboardBorderTop() {
        return scoreboardConfig.getString("borders.top", "&7&m━━━━━━━━━━━━━━━━");
    }

    public String getScoreboardBorderBottom() {
        return scoreboardConfig.getString("borders.bottom", "&7&m━━━━━━━━━━━━━━━━");
    }

    public boolean isScoreboardBorderTopEnabled() {
        return scoreboardConfig.getBoolean("borders.use_top", true);
    }

    public boolean isScoreboardBorderBottomEnabled() {
        return scoreboardConfig.getBoolean("borders.use_bottom", true);
    }

    public boolean isScoreboardNumberFormat() {
        return scoreboardConfig.getBoolean("number_format.enabled", true);
    }

    public boolean isScoreboardNumberFormatShort() {
        return scoreboardConfig.getBoolean("number_format.use_short_format", true);
    }

    public int getScoreboardNumberFormatDecimalPlaces() {
        return scoreboardConfig.getInt("number_format.decimal_places", 1);
    }

    public boolean isTablistEnabled() {
        return tablistConfig.getBoolean("enabled", true);
    }

    public int getTablistUpdateInterval() {
        return tablistConfig.getInt("update_interval", 100);
    }

    public boolean isTablistHeaderAnimated() {
        return tablistConfig.getBoolean("header.animated", true);
    }

    public int getTablistHeaderAnimationSpeed() {
        return tablistConfig.getInt("header.animation_speed", 60);
    }

    public List<String> getTablistHeaderStatic() {
        return tablistConfig.getStringList("header.static");
    }

    public boolean isTablistFooterAnimated() {
        return tablistConfig.getBoolean("footer.animated", true);
    }

    public int getTablistFooterAnimationSpeed() {
        return tablistConfig.getInt("footer.animation_speed", 80);
    }

    public List<String> getTablistFooterStatic() {
        return tablistConfig.getStringList("footer.static");
    }

    public boolean isTablistPlayerNameEnabled() {
        return tablistConfig.getBoolean("player_name.enabled", true);
    }

    public boolean isTablistPrefixEnabled() {
        return tablistConfig.getBoolean("player_name.prefix.enabled", true);
    }

    public boolean isTablistSuffixEnabled() {
        return tablistConfig.getBoolean("player_name.suffix.enabled", true);
    }

    public boolean isTablistSortingEnabled() {
        return tablistConfig.getBoolean("sorting.enabled", true);
    }

    public String getTablistSortingPrimary() {
        return tablistConfig.getString("sorting.primary", "RANK");
    }

    public String getTablistSortingSecondary() {
        return tablistConfig.getString("sorting.secondary", "LEVEL");
    }

    public boolean isActionBarEnabled() {
        return actionbarConfig.getBoolean("enabled", true);
    }

    public int getActionBarUpdateInterval() {
        return actionbarConfig.getInt("update_interval", 20);
    }

    public String getActionBarFormat() {
        return actionbarConfig.getString("default.format", "&f⚡ Level {level} &8│ {xp_bar} &8│ &6⛁ {coins}");
    }

    public String getActionBarFormatMaxLevel() {
        return actionbarConfig.getString("default.format_max_level", "&f⚡ Level {level} &7(MAX) &8│ &6⛁ {coins} Coins");
    }

    public int getActionBarProgressBarLength() {
        return actionbarConfig.getInt("progress_bar.length", 20);
    }

    public String getActionBarProgressBarFilled() {
        return actionbarConfig.getString("progress_bar.filled_character", "█");
    }

    public String getActionBarProgressBarEmpty() {
        return actionbarConfig.getString("progress_bar.empty_character", "░");
    }

    public String getActionBarProgressBarFilledColor() {
        return actionbarConfig.getString("progress_bar.filled_color", "&a");
    }

    public String getActionBarProgressBarEmptyColor() {
        return actionbarConfig.getString("progress_bar.empty_color", "&7");
    }

    public String getActionBarProgressBarStyle() {
        return actionbarConfig.getString("progress_bar.style", "GRADIENT");
    }

    public boolean isActionBarCombatEnabled() {
        return actionbarConfig.getBoolean("combat.enabled", true);
    }

    public String getActionBarCombatFormat() {
        return actionbarConfig.getString("combat.format", "&c⚔ Combat &8│ &f{combat_timer}s &8│ &7Health: {health_bar}");
    }

    public boolean isActionBarKillstreakEnabled() {
        return actionbarConfig.getBoolean("killstreak.enabled", true);
    }

    public int getActionBarKillstreakMinimum() {
        return actionbarConfig.getInt("killstreak.minimum", 3);
    }

    public int getActionBarKillstreakDuration() {
        return actionbarConfig.getInt("killstreak.duration", 5);
    }

    public boolean isActionBarLevelUpEnabled() {
        return actionbarConfig.getBoolean("level_up.enabled", true);
    }

    public int getActionBarLevelUpDuration() {
        return actionbarConfig.getInt("level_up.duration", 5);
    }

    public List<String> getActionBarLevelUpFrames() {
        return actionbarConfig.getStringList("level_up.frames");
    }

    public boolean isNametagsEnabled() {
        return nametagsConfig.getBoolean("enabled", true);
    }

    public int getNametagUpdateInterval() {
        return nametagsConfig.getInt("update_interval", 40);
    }

    public boolean isNametagPrefixEnabled() {
        return nametagsConfig.getBoolean("format.prefix.enabled", true);
    }

    public boolean isNametagUseLuckPerms() {
        return nametagsConfig.getBoolean("format.prefix.use_luckperms", true);
    }

    public boolean isNametagShowLevel() {
        return nametagsConfig.getBoolean("format.prefix.show_level", true);
    }

    public String getNametagLevelFormat() {
        return nametagsConfig.getString("format.prefix.level_format", "&7[{level}&7]");
    }

    public boolean isNametagShowRank() {
        return nametagsConfig.getBoolean("format.prefix.show_rank", true);
    }

    public String getNametagRankFormat() {
        return nametagsConfig.getString("format.prefix.rank_format", "{rank}");
    }

    public boolean isNametagStripRankColors() {
        return nametagsConfig.getBoolean("format.prefix.strip_rank_colors", false);
    }

    public String getNametagPrefixTemplate() {
        return nametagsConfig.getString("format.prefix.template", "{rank} &7[{level}&7] ");
    }

    public String getNametagNameColor() {
        return nametagsConfig.getString("format.name.color", "&f");
    }

    public boolean isNametagHealthColor() {
        return nametagsConfig.getBoolean("format.name.health_color.enabled", true);
    }

    public boolean isNametagClassColor() {
        return nametagsConfig.getBoolean("format.name.class_color.enabled", false);
    }

    public boolean isNametagSuffixEnabled() {
        return nametagsConfig.getBoolean("format.suffix.enabled", true);
    }

    public boolean isNametagShowClass() {
        return nametagsConfig.getBoolean("format.suffix.show_class", true);
    }

    public String getNametagClassFormat() {
        return nametagsConfig.getString("format.suffix.class_format", " &8[{class_symbol}&8]");
    }

    public String getNametagSuffixTemplate() {
        return nametagsConfig.getString("format.suffix.template", " &8[{class_symbol}&8]");
    }

    public String getNametagClassSymbol(String className) {
        return nametagsConfig.getString("class_symbols." + className.toLowerCase(), "&7?");
    }

    public boolean isNametagHealthDisplayEnabled() {
        return nametagsConfig.getBoolean("health_display.enabled", true);
    }

    public int getNametagHealthDisplayUpdateInterval() {
        return nametagsConfig.getInt("health_display.update_interval", 10);
    }

    public String getNametagHealthDisplayFormat() {
        return nametagsConfig.getString("health_display.format", "&c❤ {health}&7/&c{max_health}");
    }

    public boolean isNametagHealthDisplayOnlyWhenDamaged() {
        return nametagsConfig.getBoolean("health_display.only_when_damaged", true);
    }

    public int getNametagHealthDisplayHideAfter() {
        return nametagsConfig.getInt("health_display.hide_after", 5);
    }

    public boolean isMotdEnabled() {
        return motdConfig.getBoolean("enabled", true);
    }

    public String getMotdText() {
        return motdConfig.getString("text", "");
    }

    public int getMotdMaxPlayers() {
        return motdConfig.getInt("max_players", -1);
    }

    public boolean isMotdAnimationEnabled() {
        return motdConfig.getBoolean("animation.enabled", true);
    }

    public int getMotdAnimationSpeed() {
        return motdConfig.getInt("animation.speed", 60);
    }

    public String getMotdAnimationType() {
        return motdConfig.getString("animation.type", "CYCLE");
    }

    public boolean isMotdMaintenanceEnabled() {
        return motdConfig.getBoolean("maintenance.enabled", false);
    }

    public boolean isMotdMaintenanceKickPlayers() {
        return motdConfig.getBoolean("maintenance.kick_players", true);
    }

    public List<String> getMotdMaintenanceKickMessage() {
        return motdConfig.getStringList("maintenance.kick_message");
    }

    public boolean isMotdEventsEnabled() {
        return motdConfig.getBoolean("events.enabled", true);
    }

    public void reload() {
        plugin.reloadConfig();
        loadAllConfigs();
    }

    public void saveConfig(String name) {
        FileConfiguration cfg = configs.get(name);
        if (cfg != null) {
            try {
                File file = new File(plugin.getDataFolder(), name + ".yml");
                cfg.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save " + name + ".yml: " + e.getMessage());
            }
        }
    }
}
