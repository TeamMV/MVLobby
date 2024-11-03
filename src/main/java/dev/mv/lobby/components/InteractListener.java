package dev.mv.lobby.components;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashSet;
import java.util.function.Consumer;

public class InteractListener implements Listener {
    private static final HashSet<Consumer<PlayerInteractEntityEvent>> interactions = new HashSet<>();
    private static final HashSet<Consumer<EntityDamageByEntityEvent>> leftClickInteractions = new HashSet<>();

    public static void addCallback(Consumer<PlayerInteractEntityEvent> interaction) {
        interactions.add(interaction);
    }

    public static void removeCallback(Consumer<PlayerInteractEntityEvent> interaction) {
        interactions.remove(interaction);
    }

    public static void addLeftClickCallback(Consumer<EntityDamageByEntityEvent> interaction) {
        leftClickInteractions.add(interaction);
    }

    public static void removeLeftClickCallback(Consumer<EntityDamageByEntityEvent> interaction) {
        leftClickInteractions.remove(interaction);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        interactions.forEach(c -> c.accept(e));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            leftClickInteractions.forEach(c -> c.accept(e));
        }
    }
}
