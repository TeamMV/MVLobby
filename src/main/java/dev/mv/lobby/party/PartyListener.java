package dev.mv.lobby.party;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyListener implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Party party = Party.findParty(e.getPlayer());
        if (party != null) {
            party.disconnect(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Party party = Party.findParty(e.getPlayer());
        if (party != null) {
            party.reconnect(e.getPlayer());
        }
    }
}
