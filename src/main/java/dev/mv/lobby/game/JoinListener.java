package dev.mv.lobby.game;

import dev.mv.ptk.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(Utils.chat("&2&l%s joined mvteam.dev!", e.getPlayer().getName()));
        Game.onConnect(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Game.onDisconnect(e.getPlayer());
    }
}
