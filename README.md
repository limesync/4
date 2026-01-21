# MobRealms Core Plugin

A grindy RPG Minecraft server plugin inspired by AdventureQuest Worlds (AQW), focused on mob grinding progression through different realms.

## Features

- **Level & XP System**: Gain XP by defeating mobs, level up for rewards
- **Class System**: Choose from Warrior, Mage, or Rogue
- **Realm System**: Progress through different realms with level requirements
- **Economy**: Earn coins by grinding mobs
- **ActionBar Display**: Always-visible progression stats
- **NPC System**: Interactive NPCs for class selection
- **PlaceholderAPI Integration**: Full placeholder support for scoreboard/chat integration
- **File-Based Storage**: YAML-based persistent data storage

## Requirements

- Java 17 or higher
- Paper/Spigot 1.20.4 (compatible with 1.8-1.20+)
- Maven for building
- PlaceholderAPI (optional but recommended)

## Building

```bash
mvn clean package
```

The compiled JAR will be in `target/MobRealmsCore-1.0.0.jar`

## Installation

1. Build the plugin or download the JAR
2. Place in your server's `plugins/` folder
3. Start the server - configuration files will be generated automatically
4. Configure settings in `plugins/MobRealmsCore/config.yml`
5. Use `/setlobby` and `/spawnadventurenpc` to set up the server

## Configuration

### config.yml

Configure level progression, realms, and classes.

### messages.yml

Customize all plugin messages and GUI text.

## Commands

### General Commands
- `/mobrealms` (aliases: `/mr`, `/mrhelp`) - Show help menu with available commands

### Setup Commands
- `/setlobby` - Set the lobby spawn location
- `/setspawn <realm>` - Set spawn location for a realm
- `/spawnadventurenpc` - Spawn the Begin Adventure NPC

### Player Management
- `/setlevel <player> <level>` - Set player level
- `/setxp <player> <amount>` - Set player XP
- `/addxp <player> <amount>` - Add XP to player
- `/setcoins <player> <amount>` - Set player coins
- `/addcoins <player> <amount>` - Add coins to player
- `/givecoins <player> <amount>` - Give coins to player
- `/removecoins <player> <amount>` - Remove coins from player
- `/setclass <player> <class>` - Set player class
- `/resetclass <player>` - Reset player class

**Note:** Setup and management commands require `mobrealms.admin` permission. The help command is available to all players but shows different commands based on permissions.

## PlaceholderAPI Placeholders

- `%mobrealms_level%` - Player level
- `%mobrealms_xp%` - Current XP
- `%mobrealms_xp_required%` - XP required for next level
- `%mobrealms_coins%` - Player coins
- `%mobrealms_class%` - Player class
- `%mobrealms_realm%` - Current realm

## How It Works

### First Join
1. Players spawn in the lobby (void world)
2. They cannot take damage or deal damage
3. They must interact with "Begin Your Adventure" NPC
4. Choose a class: Warrior, Mage, or Rogue
5. Once chosen, progression is unlocked

### Progression
- Kill mobs to gain XP and coins
- Level up to access new realms
- Higher realms = stronger mobs = better rewards
- XP requirements scale with level

### Realms
Default realms (configurable):
- Starter Realm (Level 1)
- Forest Realm (Level 10)
- Crypt Realm (Level 25)
- Infernal Realm (Level 50)

## Data Storage

The plugin uses YAML files for data storage:

### Player Data
- Stored in `plugins/MobRealmsCore/playerdata/<uuid>.yml`
- Each player has their own file containing:
  - uuid, level, xp, coins, class, current_realm

### Locations
- Stored in `plugins/MobRealmsCore/locations.yml`
- Contains lobby spawn and all realm spawn locations
- Organized by location type and realm name

## Version Compatibility

Built for 1.20.4 but compatible with clients from 1.8 to 1.20+:
- Uses ViaVersion/ViaBackwards for client compatibility
- No NMS code - pure Bukkit API
- Cross-version tested components

## Development

This plugin is designed as a core framework that can be extended with:
- Class abilities
- Gear tiers
- Quest systems
- Dungeons and raids
- Boss encounters
- Custom mob attributes per realm

## License

Copyright 2024 MobRealms
