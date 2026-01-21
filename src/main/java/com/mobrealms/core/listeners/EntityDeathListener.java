package com.mobrealms.core.listeners;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EntityDeathListener implements Listener {

    private final MobRealmsCore plugin;
    private final Random random;

    public EntityDeathListener(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (plugin.getCustomMobManager().isCustomMob(entity)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        if (killer == null) {
            return;
        }

        plugin.getCombatLogManager().handleDeath(killer);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(killer.getUniqueId());
        if (data == null || !data.hasClass()) {
            return;
        }

        if (data.isAfk()) {
            killer.sendMessage("§cYou are AFK! No rewards earned.");
            return;
        }

        long xpReward;
        long coinsReward;
        boolean isBoss = false;
        int mobLevel = 1;

        if (plugin.getCustomMobManager().isCustomMob(entity)) {
            if (entity.hasMetadata("XPReward") && entity.hasMetadata("CoinReward")) {
                xpReward = entity.getMetadata("XPReward").get(0).asLong();
                coinsReward = entity.getMetadata("CoinReward").get(0).asLong();

                if (entity.hasMetadata("MobLevel")) {
                    mobLevel = entity.getMetadata("MobLevel").get(0).asInt();
                }

                isBoss = mobLevel >= 30;
            } else {
                xpReward = calculateXPReward(entity, data);
                coinsReward = calculateCoinsReward(entity, data);
            }

            plugin.getCustomMobManager().removeSpawnedMob(entity.getUniqueId());
        } else if (entity instanceof Monster) {
            xpReward = calculateXPReward(entity, data);
            coinsReward = calculateCoinsReward(entity, data);
        } else {
            return;
        }

        plugin.getKillstreakManager().handleKill(killer);
        double killstreakMultiplier = plugin.getKillstreakManager().getXPMultiplier(data.getCurrentKillstreak());

        xpReward = (long)(xpReward * killstreakMultiplier);

        data.incrementKills();
        if (isBoss) {
            data.incrementBossKills();
            plugin.getQuestManager().handleBossKill(killer);
        }
        data.addTotalCoinsEarned(coinsReward);

        plugin.getLevelManager().addXP(killer, xpReward);
        data.addCoins(coinsReward);

        plugin.getPlayerDataManager().forceUpdate(killer.getUniqueId());
        plugin.getScoreboardManager().forceUpdate(killer);
        plugin.getTablistManager().forceUpdate(killer);
        plugin.getActionBarManager().forceUpdate(killer);

        plugin.getQuestManager().handleKill(killer);
        plugin.getQuestManager().handleCoinsEarned(killer, coinsReward);
        plugin.getQuestManager().handleKillstreak(killer, data.getCurrentKillstreak());

        plugin.getBaldricQuestManager().handleKill(killer);
        plugin.getBaldricQuestManager().handleCoinsEarned(killer, coinsReward);
    }

    private long calculateXPReward(LivingEntity entity, PlayerData data) {
        double baseXP = entity.getMaxHealth() * 2;

        String realm = data.getCurrentRealm();
        if (realm != null) {
            int requiredLevel = plugin.getConfigManager().getRealmRequiredLevel(realm);
            baseXP *= (1 + (requiredLevel * 0.1));
        }

        return Math.max(1, (long) (baseXP * (0.8 + (random.nextDouble() * 0.4))));
    }

    private long calculateCoinsReward(LivingEntity entity, PlayerData data) {
        double baseCoins = entity.getMaxHealth() * 0.5;

        String realm = data.getCurrentRealm();
        if (realm != null) {
            int requiredLevel = plugin.getConfigManager().getRealmRequiredLevel(realm);
            baseCoins *= (1 + (requiredLevel * 0.15));
        }

        return Math.max(1, (long) (baseCoins * (0.7 + (random.nextDouble() * 0.6))));
    }
}
