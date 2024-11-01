package dev.mv.lobby;

import dev.mv.lobby.commands.PartyCommandTabCompleter;
import dev.mv.lobby.components.NPC;
import dev.mv.lobby.conf.LobbyConfig;
import dev.mv.lobby.game.Game;
import dev.mv.lobby.game.JoinListener;
import dev.mv.lobby.party.PartyListener;
import dev.mv.ptk.PluginToolkit;
import dev.mv.ptk.Utils;
import dev.mv.ptk.display.Display;
import dev.mv.ptk.display.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Lobby extends PluginToolkit {
    public static Lobby INSTANCE;
    public static String SERVER_IP = "mvteam.dev";

    private static Sidebar sidebar;

    @Override
    public void start() {
        INSTANCE = this;
        LobbyConfig config = LobbyConfig.getInstance();

        require("commands").enable();
        getServer().getPluginManager().registerEvents(new PluginListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new PartyListener(), this);

        getCommand("p").setTabCompleter(new PartyCommandTabCompleter());

        sidebar = Sidebar.create()
                .withTitle(Utils.chat("&6&lLobby"))
                .withLine()
                .string(Utils.chat("&7Server: &2" + Lobby.SERVER_IP))
                .build()
                .build();
    }

    @Override
    public void stop() {
        for (NPC npc : NPC.NPCS.values()) {
            npc.getEntity().remove();
        }
    }

    public static void sendToLobby(Player player) {
        if (Game.onLobby(player)) {
            player.sendMessage(Utils.chat("&4&lSending you back to lobby!"));
            player.teleport(LobbyConfig.getInstance().getLobbyWorld().getSpawnLocation());
            Display.getInstance().removeSidebar(player);
            Display.getInstance().addSidebar(player, sidebar);
        } else {
            player.sendMessage(Utils.chat("&4You cannot go to the lobby right now!"));
        }
    }
}
