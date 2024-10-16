package dev.mv.lobby.commands;

import dev.mv.lobby.scoreboard.LobbyScoreboard;
import dev.mv.ptk.Utils;
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
        LobbyScoreboard.SCOREBOARDS.computeIfPresent(player, (p, sb) -> {
            p.sendMessage(Utils.chat("&4&lSending you back to lobby..."));
            sb.setCurrentGame(null);
            return sb;
        });
    }
}
