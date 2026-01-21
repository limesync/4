package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RealmManager {

    private final MobRealmsCore plugin;
    private final Map<String, Location> realmSpawnCache;
    private Location lobbySpawn;

    public RealmManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.realmSpawnCache = new HashMap<>();

        loadLobbySpawn();
    }

    private void loadLobbySpawn() {
        plugin.getDatabaseManager().loadLocation("lobby", null).thenAccept(location -> {
            lobbySpawn = location;
            if (location != null && plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Loaded lobby spawn location");
            }
        });
    }

    public void setLobbySpawn(Location location) {
        lobbySpawn = location;
        plugin.getDatabaseManager().saveLocation("lobby", null, location);
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public void setRealmSpawn(String realm, Location location) {
        realmSpawnCache.put(realm, location);
        plugin.getDatabaseManager().saveLocation("realm_spawn", realm, location);
    }

    public void loadRealmSpawn(String realm) {
        plugin.getDatabaseManager().loadLocation("realm_spawn", realm).thenAccept(location -> {
            if (location != null) {
                realmSpawnCache.put(realm, location);
                if (plugin.getConfigManager().isDebug()) {
                    plugin.getLogger().info("Loaded spawn for realm: " + realm);
                }
            }
        });
    }

    public Location getRealmSpawn(String realm) {
        if (!realmSpawnCache.containsKey(realm)) {
            loadRealmSpawn(realm);
        }
        return realmSpawnCache.get(realm);
    }

    public boolean canAccessRealm(Player player, String realm) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return false;

        int requiredLevel = plugin.getConfigManager().getRealmRequiredLevel(realm);
        return data.getLevel() >= requiredLevel;
    }

    public void teleportToRealm(Player player, String realm) {
        if (!canAccessRealm(player, realm)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("required_level", String.valueOf(plugin.getConfigManager().getRealmRequiredLevel(realm)));
            replacements.put("realm", plugin.getConfigManager().getRealmName(realm));
            plugin.getMessageManager().sendMessage(player, "realm.insufficient_level", replacements);
            return;
        }

        Location spawn = getRealmSpawn(realm);
        if (spawn == null) {
            player.sendMessage(plugin.getMessageManager().getPrefix() + " §cRealm spawn not set!");
            return;
        }

        player.teleport(spawn);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data != null) {
            data.setCurrentRealm(realm);
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("realm", plugin.getConfigManager().getRealmName(realm));
        plugin.getMessageManager().sendMessage(player, "realm.entered", replacements);
    }

    public void teleportToLobby(Player player) {
        if (lobbySpawn == null) {
            player.sendMessage(plugin.getMessageManager().getPrefix() + " §cLobby spawn not set!");
            return;
        }

        player.teleport(lobbySpawn);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data != null) {
            data.setCurrentRealm(null);
        }
    }
}
