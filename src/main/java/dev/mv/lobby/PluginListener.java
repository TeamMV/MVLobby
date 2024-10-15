package dev.mv.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PluginListener implements Listener {
    private static long counter = 0;
    private static final Map<Long, Consumer<PlayerInteractEntityEvent>> interactions = new HashMap<>();

    public static long interactionCallback(Consumer<PlayerInteractEntityEvent> interaction) {
        interactions.put(counter++, interaction);
        return counter - 1;
    }

    public static void removeCallback(long id) {
        interactions.remove(id);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        interactions.values().forEach(c -> c.accept(e));
    }
}
