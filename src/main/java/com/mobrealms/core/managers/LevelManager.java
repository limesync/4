package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LevelManager {

    private final MobRealmsCore plugin;

    public LevelManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public long getXPRequired(int level) {
        int baseXP = plugin.getConfigManager().getBaseXP();
        double multiplier = plugin.getConfigManager().getXPMultiplier();

        return (long) (baseXP * Math.pow(multiplier, level - 1));
    }

    public void addXP(Player player, long amount) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        data.addXP(amount);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("amount", String.valueOf(amount));
        plugin.getMessageManager().sendMessageWithoutPrefix(player, "xp.gained", replacements);

        checkLevelUp(player, data);

        plugin.getPlayerDataManager().forceUpdate(player.getUniqueId());
        plugin.getScoreboardManager().forceUpdate(player);
        plugin.getTablistManager().forceUpdate(player);
        plugin.getActionBarManager().forceUpdate(player);
    }

    private void checkLevelUp(Player player, PlayerData data) {
        int maxLevel = plugin.getConfigManager().getMaxLevel();

        while (data.getLevel() < maxLevel) {
            long xpRequired = getXPRequired(data.getLevel() + 1);

            if (data.getXP() >= xpRequired) {
                data.setXP(data.getXP() - xpRequired);
                data.setLevel(data.getLevel() + 1);

                levelUpEffects(player);

                Map<String, String> replacements = new HashMap<>();
                replacements.put("level", String.valueOf(data.getLevel()));
                plugin.getMessageManager().sendMessageWithoutPrefix(player, "level.levelup", replacements);

                plugin.getQuestManager().handleLevelUp(player, data.getLevel());
            } else {
                break;
            }
        }
    }

    private void levelUpEffects(Player player) {
        player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void setLevel(Player player, int level) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        int maxLevel = plugin.getConfigManager().getMaxLevel();
        level = Math.min(level, maxLevel);
        level = Math.max(level, 1);

        data.setLevel(level);
        data.setXP(0);

        plugin.getPlayerDataManager().forceUpdate(player.getUniqueId());
        plugin.getScoreboardManager().forceUpdate(player);
        plugin.getTablistManager().forceUpdate(player);
        plugin.getActionBarManager().forceUpdate(player);
    }

    public void setXP(Player player, long xp) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        data.setXP(Math.max(0, xp));
        checkLevelUp(player, data);

        plugin.getPlayerDataManager().forceUpdate(player.getUniqueId());
        plugin.getScoreboardManager().forceUpdate(player);
        plugin.getTablistManager().forceUpdate(player);
        plugin.getActionBarManager().forceUpdate(player);
    }
}
