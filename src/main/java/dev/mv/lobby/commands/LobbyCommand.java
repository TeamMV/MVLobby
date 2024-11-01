package dev.mv.lobby.commands;

import dev.mv.lobby.Lobby;
import dev.mv.ptk.command.AbstractCommand;
import dev.mv.ptk.command.Command;
import dev.mv.ptk.command.CommandRoutes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command("lobby")
public class LobbyCommand extends AbstractCommand {
    public LobbyCommand() {
        super(new CommandRoutes.Builder()
                .withRoute()
                .then()
                .build()
        );
    }

    public void call(Player player) {
        Lobby.sendToLobby(player);
    }
}
