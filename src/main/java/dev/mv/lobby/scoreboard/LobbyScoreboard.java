package dev.mv.lobby.scoreboard;

import dev.mv.lobby.Lobby;
import dev.mv.lobby.game.LobbyGame;
import dev.mv.ptk.Utils;
import dev.mv.ptk.display.Display;
import dev.mv.ptk.display.Sidebar;
import dev.mv.ptk.utils.display.DisplayName;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class LobbyScoreboard {
    public static HashMap<Player, LobbyScoreboard> SCOREBOARDS = new HashMap<>();

    private Sidebar currentSidebar;
    private Sidebar lobbySidebar;
    private Player player;
    private Display display;
    private LobbyGame currentGame;

    public LobbyScoreboard(Player player) {
        SCOREBOARDS.put(player, this);
        this.display = Display.getInstance();
        this.player = player;

        lobbySidebar = Sidebar.create()
                .withTitle(Utils.chat("&6&lLobby"))
                .withLine()
                    .string(Utils.chat("&7Server: &2%s", Lobby.SERVER_IP))
                    .build()
                .build();

        setCurrentGame(null);
    }

    public void setCurrentGame(LobbyGame game) {
        display.removeSidebar(player);
        if (game != null) {
            game.onPlayerJoin(player);
            currentSidebar = game.getSidebar(player);
        } else {
            if (currentGame != null) currentGame.onPlayerLeave(player);
            currentSidebar = lobbySidebar;
        }
        currentGame = game;
        display.addSidebar(player, currentSidebar);
    }
}
