package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.entity.Player;

public class KillstreakManager {
    private final MobRealmsCore plugin;

    public KillstreakManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public void handleKill(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data != null) {
            data.incrementCurrentKillstreak();
            int streak = data.getCurrentKillstreak();

            if (streak > data.getHighestKillstreak()) {
                data.setHighestKillstreak(streak);
            }

            if (streak % 5 == 0) {
                plugin.getServer().broadcastMessage("§6" + player.getName() + " §eis on a §c" + streak + " §ekillstreak!");
            }
        }
    }

    public void resetStreak(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data != null) {
            data.setCurrentKillstreak(0);
        }
    }

    public double getXPMultiplier(int killstreak) {
        if (killstreak >= 25) return 5.0;
        if (killstreak >= 20) return 3.0;
        if (killstreak >= 15) return 2.5;
        if (killstreak >= 10) return 2.0;
        if (killstreak >= 5) return 1.5;
        if (killstreak >= 3) return 1.2;
        return 1.0;
    }
}
