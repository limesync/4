# MobRealms Project Structure

## Overview

MobRealmsCore is organized into clean, modular packages following best practices for Minecraft plugin development.

## Directory Structure

```
MobRealmsCore/
├── src/main/
│   ├── java/com/mobrealms/core/
│   │   ├── MobRealmsCore.java          # Main plugin class
│   │   ├── commands/                    # All command implementations
│   │   │   ├── AddCoinsCommand.java
│   │   │   ├── AddXPCommand.java
│   │   │   ├── GiveCoinsCommand.java
│   │   │   ├── RemoveCoinsCommand.java
│   │   │   ├── ResetClassCommand.java
│   │   │   ├── SetClassCommand.java
│   │   │   ├── SetCoinsCommand.java
│   │   │   ├── SetLevelCommand.java
│   │   │   ├── SetLobbyCommand.java
│   │   │   ├── SetSpawnCommand.java
│   │   │   ├── SetXPCommand.java
│   │   │   └── SpawnAdventureNPCCommand.java
│   │   ├── database/                    # Database layer
│   │   │   └── DatabaseManager.java
│   │   ├── gui/                         # GUI implementations
│   │   │   └── ClassSelectionGUI.java
│   │   ├── listeners/                   # Event listeners
│   │   │   ├── EntityDamageByEntityListener.java
│   │   │   ├── EntityDeathListener.java
│   │   │   ├── InventoryClickListener.java
│   │   │   ├── PlayerDamageListener.java
│   │   │   ├── PlayerInteractListener.java
│   │   │   ├── PlayerJoinListener.java
│   │   │   └── PlayerQuitListener.java
│   │   ├── managers/                    # Business logic managers
│   │   │   ├── ActionBarManager.java
│   │   │   ├── ConfigManager.java
│   │   │   ├── LevelManager.java
│   │   │   ├── MessageManager.java
│   │   │   ├── NPCManager.java
│   │   │   ├── PlayerDataManager.java
│   │   │   └── RealmManager.java
│   │   ├── models/                      # Data models
│   │   │   └── PlayerData.java
│   │   └── placeholder/                 # PlaceholderAPI integration
│   │       └── MobRealmsPlaceholder.java
│   └── resources/
│       ├── config.yml                   # Main configuration
│       ├── messages.yml                 # All messages and text
│       └── plugin.yml                   # Plugin metadata
├── supabase/
│   └── migrations/                      # Database migrations
│       ├── create_mobrealms_players_table.sql
│       └── create_mobrealms_locations_table.sql
├── pom.xml                              # Maven build configuration
├── README.md                            # Main documentation
├── INSTALLATION.md                      # Installation guide
└── .gitignore                           # Git ignore rules
```

## Package Descriptions

### Core (`com.mobrealms.core`)

**MobRealmsCore.java**
- Main plugin entry point
- Manages plugin lifecycle (onEnable/onDisable)
- Initializes all managers
- Registers commands, listeners, and integrations

### Commands (`commands/`)

Each command is its own class implementing `CommandExecutor`. Commands handle:
- Permission checks
- Argument validation
- Calling appropriate manager methods
- Sending feedback to command sender

**Command Categories:**
- **Setup**: SetLobby, SetSpawn, SpawnAdventureNPC
- **Player Management**: SetLevel, SetClass, ResetClass
- **Economy**: SetCoins, AddCoins, GiveCoins, RemoveCoins
- **Progression**: SetXP, AddXP

### Database (`database/`)

**DatabaseManager.java**
- Handles all Supabase communication
- Async operations with CompletableFuture
- CRUD operations for player data and locations
- Uses JSON for API communication

**Key Methods:**
- `loadPlayerData(UUID)` - Load player data from database
- `savePlayerData(PlayerData)` - Save player data to database
- `loadLocation(type, realm)` - Load spawn locations
- `saveLocation(type, realm, location)` - Save spawn locations

### GUI (`gui/`)

**ClassSelectionGUI.java**
- Creates inventory-based GUI for class selection
- Handles inventory clicks
- Updates player data upon class selection
- Displays class information with lore

### Listeners (`listeners/`)

Event listeners handle game events:

**PlayerJoinListener** - Load player data on join
**PlayerQuitListener** - Save and unload player data on quit
**PlayerDamageListener** - Prevent damage for players without class
**PlayerInteractListener** - Handle NPC interactions
**EntityDamageByEntityListener** - Prevent players without class from attacking
**EntityDeathListener** - Grant XP and coins for mob kills
**InventoryClickListener** - Handle GUI clicks

### Managers (`managers/`)

Managers contain business logic and are the core of the plugin:

**ActionBarManager**
- Updates ActionBar display for all players
- Shows level, XP, and coins
- Configurable update interval

**ConfigManager**
- Centralized config access
- Type-safe config getters
- Handles config reloading

**LevelManager**
- XP and level calculations
- Level-up logic and effects
- XP requirement formulas

**MessageManager**
- Message loading and caching
- Color code translation
- Placeholder replacement in messages

**NPCManager**
- NPC spawning and management
- NPC metadata for identification
- Cleanup on plugin disable

**PlayerDataManager**
- In-memory cache of player data
- Load/unload player data
- Auto-save task
- Thread-safe operations

**RealmManager**
- Realm spawn management
- Level requirement checks
- Teleportation logic
- Realm spawn caching

### Models (`models/`)

**PlayerData**
- POJO for player information
- Dirty flag for change tracking
- Getters/setters with automatic dirty marking
- Represents a player's RPG state

### Placeholder (`placeholder/`)

**MobRealmsPlaceholder**
- PlaceholderAPI expansion
- Provides all player stat placeholders
- Real-time data from PlayerDataManager

## Data Flow

### Player Join Flow
1. PlayerJoinListener triggers
2. PlayerDataManager loads data from database (async)
3. Data cached in memory
4. ActionBarManager starts displaying stats

### Combat Flow
1. Player attacks mob → EntityDamageByEntityListener checks class
2. Mob dies → EntityDeathListener calculates rewards
3. LevelManager.addXP() called
4. Check for level-up → effects + message
5. PlayerData marked dirty for auto-save

### Class Selection Flow
1. Player right-clicks NPC → PlayerInteractListener
2. ClassSelectionGUI opens
3. Player clicks class → InventoryClickListener
4. ClassSelectionGUI.handleClick() updates PlayerData
5. Player unlocked, ActionBar starts showing

### Data Persistence Flow
1. PlayerData changes → marked dirty
2. Auto-save task runs every 5 minutes
3. Or immediate save on player quit
4. DatabaseManager sends to Supabase (async)
5. Dirty flag cleared on success

## Design Patterns

### Singleton Pattern
- MobRealmsCore.getInstance()
- All managers accessible through main plugin instance

### Manager Pattern
- Business logic separated from event handling
- Managers are stateless where possible
- Managers depend on other managers via plugin reference

### Repository Pattern
- DatabaseManager abstracts data persistence
- PlayerDataManager provides caching layer
- Clean separation between data access and business logic

### Observer Pattern
- Bukkit event system
- Listeners observe game events
- React by calling manager methods

## Extension Points

The plugin is designed for easy extension:

### Adding New Commands
1. Create class in `commands/` implementing CommandExecutor
2. Register in MobRealmsCore.registerCommands()
3. Add to plugin.yml

### Adding New Realms
1. Add to config.yml under `realms`
2. Use `/setspawn <realm>` in-game
3. No code changes needed

### Adding Class Abilities
1. Create AbilityManager in managers/
2. Listen for ability triggers in listeners/
3. Check player class before applying effects

### Adding Gear System
1. Create GearManager
2. Store gear in PlayerData or separate table
3. Modify combat listeners to apply gear stats

### Adding Quests
1. Create QuestManager and Quest model
2. Create quest database table
3. Add quest listeners for completion checks

## Thread Safety

- PlayerDataManager uses ConcurrentHashMap (future-proof)
- Database operations are async (CompletableFuture)
- Bukkit scheduler used for repeating tasks
- No shared mutable state between managers

## Performance Considerations

- Player data cached in memory
- Database writes batched (auto-save every 5 minutes)
- Async database operations don't block main thread
- Realm spawn locations cached after first load
- ActionBar updates at configurable interval

## Testing Strategy

### Unit Testing (Future)
- Test level calculations in LevelManager
- Test XP requirements formula
- Test PlayerData dirty flag logic

### Integration Testing
1. Test player join/quit cycle
2. Test class selection flow
3. Test level-up progression
4. Test realm access restrictions
5. Test command permissions

### Load Testing
- Multiple players joining simultaneously
- Concurrent database operations
- Large XP gains triggering multiple level-ups

## Version Compatibility

**1.8 - 1.20+ Support:**
- No NMS (Native Minecraft Server) code used
- Only Bukkit/Spigot API calls
- Material names compatible across versions
- ActionBar uses Spigot API (1.8+)
- ViaVersion handles protocol differences

**Java 17:**
- Modern language features
- Better performance
- Long-term support

## Future Enhancements

Potential additions without breaking core:

1. **Class Abilities System**
   - AbilityManager
   - Ability cooldowns
   - Class-specific skills

2. **Gear & Equipment**
   - Custom item stats
   - Tier progression
   - Set bonuses

3. **Quest System**
   - Quest objectives
   - Quest rewards
   - Daily/weekly quests

4. **Party System**
   - Group grinding
   - Shared XP
   - Party buffs

5. **Boss Encounters**
   - Custom boss mobs per realm
   - Special rewards
   - Boss mechanics

6. **Leaderboards**
   - Level leaderboard
   - Coin leaderboard
   - Kills leaderboard

All can be added as separate plugins extending MobRealmsCore or as modules within core.
