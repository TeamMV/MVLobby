package dev.mv.lobby.components;

import dev.mv.lobby.game.Games;
import org.bukkit.entity.Player;

public class GameClickAction extends ClickAction {

    private String gameId;

    public GameClickAction(String gameId) {
        this(gameId, false);
    }

    public GameClickAction(String gameId, boolean leftClickable) {
        this.gameId = gameId;
        this.leftClickable = leftClickable;
    }

    @Override
    public void callback(Player player) {
        Games.getInstance().joinGame(player, gameId);
    }
}
