package com.mobrealms.core;

import com.mobrealms.core.commands.*;
import com.mobrealms.core.database.DatabaseManager;
import com.mobrealms.core.listeners.*;
import com.mobrealms.core.managers.*;
import com.mobrealms.core.placeholder.MobRealmsPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MobRealmsCore extends JavaPlugin {

    private static MobRealmsCore instance;

    private DatabaseManager databaseManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private PlayerDataManager playerDataManager;
    private LevelManager levelManager;
    private RealmManager realmManager;
    private NPCManager npcManager;
    private ActionBarManager actionBarManager;
    private KitManager kitManager;
    private ScoreboardManager scoreboardManager;
    private TablistManager tablistManager;
    private ChatManager chatManager;
    private CustomMobManager customMobManager;
    private SpawnerManager spawnerManager;
    private CombatLogManager combatLogManager;
    private AFKManager afkManager;
    private HealthBarManager healthBarManager;
    private QuestManager questManager;
    private DailyRewardManager dailyRewardManager;
    private KillstreakManager killstreakManager;
    private ItemDropManager itemDropManager;
    private ShopManager shopManager;
    private NametagManager nametagManager;
    private BaldricQuestManager baldricQuestManager;
    private AchievementManager achievementManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        databaseManager = new DatabaseManager(this);

        if (!databaseManager.connect()) {
            getLogger().severe("Failed to connect to database! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        playerDataManager = new PlayerDataManager(this);
        levelManager = new LevelManager(this);
        realmManager = new RealmManager(this);
        npcManager = new NPCManager(this);
        actionBarManager = new ActionBarManager(this);
        kitManager = new KitManager(this);
        scoreboardManager = new ScoreboardManager(this);
        tablistManager = new TablistManager(this);
        chatManager = new ChatManager(this);
        customMobManager = new CustomMobManager(this);
        spawnerManager = new SpawnerManager(this);
        combatLogManager = new CombatLogManager(this);
        afkManager = new AFKManager(this);
        healthBarManager = new HealthBarManager(this);
        questManager = new QuestManager(this);
        dailyRewardManager = new DailyRewardManager(this);
        killstreakManager = new KillstreakManager(this);
        itemDropManager = new ItemDropManager(this);
        shopManager = new ShopManager(this);
        nametagManager = new NametagManager(this);
        baldricQuestManager = new BaldricQuestManager(this);
        achievementManager = new AchievementManager(this);

        registerCommands();
        registerListeners();
        registerPlaceholders();

        actionBarManager.start();
        combatLogManager.start();
        afkManager.start();
        healthBarManager.start();
        nametagManager.start();

        getLogger().info("MobRealmsCore has been enabled!");
    }

    @Override
    public void onDisable() {
        if (actionBarManager != null) {
            actionBarManager.stop();
        }

        if (spawnerManager != null) {
            spawnerManager.stop();
        }

        if (combatLogManager != null) {
            combatLogManager.stop();
        }

        if (afkManager != null) {
            afkManager.stop();
        }

        if (healthBarManager != null) {
            healthBarManager.stop();
        }

        if (questManager != null) {
            questManager.saveAll();
        }

        if (baldricQuestManager != null) {
            baldricQuestManager.saveAll();
        }

        if (achievementManager != null) {
            achievementManager.saveAll();
        }

        if (dailyRewardManager != null) {
            dailyRewardManager.saveAll();
        }

        if (playerDataManager != null) {
            playerDataManager.saveAll();
        }

        if (npcManager != null) {
            npcManager.removeAllNPCs();
        }

        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("MobRealmsCore has been disabled!");
    }

    private void registerCommands() {
        getCommand("mobrealms").setExecutor(new HelpCommand(this));
        getCommand("setlobby").setExecutor(new SetLobbyCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("setcoins").setExecutor(new SetCoinsCommand(this));
        getCommand("addcoins").setExecutor(new AddCoinsCommand(this));
        getCommand("givecoins").setExecutor(new GiveCoinsCommand(this));
        getCommand("removecoins").setExecutor(new RemoveCoinsCommand(this));
        getCommand("setxp").setExecutor(new SetXPCommand(this));
        getCommand("addxp").setExecutor(new AddXPCommand(this));
        getCommand("setlevel").setExecutor(new SetLevelCommand(this));
        getCommand("setclass").setExecutor(new SetClassCommand(this));
        getCommand("resetclass").setExecutor(new ResetClassCommand(this));
        getCommand("spawnadventurenpc").setExecutor(new SpawnAdventureNPCCommand(this));
        getCommand("spawnshanenpc").setExecutor(new SpawnShaneNPCCommand(this));
        getCommand("spawnkendranpc").setExecutor(new SpawnKendraNPCCommand(this));
        getCommand("spawnbaldricnpc").setExecutor(new SpawnBaldricNPCCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("mobspawner").setExecutor(new MobSpawnerCommand(this));
        getCommand("killmobs").setExecutor(new KillMobsCommand(this));
        getCommand("spawnerinfo").setExecutor(new SpawnerInfoCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("quests").setExecutor(new QuestsCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("achievements").setExecutor(new AchievementsCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);

        InventoryClickListener invListener = new InventoryClickListener(this);
        getServer().getPluginManager().registerEvents(invListener, this);

        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerVisibilityListener(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawnEggListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerPlacementListener(this), this);
        getServer().getPluginManager().registerEvents(new MobGriefingListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new ServerMOTDListener(this), this);
        getServer().getPluginManager().registerEvents(new SlimeSplitListener(this), this);
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MobRealmsPlaceholder(this).register();
            getLogger().info("PlaceholderAPI hooked successfully!");
        }
    }

    public static MobRealmsCore getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public RealmManager getRealmManager() {
        return realmManager;
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public TablistManager getTablistManager() {
        return tablistManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public CustomMobManager getCustomMobManager() {
        return customMobManager;
    }

    public SpawnerManager getSpawnerManager() {
        return spawnerManager;
    }

    public CombatLogManager getCombatLogManager() {
        return combatLogManager;
    }

    public AFKManager getAFKManager() {
        return afkManager;
    }

    public HealthBarManager getHealthBarManager() {
        return healthBarManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public DailyRewardManager getDailyRewardManager() {
        return dailyRewardManager;
    }

    public KillstreakManager getKillstreakManager() {
        return killstreakManager;
    }

    public ItemDropManager getItemDropManager() {
        return itemDropManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public NametagManager getNametagManager() {
        return nametagManager;
    }

    public BaldricQuestManager getBaldricQuestManager() {
        return baldricQuestManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }
}
