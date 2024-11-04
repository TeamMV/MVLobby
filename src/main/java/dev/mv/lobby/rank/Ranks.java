package dev.mv.lobby.rank;

import dev.mv.lobby.Lobby;
import dev.mv.ptk.PluginToolkit;
import dev.mv.ptk.Utils;
import dev.mv.ptk.module.SingletonModule;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class Ranks extends SingletonModule {
    private static Ranks INSTANCE;

    private Ranks(PluginToolkit toolkit) {
        super(toolkit);
    }

    public String formatMessage(Player player, String message) {
        return Utils.chat("&f%s: &7%s", player.getName(), message);
    }

    public String formatName(Player player) {
        return Utils.chat("&f%s", player.getName());
    }

    public static String format(Player player, String message) {
        return getInstance().formatMessage(player, message);
    }

    public static String format(Player player) {
        return getInstance().formatName(player);
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
