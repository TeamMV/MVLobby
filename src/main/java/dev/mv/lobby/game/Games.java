package dev.mv.lobby.game;

import dev.mv.lobby.party.Party;
import dev.mv.ptk.PluginToolkit;
import dev.mv.ptk.Ptk;
import dev.mv.ptk.Utils;
import dev.mv.ptk.module.SingletonModule;
import dev.mv.ptk.style.Chat;
import dev.mv.utilsx.collection.Vec;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;

public class Games extends SingletonModule {
    private HashMap<String, Supplier<Game>> constructors = new HashMap<>();
    private HashMap<String, Vec<Game>> activeGames = new HashMap<>();
    private HashMap<String, LobbyGame> gameInfo = new HashMap<>();
    private HashMap<Class<? extends Game>, String> gameIds = new HashMap<>();

    private static Games INSTANCE;

    private Games(PluginToolkit toolkit) {
        super(toolkit);
        Utils.registerListener(classes -> {
            classes.iterCopied()
                .filter(clazz -> Game.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(LobbyGame.class))
                .forEach(c -> {
                    Class<? extends Game> clazz = (Class<? extends Game>) c;
                    if (clazz == Game.class) return;
                    LobbyGame game = clazz.getAnnotation(LobbyGame.class);
                    try {
                        Constructor<? extends Game> constructor = clazz.getConstructor();
                        constructors.put(game.value(), () -> {
                            try {
                                return constructor.newInstance();
                            } catch (Exception ignore) {
                                System.err.println("Failed to create new game " + game.value());
                                return null;
                            }
                        });
                        gameIds.put(clazz, game.value());
                        gameInfo.put(game.value(), game);
                    } catch (Exception ignore) {
                        System.err.println("Failed to load game " + game.value());
                    }
                });
        });
    }

    void end(Game game) {
        String id = getGameId(game);
        if (id == null) return;
        activeGames.get(id).remove(game);
    }

    @Override
    protected void clean() {

    }

    private Game findGame(String gameId, int players) {
        LobbyGame info = gameInfo.get(gameId);
        if (info == null) return null;
        if (info.maxPlayers() >= 0 && players > info.maxPlayers()) return null;

        Vec<Game> games = activeGames.get(gameId);
        if (games == null) {
            Game game = constructors.get(gameId).get();
            games = new Vec<>(game);
            activeGames.put(gameId, games);
            return game;
        }
        Vec<Game> validGames = games.iterCopied()
            .filter(g -> g.currentGameState() == Game.GameState.PREGAME || ((g.currentGameState() == Game.GameState.PLAYING) && g.alwaysJoinable()))
            .filter(g -> info.maxPlayers() < 0 || info.maxPlayers() - g.currentPlayers() >= players)
                .collect();

        if (validGames.isEmpty()) {
            Game game = constructors.get(gameId).get();
            activeGames.get(gameId).push(game);
            return game;
        }

        Game[] arr = validGames.toArray();
        Arrays.sort(arr, (a, b) -> b.currentPlayers() - a.currentPlayers());

        return arr[0];
    }

    public void joinGame(Player player, String gameId) {
        LobbyGame info = gameInfo.get(gameId);
        if (info == null) {
            Chat.send(player, "&+eThis game does not exist!");
            return;
        }
        Party party = Party.findParty(player);
        if (party == null) {
            Game game = findGame(gameId, 1);
            if (game == null) {
                Chat.send(player, "&+eThere was an error joining this game!");
                return;
            }
            game.join(player);
        } else {
            if (!party.isLeader(player)) {
                Chat.send(player, "&+eYou cannot join a game if you are not the party leader!");
                return;
            }
            if (!party.isReady()) {
                Chat.send(player, "&+eYou cannot join a game if players in your party are offline!");
                return;
            }
            if (info.maxPlayers() >= 0 && party.getPlayers().len() > info.maxPlayers()) {
                Chat.send(player, "&+eYour party is too big to join this game (Max players: %d)! Discard players to continue.", info.maxPlayers());
                return;
            }
            Game game = findGame(gameId, party.getPlayers().len());
            if (game == null) {
                Chat.send(player, "&+eThere was an error joining this game!");
                return;
            }
            party.getPlayers().forEach(game::join);
        }
    }

    public int getTotalPlayers(String gameId) {
        if (!constructors.containsKey(gameId)) return 0;
        return activeGames.get(gameId).iterCopied().fold(0, (i, g) -> i + g.currentPlayers());
    }

    public String getGameId(Game game) {
        return getGameId(game.getClass());
    }

    public String getGameId(Class<? extends Game> clazz) {
        return gameIds.get(clazz);
    }

    @Override
    public String getId() {
        return "games";
    }

    public static Games getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Games(Ptk.getInstance());
        }
        return INSTANCE;
    }
}
