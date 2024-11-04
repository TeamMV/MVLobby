package dev.mv.lobby;

import dev.mv.lobby.chat.ChatListener;
import dev.mv.lobby.components.DamageListener;
import dev.mv.lobby.components.InteractListener;
import dev.mv.lobby.components.NPC;
import dev.mv.lobby.conf.LobbyConfig;
import dev.mv.lobby.game.Game;
import dev.mv.lobby.game.JoinListener;
import dev.mv.lobby.party.PartyListener;
import dev.mv.ptk.PluginToolkit;
import dev.mv.ptk.Utils;
import dev.mv.ptk.display.Display;
import dev.mv.ptk.display.Sidebar;
import dev.mv.ptk.style.Chat;
import org.bukkit.entity.Player;

public final class Lobby extends PluginToolkit {
    private static Lobby INSTANCE;

    private static Sidebar sidebar;

    @Override
    public void start() {
        INSTANCE = this;
        LobbyConfig config = LobbyConfig.getInstance();

        require("commands").enable();
        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new PartyListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        sidebar = Sidebar.create()
                .withTitle(Utils.chat("&6&lLobby"))
                .withLine()
                .string(Utils.chat("&7Server: &2" + config.getServerIp()))
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
            Chat.send(player, "&+eSending you back to lobby...");
            player.teleport(LobbyConfig.getInstance().getLobbyWorld().getSpawnLocation());
            Display.getInstance().removeSidebar(player);
            Display.getInstance().addSidebar(player, sidebar);
        } else {
            Chat.send(player, "&+eYou cannot go to the lobby right now!");
        }
    }

    public static Lobby getInstance() {
        return INSTANCE;
    }
}
