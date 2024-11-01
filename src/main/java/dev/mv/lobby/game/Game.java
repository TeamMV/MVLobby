package dev.mv.lobby.game;

import dev.mv.lobby.Lobby;
import dev.mv.ptk.Utils;
import dev.mv.ptk.display.Sidebar;
import dev.mv.utilsx.collection.Vec;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class Game {

    private static HashMap<Player, Game> activeGames = new HashMap<>();
    private static HashMap<Player, Game> rejoinGames = new HashMap<>();

    protected Vec<Player> players = new Vec<>();
    protected Vec<Player> leftPlayers = new Vec<>();

    protected HashMap<Player, Sidebar> sidebars = new HashMap<>();

    protected Game() {}

    private void makeSidebar(Player player) {
        Sidebar.Builder builder = new Sidebar.Builder();
        builder.withTitle(getDisplayName())
                .withLine()
                .string(Utils.chat("&7Server: &2" + Lobby.SERVER_IP))
                .build()
                .withNewLine();

        populateScoreboard(builder, player);
        Sidebar sidebar = builder.build();
        sidebars.put(player, sidebar);
    }

    protected final void endGame() {
        players.forEach(activeGames::remove);
    }


    // Called when: Player attempts to join game in any way
    public final boolean join(Player player) {
        if (currentGameState() == GameState.FINISHED) return false;
        if (currentGameState() == GameState.PLAYING) {
            if (getRejoinKind() == RejoinKind.PROHIBITED && !alwaysJoinable()) return false;
            int idx = leftPlayers.indexOf(player);
            if (idx >= 0) {
                //rejoin
                leftPlayers.remove(idx);
                activeGames.put(player, this);
                rejoinGames.remove(player);
                onPlayerRejoin(player);
                return true;
            } else if (!alwaysJoinable()) return false;
        }
        //normal join
        activeGames.put(player, this);
        onPlayerJoin(player);
        return true;
    }

    // Called when: Player attempts to leave game
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

    // Called when: Player forcibly leaves game (ex: internet quit)
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

    // static shit...
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
        } else {
            Lobby.sendToLobby(player);
        }
    }

    static void onDisconnect(Player player) {
        if (activeGames.containsKey(player)) {
            activeGames.get(player).forceLeave(player);
        }
    }

    public static boolean onLobby(Player player) {
        Game game = activeGames.get(player);
        if (game == null) return true;
        if (game.getRejoinKind() == RejoinKind.FORCED && game.currentGameState() == GameState.PLAYING) return false;
        game.leave(player);
        return true;
    }

    public abstract int playersNeeded();
    public abstract GameState currentGameState();
    public abstract void onPlayerRejoin(Player player);
    public abstract void onPlayerJoin(Player player);
    public abstract void onPlayerLeave(Player player);

    public abstract void populateScoreboard(Sidebar.Builder builder, Player player);

    public Sidebar getSidebar(Player player) {
        if (sidebars.containsKey(player)) {
            return sidebars.get(player);
        }
        makeSidebar(player);
        return sidebars.get(player);
    }

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