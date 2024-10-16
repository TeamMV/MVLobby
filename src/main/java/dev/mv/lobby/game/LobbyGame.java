package dev.mv.lobby.game;

import dev.mv.lobby.Lobby;
import dev.mv.ptk.Utils;
import dev.mv.ptk.display.Sidebar;
import dev.mv.ptk.utils.display.DisplayName;
import org.bukkit.entity.Player;

public abstract class LobbyGame {
    private Sidebar sidebar;

    public abstract DisplayName getDisplayName();
    public abstract String getId();

    public abstract void onPlayerJoin(Player player);
    public abstract void onPlayerLeave(Player player);

    public abstract int getLinesScoreboard();
    public abstract void populateScoreBoard(Sidebar.Builder builder);

    public LobbyGame() {
        setupScoreboard();
    }

    public void setupScoreboard() {
        var builder = Sidebar.create()
                .withTitle(Utils.chat(getDisplayName().getAsString()))
                .withLine()
                    .string(Utils.chat("&7Server: &2%s", Lobby.SERVER_IP))
                    .build()
                .withNewLine();
        populateScoreBoard(builder);

        sidebar = builder.build();
    }

    public Sidebar getSidebar() {
        return sidebar;
    }
}
