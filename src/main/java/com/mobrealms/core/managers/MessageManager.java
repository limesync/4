package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class MessageManager {

    private final MobRealmsCore plugin;
    private final String prefix = c("&b&lMobRealms &8»&r");

    public MessageManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    public String getMessage(String path) {
        return getHardcodedMessage(path);
    }

    public String getMessage(String path, Map<String, String> replacements) {
        String message = getMessage(path);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public void sendMessage(Player player, String path) {
        player.sendMessage(prefix + " " + getMessage(path));
    }

    public void sendMessage(Player player, String path, Map<String, String> replacements) {
        player.sendMessage(prefix + " " + getMessage(path, replacements));
    }

    public void sendMessageWithoutPrefix(Player player, String path) {
        player.sendMessage(getMessage(path));
    }

    public void sendMessageWithoutPrefix(Player player, String path, Map<String, String> replacements) {
        player.sendMessage(getMessage(path, replacements));
    }

    public String getPrefix() {
        return prefix;
    }

    public List<String> getLoreList(String path) {
        String lore = getHardcodedMessage(path);
        if (lore.contains("|")) {
            return Arrays.stream(lore.split("\\|"))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return Arrays.asList(lore);
    }

    private String getHardcodedMessage(String path) {
        switch (path) {
            case "class.select_prompt": return c("&aChoose your class to begin!");
            case "class.selected": return c("&aYou selected the &e{class} &aclass!");
            case "class.already_selected": return c("&cYou already have a class!");
            case "class.locked": return c("&cSelect a class first!");
            case "class.reset": return c("&aClass reset!");

            case "gui.class_selection.title": return c("&6&lChoose Your Class");
            case "gui.class_selection.warrior.name": return c("&c&lWarrior");
            case "gui.class_selection.warrior.lore": return c("&7Strong melee fighter | &7High damage and defense | &7Best with swords and armor");
            case "gui.class_selection.mage.name": return c("&d&lMage");
            case "gui.class_selection.mage.lore": return c("&7Master of magic | &7High damage spells | &7Low defense, high power");
            case "gui.class_selection.archer.name": return c("&a&lArcher");
            case "gui.class_selection.archer.lore": return c("&7Ranged specialist | &7Expert with bows | &7Medium damage, high mobility");
            case "gui.class_selection.rogue.name": return c("&e&lRogue");
            case "gui.class_selection.rogue.lore": return c("&7Swift and deadly | &7Critical hit expert | &7High speed and damage");

            case "level.levelup": return c("&6&l✦ LEVEL UP! &r&eYou are now level &6{level}&e!");
            case "level.max_level": return c("&cMax level reached!");

            case "realm.insufficient_level": return c("&cRequires level &e{required_level} &cfor &e{realm}&c!");
            case "realm.entered": return c("&aEntered &e{realm}&a!");
            case "realm.spawn_set": return c("&aSpawn set for &e{realm}&a!");

            case "coins.set": return c("&aSet &e{player}&a's coins to &6{amount}&a!");
            case "coins.added": return c("&a+&6{amount} coins &ato &e{player}&a!");
            case "coins.removed": return c("&c-&6{amount} coins &cfrom &e{player}&a!");
            case "coins.insufficient": return c("&cNot enough coins!");

            case "shop.purchased": return c("&a&l✓ &aPurchased {item} &afor &6{price} coins&a!");
            case "shop.insufficient_coins": return c("&c&l✗ &cNeed &6{price} coins&c!");
            case "shop.insufficient_level": return c("&c&l✗ &cRequires level {level}!");

            case "xp.set": return c("&aSet &e{player}&a's XP to &b{amount}&a!");
            case "xp.added": return c("&a+&b{amount} XP &ato &e{player}&a!");
            case "xp.gained": return c("&a+{amount} XP");

            case "npc.begin_adventure.name": return c("&6&lStart Your Adventure");
            case "npc.shane.name": return c("&c&lShane &7[Weapons & Armor]");
            case "npc.kendra.name": return c("&e&lKendra &7[Blocks & Decor]");
            case "npc.baldric.name": return c("&a&lBaldric &7[Quests]");

            case "command.no_permission": return c("&cNo permission!");
            case "error.player_only": return c("&cPlayers only!");

            default: return c("&cMessage not found: " + path);
        }
    }

    private String c(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void reload() {
    }
}
