package dev.mv.lobby.components;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class ClickAction {

    private HashMap<Entity, Consumer<PlayerInteractEntityEvent>> appliedEntities = new HashMap<>();
    private HashMap<Entity, Consumer<EntityDamageByEntityEvent>> appliedLeftClickEntities = new HashMap<>();
    protected boolean leftClickable = false;

    public void applyTo(Entity entity) {
        if (!appliedEntities.containsKey(entity)) {
            Consumer<PlayerInteractEntityEvent> interaction = e -> {
                if (e.getRightClicked().getEntityId() == entity.getEntityId()) {
                    callback(e.getPlayer());
                }
            };
            InteractListener.addCallback(interaction);
            appliedEntities.put(entity, interaction);
        }
        if (leftClickable && !appliedLeftClickEntities.containsKey(entity)) {
            Consumer<EntityDamageByEntityEvent> leftClickInteraction = e -> {
                if (e.getEntity().getEntityId() == entity.getEntityId()) {
                    callback((Player) e.getDamager());
                    e.setCancelled(true);
                }
            };
            InteractListener.addLeftClickCallback(leftClickInteraction);
            appliedLeftClickEntities.put(entity, leftClickInteraction);
        }
    }

    public void removeFrom(Entity entity) {
        if (appliedEntities.containsKey(entity)) {
            InteractListener.removeCallback(appliedEntities.get(entity));
            appliedEntities.remove(entity);
        }
        if (appliedLeftClickEntities.containsKey(entity)) {
            InteractListener.removeLeftClickCallback(appliedLeftClickEntities.get(entity));
            appliedLeftClickEntities.remove(entity);
        }
    }

    public abstract void callback(Player player);

}
