package dev.mv.lobby;

import dev.mv.lobby.components.NPC;
import dev.mv.lobby.conf.LobbyConfig;
import dev.mv.lobby.game.LobbyGame;
import dev.mv.lobby.scoreboard.LobbyScoreboard;
import dev.mv.ptk.Ptk;
import dev.mv.ptk.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        new LobbyScoreboard(e.getPlayer());
        Bukkit.getScheduler().runTaskLater(Lobby.INSTANCE, () -> {
            e.getPlayer().teleport(LobbyConfig.getInstance().getLobbySpawn());
        }, 1);
        e.setJoinMessage(Utils.chat("&2&l%s joined mvteam.dev!", e.getPlayer().getName()));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(Lobby.INSTANCE, e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        LobbyScoreboard.SCOREBOARDS.remove(e.getPlayer()).setCurrentGame(null);
    }

    public void onServerReload(ServerLoadEvent e) {
        LobbyScoreboard.SCOREBOARDS.clear();
        Bukkit.getOnlinePlayers().forEach(LobbyScoreboard::new);
    }
}
