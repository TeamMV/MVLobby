package dev.mv.lobby.components;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        for (NPC npc : NPC.NPCS.values()) {
            if (npc.getEntity().equals(e.getEntity())) {
                e.setCancelled(true);
                break;
            }
        }
    }
}
