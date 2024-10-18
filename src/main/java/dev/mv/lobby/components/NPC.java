package dev.mv.lobby.components;

import dev.mv.lobby.conf.LobbyConfig;
import dev.mv.ptk.utils.display.DisplayName;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class NPC {
    public static HashMap<String, NPC> NPCS = new HashMap<>();

    private Entity entity;
    private Location location;
    private String id;

    public NPC(String id, LobbyConfig config) {
        this.id = id;
        this.location = config.getLocation(config.getLobbyWorld(), "npcs." + id + ".location");
        String skin = config.getFromNpc(id, "entity.skin");
        if (skin != null) {
            //todo: create npc
            entity = config.getLobbyWorld().spawnEntity(location, EntityType.VILLAGER);
        } else {
            String entityTypeName = config.getFromNpc(id, "entity");
            entity = config.getLobbyWorld().spawnEntity(location, EntityType.valueOf(entityTypeName));
        }
        Bukkit.broadcastMessage("Spawning npc " + id + ": " + entity);
        if (entity instanceof LivingEntity le) {
            le.setAI(false);
            le.setGravity(false);
        }

        NPCS.put(id, this);
    }

    public void setDisplayName(DisplayName name) {
        name.applyTo(entity);
    }

    public void setClickAction(ClickAction clickAction) {
        clickAction.applyTo(entity);
    }

    public Entity getEntity() {
        return entity;
    }

    public Location getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }
}
