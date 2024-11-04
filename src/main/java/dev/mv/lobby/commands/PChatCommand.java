package dev.mv.lobby.commands;

import dev.mv.lobby.chat.ChatListener;
import dev.mv.lobby.party.Party;
import dev.mv.lobby.rank.Ranks;
import dev.mv.ptk.Utils;
import dev.mv.ptk.command.AbstractCommand;
import dev.mv.ptk.command.Command;
import dev.mv.ptk.command.CommandRoute;
import dev.mv.ptk.command.CommandRoutes;
import dev.mv.ptk.style.Chat;
import dev.mv.utilsx.UtilsX;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command("pchat")
public class PChatCommand extends AbstractCommand {
    public PChatCommand() {
        super(new CommandRoutes.Builder()
                .withRoute()
                .withType(CommandRoute.ArgumentType.EXTRA)
                .then()
                .build()
        );
    }

    public void call(Player player, String[] value) {
        Party party = Party.findParty(player);
        if (party == null) {
            Chat.send(player, "&+eYou are not in a party!");
            return;
        }
        String message = String.join(" ", value);
        party.sendChatMessage(Ranks.getInstance().format(player, message));
    }
}
