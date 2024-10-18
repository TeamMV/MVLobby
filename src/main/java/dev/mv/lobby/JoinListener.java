package dev.mv.lobby;

import dev.mv.lobby.components.NPC;
import dev.mv.lobby.conf.LobbyConfig;
import dev.mv.lobby.game.LobbyGame;
import dev.mv.lobby.scoreboard.LobbyScoreboard;
import dev.mv.ptk.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        new LobbyScoreboard(e.getPlayer());
        e.getPlayer().teleport(LobbyConfig.getInstance().getLobbySpawn());
        e.setJoinMessage(Utils.chat("&2&lWelcome to mvteam.dev!"));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        LobbyScoreboard.SCOREBOARDS.remove(e.getPlayer());
    }

    public void onServerReload(ServerLoadEvent e) {
        LobbyScoreboard.SCOREBOARDS.clear();
        Bukkit.getOnlinePlayers().forEach(LobbyScoreboard::new);
    }
}
