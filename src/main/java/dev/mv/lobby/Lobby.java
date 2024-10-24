package dev.mv.lobby;

import dev.mv.lobby.components.NPC;
import dev.mv.lobby.conf.LobbyConfig;
import dev.mv.lobby.scoreboard.LobbyScoreboard;
import dev.mv.ptk.PluginToolkit;
import dev.mv.ptk.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Lobby extends PluginToolkit {
    public static Lobby INSTANCE;
    public static String SERVER_IP = "mvteam.dev";

    @Override
    public void start() {
        INSTANCE = this;
        LobbyConfig config = LobbyConfig.getInstance();

        require("commands").enable();
        getServer().getPluginManager().registerEvents(new PluginListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);

        LobbyScoreboard.SCOREBOARDS.clear();
        Bukkit.getOnlinePlayers().forEach(LobbyScoreboard::new);
    }

    @Override
    public void stop() {
        for (NPC npc : NPC.NPCS.values()) {
            npc.getEntity().remove();
        }
    }

    public static void sendToLobby(Player player) {
        LobbyScoreboard.SCOREBOARDS.computeIfPresent(player, (p, sb) -> {
            p.sendMessage(Utils.chat("&4&lSending you back to lobby..."));
            player.teleport(LobbyConfig.getInstance().getLobbySpawn());
            sb.setCurrentGame(null);
            return sb;
        });
    }
}
