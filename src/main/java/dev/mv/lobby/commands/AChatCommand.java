package dev.mv.lobby.commands;

import dev.mv.lobby.party.Party;
import dev.mv.lobby.rank.Ranks;
import dev.mv.ptk.Utils;
import dev.mv.ptk.command.AbstractCommand;
import dev.mv.ptk.command.Command;
import dev.mv.ptk.command.CommandRoute;
import dev.mv.ptk.command.CommandRoutes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command("achat")
public class AChatCommand extends AbstractCommand {
    protected AChatCommand() {
        super(new CommandRoutes.Builder()
                .withRoute()
                .withType(CommandRoute.ArgumentType.EXTRA)
                .then()
                .build()
        );
    }

    public void call(Player player, String[] value) {
        String message = String.join(" ", value);
        Bukkit.broadcastMessage(Ranks.getInstance().format(player, message));
    }
}
