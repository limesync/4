package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final MobRealmsCore plugin;
    private final Map<UUID, PlayerData> playerDataMap;

    public PlayerDataManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();

        startAutoSaveTask();
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();

        plugin.getDatabaseManager().loadPlayerData(uuid).thenAccept(data -> {
            playerDataMap.put(uuid, data);

            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Loaded data for player " + player.getName() +
                        " (Level: " + data.getLevel() + ", XP: " + data.getXP() + ", Coins: " + data.getCoins() + ")");
            }
        });
    }

    public void unloadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = playerDataMap.remove(uuid);

        if (data != null && data.isDirty()) {
            plugin.getDatabaseManager().savePlayerData(data);
        }
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.get(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public Map<UUID, PlayerData> getAllPlayerData() {
        return new HashMap<>(playerDataMap);
    }

    public void savePlayerData(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data != null && data.isDirty()) {
            plugin.getDatabaseManager().savePlayerData(data);
        }
    }

    public void saveAll() {
        for (PlayerData data : playerDataMap.values()) {
            if (data.isDirty()) {
                plugin.getDatabaseManager().savePlayerData(data);
            }
        }
    }

    private void startAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAll();
            }
        }.runTaskTimerAsynchronously(plugin, 1200L, 1200L);
    }

    public void forceUpdate(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data != null) {
            data.setDirty(true);
            plugin.getDatabaseManager().savePlayerData(data);
        }
    }

    public void forceUpdateAll() {
        for (PlayerData data : playerDataMap.values()) {
            data.setDirty(true);
            plugin.getDatabaseManager().savePlayerData(data);
        }
    }
}
