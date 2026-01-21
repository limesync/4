package com.mobrealms.core.placeholder;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class MobRealmsPlaceholder extends PlaceholderExpansion {

    private final MobRealmsCore plugin;

    public MobRealmsPlaceholder(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "mobrealms";
    }

    @Override
    public String getAuthor() {
        return "MobRealms";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) {
            return "";
        }

        switch (identifier.toLowerCase()) {
            case "level":
                return String.valueOf(data.getLevel());

            case "xp":
                return String.valueOf(data.getXP());

            case "xp_required":
                int nextLevel = data.getLevel() + 1;
                return String.valueOf(plugin.getLevelManager().getXPRequired(nextLevel));

            case "coins":
                return String.valueOf(data.getCoins());

            case "class":
                return data.hasClass() ? data.getPlayerClass() : "None";

            case "realm":
                return data.getCurrentRealm() != null ? data.getCurrentRealm() : "None";

            default:
                return null;
        }
    }
}
