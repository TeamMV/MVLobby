package dev.mv.lobby.rank;

import dev.mv.lobby.Lobby;
import dev.mv.ptk.PluginToolkit;
import dev.mv.ptk.Utils;
import dev.mv.ptk.module.SingletonModule;
import org.bukkit.entity.Player;

public class Ranks extends SingletonModule {
    private static Ranks INSTANCE;

    private Ranks(PluginToolkit toolkit) {
        super(toolkit);
    }

    public String format(Player player, String message) {
        return Utils.chat("&f%s: &7%s", player.getName(), message);
    }

    @Override
    protected void clean() {

    }

    @Override
    public String getId() {
        return "ranks";
    }

    public static Ranks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ranks(Lobby.getInstance());
        }
        return INSTANCE;
    }
}
