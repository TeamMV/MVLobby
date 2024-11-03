package dev.mv.lobby.commands;

import dev.mv.lobby.chat.ChatListener;
import dev.mv.ptk.Utils;
import dev.mv.ptk.command.AbstractCommand;
import dev.mv.ptk.command.Command;
import dev.mv.ptk.command.CommandRoute;
import dev.mv.ptk.command.CommandRoutes;
import dev.mv.utilsx.UtilsX;
import org.bukkit.entity.Player;

@Command("chat")
public class ChatCommand extends AbstractCommand {
    protected ChatCommand() {
        super(new CommandRoutes.Builder()
                .withRoute()
                .withType(CommandRoute.ArgumentType.STRING)
                .then()
                .build()
        );
    }

    public void call(Player player, String value) {
        if (UtilsX.isAnyOf(value, "all", "a")) {
            ChatListener.setChatType(player, ChatListener.ChatType.ALL);
        } else if (UtilsX.isAnyOf(value, "party", "p")) {
            ChatListener.setChatType(player, ChatListener.ChatType.PARTY);
        } else {
            player.sendMessage(Utils.chat("&cInvalid chat type"));
        }
    }
}
