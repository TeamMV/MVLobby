package dev.mv.lobby.game;

import dev.mv.ptk.Utils;
import dev.mv.utilsx.collection.Vec;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;

public abstract class Game {

    private static HashMap<Player, Game> activeGames = new HashMap<>();
    private static HashMap<Player, Game> rejoinGames = new HashMap<>();

    protected Vec<Player> players = new Vec<>();
    protected Vec<Player> leftPlayers = new Vec<>();

    protected Game() {

    }

    protected final void endGame() {
        players.forEach(activeGames::remove);
    }

    public final void join(Player player) {
        if (currentGameState() == GameState.FINISHED) return;
        if (currentGameState() == GameState.PLAYING) {
            if (getRejoinKind() == RejoinKind.PROHIBITED && !alwaysJoinable()) return;
            int idx = leftPlayers.indexOf(player);
            if (idx >= 0) {
                leftPlayers.remove(idx);
                activeGames.put(player, this);
                rejoinGames.remove(player);
                onPlayerRejoin(player);
                return;
            } else if (!alwaysJoinable()) return;
        }
        activeGames.put(player, this);
        onPlayerJoin(player);
    }

    public final void leave(Player player) {
        if (currentGameState() == GameState.PLAYING) {
            if (getRejoinKind() == RejoinKind.FORCED) {
                player.sendMessage(Utils.chat("&cYou cannot leave mid game!"));
                return;
            }
            else if (getRejoinKind() == RejoinKind.ALLOWED) {
                leftPlayers.push(player);
                rejoinGames.put(player, this);
            }
        }
        activeGames.remove(player);
        onPlayerLeave(player);
    }

    public final void forceLeave(Player player) {
        if (currentGameState() == GameState.PLAYING) {
            if (getRejoinKind() != RejoinKind.PROHIBITED) {
                leftPlayers.push(player);
                rejoinGames.put(player, this);
            }
        }
        activeGames.remove(player);
        onPlayerLeave(player);
    }

    static void onConnect(Player player) {
        if (rejoinGames.containsKey(player)) {
            Game game = rejoinGames.get(player);
            if (game == null) {
                rejoinGames.remove(player);
                return;
            }
            if (game.currentGameState() == GameState.PLAYING && game.getRejoinKind() == RejoinKind.FORCED) {
                game.join(player);
            } else if (game.currentGameState() != GameState.PLAYING) {
                rejoinGames.remove(player);
            }
        }
    }

    static void onDisconnect(Player player) {
        if (activeGames.containsKey(player)) {
            activeGames.get(player).forceLeave(player);
        }
    }

    public abstract int playersNeeded();
    public abstract GameState currentGameState();
    public abstract void onPlayerRejoin(Player player);
    public abstract void onPlayerJoin(Player player);
    public abstract void onPlayerLeave(Player player);

    public enum RejoinKind {
        PROHIBITED,
        ALLOWED,
        FORCED
    }

    public enum GameState {
        PREGAME,
        PLAYING,
        FINISHED
    }

    public abstract String getDisplayName();
    public abstract String getKey();
    public abstract RejoinKind getRejoinKind();
    public abstract boolean alwaysJoinable();
}