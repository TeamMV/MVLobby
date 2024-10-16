package dev.mv.lobby;

import dev.mv.lobby.components.NPC;
import dev.mv.lobby.scoreboard.LobbyScoreboard;
import dev.mv.ptk.PluginToolkit;
import org.bukkit.Bukkit;

public final class Lobby extends PluginToolkit {
    public static String SERVER_IP = "mvteam.dev";

    @Override
    public void start() {
        require("commands").enable();
        getServer().getPluginManager().registerEvents(new PluginListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        LobbyScoreboard.SCOREBOARDS.clear();
        Bukkit.getOnlinePlayers().forEach(LobbyScoreboard::new);
    }

    @Override
    public void stop() {
        // Plugin shutdown logic
    }

    public static NPC.Builder createNPC(String id) {
        return new NPC.Builder(id);
    }
}
