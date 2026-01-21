package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import com.mobrealms.core.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KitManager {

    private final MobRealmsCore plugin;
    private static final long KIT_COOLDOWN = TimeUnit.HOURS.toMillis(24);

    public KitManager(MobRealmsCore plugin) {
        this.plugin = plugin;
    }

    /* ===================== CORE ===================== */

    public boolean canClaimKit(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null || !data.hasClass()) return false;
        return System.currentTimeMillis() - data.getLastKitClaim() >= KIT_COOLDOWN;
    }

    public long getRemainingCooldown(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) return 0;

        long elapsed = System.currentTimeMillis() - data.getLastKitClaim();
        return Math.max(0, KIT_COOLDOWN - elapsed);
    }

    public void giveKit(Player player) {
        giveKit(player, false);
    }

    public void giveKit(Player player, boolean bypassCooldown) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (data == null || !data.hasClass()) {
            player.sendMessage(plugin.getMessageManager().getMessage("kit.no_class"));
            return;
        }

        if (!bypassCooldown && !canClaimKit(player)) {
            player.sendMessage(plugin.getMessageManager().getMessage("kit.cooldown")
                    .replace("{time}", formatTime(getRemainingCooldown(player))));
            return;
        }

        player.getInventory().clear();

        switch (data.getPlayerClass().toLowerCase()) {
            case "warrior":
                giveWarriorKit(player);
                break;
            case "mage":
                giveMageKit(player);
                break;
            case "archer":
                giveArcherKit(player);
                break;
            case "rogue":
                giveRogueKit(player);
                break;
            default:
                player.sendMessage(plugin.getMessageManager().getMessage("kit.invalid_class"));
                return;
        }

        data.setLastKitClaim(System.currentTimeMillis());
        plugin.getDatabaseManager().savePlayerData(data);

        player.sendMessage(plugin.getMessageManager().getMessage("kit.received")
                .replace("{class}", capitalizeFirst(data.getPlayerClass())));
    }

    /* ===================== KITS ===================== */

    private void giveWarriorKit(Player player) {
        ItemStack sword = createItem(Material.IRON_SWORD, "&c&lWarrior Sword", "&7A sturdy blade");
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

        equipArmor(player,
                Material.IRON_HELMET,
                Material.IRON_CHESTPLATE,
                Material.IRON_LEGGINGS,
                Material.IRON_BOOTS
        );

        player.getInventory().setItem(0, sword);
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
    }

    private void giveMageKit(Player player) {
        ItemStack staff = createItem(Material.BLAZE_ROD, "&9&lMage Staff", "&7Arcane power flows through it");

        staff.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        ItemMeta meta = staff.getItemMeta();
        if (meta != null) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ATTACK_DAMAGE,
                    new AttributeModifier(
                            UUID.randomUUID(),
                            "mage_damage",
                            5.0,
                            AttributeModifier.Operation.ADD_NUMBER
                    )
            );
            staff.setItemMeta(meta);
        }

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        player.getInventory().setItem(0, staff);
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
    }

    private void giveArcherKit(Player player) {
        ItemStack bow = createItem(Material.BOW, "&a&lArcher Bow", "&7Precision and range");
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);

        equipArmor(player,
                Material.CHAINMAIL_HELMET,
                Material.CHAINMAIL_CHESTPLATE,
                Material.CHAINMAIL_LEGGINGS,
                Material.CHAINMAIL_BOOTS
        );

        player.getInventory().setItem(0, bow);
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
    }

    private void giveRogueKit(Player player) {
        ItemStack dagger = createItem(Material.IRON_SWORD, "&e&lRogue Dagger", "&7Fast and deadly");
        dagger.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        dagger.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        equipArmor(player,
                Material.LEATHER_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS
        );

        player.getInventory().setItem(0, dagger);
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
    }

    /* ===================== HELPERS ===================== */

    private void equipArmor(Player player, Material h, Material c, Material l, Material b) {
        player.getInventory().setHelmet(new ItemStack(h));
        player.getInventory().setChestplate(new ItemStack(c));
        player.getInventory().setLeggings(new ItemStack(l));
        player.getInventory().setBoots(new ItemStack(b));
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(colorize(name));
            if (lore.length > 0) {
                meta.setLore(Arrays.stream(lore).map(this::colorize).collect(Collectors.toList()));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private String capitalizeFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public String formatTime(long millis) {
        long h = TimeUnit.MILLISECONDS.toHours(millis);
        long m = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long s = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }
}
