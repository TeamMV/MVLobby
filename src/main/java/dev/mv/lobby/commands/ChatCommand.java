package dev.mv.lobby.commands;

import dev.mv.lobby.Lobby;
import dev.mv.lobby.chat.ChatListener;
import dev.mv.lobby.party.Party;
import dev.mv.ptk.command.AbstractCommand;
import dev.mv.ptk.command.Command;
import dev.mv.ptk.command.CommandRoute;
import dev.mv.ptk.command.CommandRoutes;
import dev.mv.ptk.style.Chat;
import dev.mv.utilsx.UtilsX;
import dev.mv.utilsx.collection.Vec;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@Command("chat")
public class ChatCommand extends AbstractCommand {
    public ChatCommand() {
        super(new CommandRoutes.Builder()
                .withRoute()
                .withType(CommandRoute.ArgumentType.STRING)
                .withTabCompleter(new Vec<>("a", "all", "p", "party"))
                .then()
                .withRoute()
                .then()
                .build()
        );
    }

    public void call(Player player) {
        Chat.send(player, "&+dYou are currently in the &+a%s &+dchat", ChatListener.getChatType(player));
    }

    public void call(Player player, String value) {
        if (UtilsX.isAnyOf(value, "all", "a")) {
            ChatListener.setChatType(player, ChatListener.ChatType.ALL);
        } else if (UtilsX.isAnyOf(value, "party", "p")) {
            Party party = Party.findParty(player);
            if (party == null) {
                Chat.send(player, "&+eYou are not in a party!");
                return;
            }
            ChatListener.setChatType(player, ChatListener.ChatType.PARTY);
        } else {
            Chat.send(player, "&+eInvalid chat type!");
        }
    }
}
