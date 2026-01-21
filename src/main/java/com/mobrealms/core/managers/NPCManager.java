package com.mobrealms.core.managers;

import com.mobrealms.core.MobRealmsCore;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCManager {

    private final MobRealmsCore plugin;
    private final List<UUID> npcList;

    public NPCManager(MobRealmsCore plugin) {
        this.plugin = plugin;
        this.npcList = new ArrayList<>();
        loadAndSpawnNPCs();
    }

    private void loadAndSpawnNPCs() {
        plugin.getDatabaseManager().loadLocation("npc", "begin_adventure").thenAccept(location -> {
            if (location != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    spawnBeginAdventureNPC(location, false);
                    if (plugin.getConfigManager().isDebug()) {
                        plugin.getLogger().info("Loaded and spawned Begin Adventure NPC from saved location");
                    }
                });
            }
        });

        plugin.getDatabaseManager().loadLocation("npc", "shane").thenAccept(location -> {
            if (location != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    spawnShaneNPC(location, false);
                    if (plugin.getConfigManager().isDebug()) {
                        plugin.getLogger().info("Loaded and spawned Shane NPC from saved location");
                    }
                });
            }
        });

        plugin.getDatabaseManager().loadLocation("npc", "kendra").thenAccept(location -> {
            if (location != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    spawnKendraNPC(location, false);
                    if (plugin.getConfigManager().isDebug()) {
                        plugin.getLogger().info("Loaded and spawned Kendra NPC from saved location");
                    }
                });
            }
        });

        plugin.getDatabaseManager().loadLocation("npc", "baldric").thenAccept(location -> {
            if (location != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    spawnBaldricNPC(location, false);
                    if (plugin.getConfigManager().isDebug()) {
                        plugin.getLogger().info("Loaded and spawned Baldric NPC from saved location");
                    }
                });
            }
        });
    }

    public void spawnBeginAdventureNPC(Location location) {
        spawnBeginAdventureNPC(location, true);
    }

    private void spawnBeginAdventureNPC(Location location, boolean saveLocation) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        villager.setCustomName(plugin.getMessageManager().getMessage("npc.begin_adventure.name"));
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);

        villager.setMetadata("mobrealms_npc", new FixedMetadataValue(plugin, "begin_adventure"));

        npcList.add(villager.getUniqueId());

        if (saveLocation) {
            plugin.getDatabaseManager().saveLocation("npc", "begin_adventure", location);
        }

        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Spawned Begin Adventure NPC at " + location);
        }
    }

    public boolean isBeginAdventureNPC(org.bukkit.entity.Entity entity) {
        if (!entity.hasMetadata("mobrealms_npc")) {
            return false;
        }

        String npcType = entity.getMetadata("mobrealms_npc").get(0).asString();
        return "begin_adventure".equals(npcType);
    }

    public void spawnShaneNPC(Location location) {
        spawnShaneNPC(location, true);
    }

    private void spawnShaneNPC(Location location, boolean saveLocation) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        villager.setCustomName(plugin.getMessageManager().getMessage("npc.shane.name"));
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);
        villager.setProfession(Villager.Profession.WEAPONSMITH);

        villager.setMetadata("mobrealms_npc", new FixedMetadataValue(plugin, "shane"));

        npcList.add(villager.getUniqueId());

        if (saveLocation) {
            plugin.getDatabaseManager().saveLocation("npc", "shane", location);
        }

        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Spawned Shane NPC at " + location);
        }
    }

    public void spawnKendraNPC(Location location) {
        spawnKendraNPC(location, true);
    }

    private void spawnKendraNPC(Location location, boolean saveLocation) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        villager.setCustomName(plugin.getMessageManager().getMessage("npc.kendra.name"));
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);
        villager.setProfession(Villager.Profession.MASON);

        villager.setMetadata("mobrealms_npc", new FixedMetadataValue(plugin, "kendra"));

        npcList.add(villager.getUniqueId());

        if (saveLocation) {
            plugin.getDatabaseManager().saveLocation("npc", "kendra", location);
        }

        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Spawned Kendra NPC at " + location);
        }
    }

    public void spawnBaldricNPC(Location location) {
        spawnBaldricNPC(location, true);
    }

    private void spawnBaldricNPC(Location location, boolean saveLocation) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        villager.setCustomName(plugin.getMessageManager().getMessage("npc.baldric.name"));
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);
        villager.setProfession(Villager.Profession.LIBRARIAN);

        villager.setMetadata("mobrealms_npc", new FixedMetadataValue(plugin, "baldric"));

        npcList.add(villager.getUniqueId());

        if (saveLocation) {
            plugin.getDatabaseManager().saveLocation("npc", "baldric", location);
        }

        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Spawned Baldric NPC at " + location);
        }
    }

    public boolean isShaneNPC(org.bukkit.entity.Entity entity) {
        if (!entity.hasMetadata("mobrealms_npc")) {
            return false;
        }

        String npcType = entity.getMetadata("mobrealms_npc").get(0).asString();
        return "shane".equals(npcType);
    }

    public boolean isKendraNPC(org.bukkit.entity.Entity entity) {
        if (!entity.hasMetadata("mobrealms_npc")) {
            return false;
        }

        String npcType = entity.getMetadata("mobrealms_npc").get(0).asString();
        return "kendra".equals(npcType);
    }

    public boolean isBaldricNPC(org.bukkit.entity.Entity entity) {
        if (!entity.hasMetadata("mobrealms_npc")) {
            return false;
        }

        String npcType = entity.getMetadata("mobrealms_npc").get(0).asString();
        return "baldric".equals(npcType);
    }

    public boolean isQuestNPC(org.bukkit.entity.Entity entity) {
        if (!entity.hasMetadata("mobrealms_npc")) {
            return false;
        }

        String npcType = entity.getMetadata("mobrealms_npc").get(0).asString();
        return "quest_npc".equals(npcType);
    }

    public void removeAllNPCs() {
        for (UUID uuid : npcList) {
            plugin.getServer().getWorlds().forEach(world -> {
                world.getEntities().stream()
                    .filter(entity -> entity.getUniqueId().equals(uuid))
                    .forEach(org.bukkit.entity.Entity::remove);
            });
        }
        npcList.clear();
    }
}
