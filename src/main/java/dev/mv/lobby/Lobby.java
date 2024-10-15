package dev.mv.lobby;

import dev.mv.ptk.PluginToolkit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lobby extends PluginToolkit {

    @Override
    public void start() {
        getServer().getPluginManager().registerEvents(new PluginListener(), this);
    }

    @Override
    public void stop() {
        // Plugin shutdown logic
    }

    public static NPC.Builder createNPC(String id) {
        return new NPC.Builder(id);
    }
}
