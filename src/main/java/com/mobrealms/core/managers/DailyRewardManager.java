package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class DailyRewardManager {
    private final MobRealmsCore plugin;
    private final File rewardsFolder;

    public DailyRewardManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.rewardsFolder = new File(plugin.getDataFolder(), "dailyrewards");
        if (!rewardsFolder.exists()) {
            rewardsFolder.mkdirs();
        }
    }

    public void handleLogin(Player player) {
        YamlConfiguration config = getRewardData(player.getUniqueId());
        long lastLogin = config.getLong("last_login", 0);
        long currentTime = System.currentTimeMillis();
        long dayInMillis = 86400000;

        if (currentTime - lastLogin > dayInMillis * 2) {
            config.set("streak", 0);
        } else if (currentTime - lastLogin > dayInMillis) {
            int streak = config.getInt("streak", 0) + 1;
            if (streak > 7) streak = 1;
            config.set("streak", streak);

            long reward = 100 * streak;
            plugin.getPlayerDataManager().getPlayerData(player.getUniqueId()).addCoins(reward);
            player.sendMessage("§6Daily Reward! §e+" + reward + " coins §7(Day " + streak + "/7)");
        }
        config.set("last_login", currentTime);
    }

    public void saveAll() {}

    private YamlConfiguration getRewardData(UUID uuid) {
        File file = new File(rewardsFolder, uuid.toString() + ".yml");
        if (!file.exists()) {
            return new YamlConfiguration();
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
