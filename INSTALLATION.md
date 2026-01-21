# MobRealms Installation Guide

## Prerequisites

### Server Requirements
- Paper or Spigot server (1.20.4 recommended)
- Java 17 or higher
- ViaVersion + ViaBackwards (optional, for 1.8+ client support)
- PlaceholderAPI (optional, but recommended)

## Building the Plugin

### Using Maven

1. Ensure Maven is installed:
```bash
mvn --version
```

2. Build the plugin:
```bash
cd /path/to/MobRealmsCore
mvn clean package
```

3. Find the JAR in `target/MobRealmsCore-1.0.0.jar`

## Server Installation

### Step 1: Install the Plugin

1. Copy `MobRealmsCore-1.0.0.jar` to your server's `plugins/` folder
2. Start the server to generate configuration files
3. Plugin is ready to use!

### Step 2: Configure Settings (Optional)

1. Navigate to `plugins/MobRealmsCore/config.yml`
2. Adjust settings as needed:
   - Level progression (base_xp, xp_multiplier, max_level)
   - Classes (add or remove classes)
   - Realms (configure realm names and level requirements)
   - ActionBar update interval

3. Edit `plugins/MobRealmsCore/messages.yml` to customize all messages

### Step 3: In-Game Setup

1. Join the server as admin (with OP or `mobrealms.admin` permission)

2. Set the lobby spawn location:
```
/setlobby
```

3. Spawn the Begin Adventure NPC:
```
/spawnadventurenpc
```

4. Set spawn points for each realm:
```
/setspawn starter_realm
/setspawn forest_realm
/setspawn crypt_realm
/setspawn infernal_realm
```

Stand at the desired spawn location before running each command.

## Testing

### Test Player Flow

1. Join with a test account (non-admin)
2. You should spawn in lobby
3. Try to take damage - it should be blocked
4. Right-click the Begin Adventure NPC
5. Choose a class (Warrior, Mage, or Rogue)
6. You should now be able to take damage and fight
7. Kill a mob to test XP and coin rewards
8. Check ActionBar for live stats display

### Test Help Command

As any player:
```
/mobrealms
```
or
```
/mr
```

This shows available commands based on your permissions.

### Test Admin Commands

```
/setlevel <player> 10
/addxp <player> 1000
/addcoins <player> 500
```

### Test PlaceholderAPI (if installed)

Use placeholders in chat or scoreboard plugins:
- `%mobrealms_level%`
- `%mobrealms_xp%`
- `%mobrealms_coins%`
- `%mobrealms_class%`

## Data Storage

All data is stored in YAML files:

### Player Data
- Location: `plugins/MobRealmsCore/playerdata/<uuid>.yml`
- Contains: level, xp, coins, class, current_realm

### Locations
- Location: `plugins/MobRealmsCore/locations.yml`
- Contains: lobby spawn and all realm spawns

### Backups

To backup player data:
```bash
cp -r plugins/MobRealmsCore/playerdata/ backups/playerdata-$(date +%Y%m%d)/
cp plugins/MobRealmsCore/locations.yml backups/locations-$(date +%Y%m%d).yml
```

## Troubleshooting

### Plugin won't enable

- Check server logs in `logs/latest.log`
- Verify Java version is 17 or higher
- Check for conflicting plugins

### NPC not responding

- Verify NPC was spawned with `/spawnadventurenpc`
- Check if NPC entity still exists
- Try respawning the NPC

### Player data not saving

- Check file permissions on `plugins/MobRealmsCore/playerdata/`
- Verify disk space available
- Check server logs for errors

### PlaceholderAPI not working

- Ensure PlaceholderAPI is installed and enabled
- Check plugin load order in console
- Verify placeholder syntax is correct

## Realm Configuration

Edit `config.yml` to add custom realms:

```yaml
realms:
  your_custom_realm:
    name: "Your Custom Realm"
    required_level: 35
```

Then set the spawn in-game:
```
/setspawn your_custom_realm
```

## Performance Optimization

### For large servers

1. Adjust auto-save interval in code (default: 5 minutes)
2. Use SSD storage for player data files
3. Consider disabling debug mode in production

### ActionBar updates

Increase update interval to reduce CPU usage:
```yaml
actionbar_update_interval: 40  # 2 seconds instead of 1
```

## Next Steps

1. Customize messages in `messages.yml`
2. Adjust XP and level progression in `config.yml`
3. Create mob spawn areas in each realm
4. Set up additional realms as needed
5. Consider extensions:
   - Class abilities plugin
   - Custom gear system
   - Quest system
   - Boss encounters

## Support

For issues or questions:
- Check server logs in `logs/latest.log`
- Enable debug mode in `config.yml`
- Review player data files for corruption
- Check GitHub issues or documentation

## Migration from Old Versions

If upgrading from a Supabase-based version:
1. Export player data from Supabase
2. Convert to YAML format manually or with script
3. Place files in `plugins/MobRealmsCore/playerdata/`
4. Restart server to verify data loads correctly
