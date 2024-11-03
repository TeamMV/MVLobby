package dev.mv.lobby.chat;

import dev.mv.lobby.party.Party;
import dev.mv.lobby.rank.Ranks;
import dev.mv.ptk.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class ChatListener implements Listener {
    private final static HashMap<Player, ChatType> selectedChats = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();
        e.setCancelled(true);
        if (!selectedChats.containsKey(player)) {
            selectedChats.put(player, ChatType.ALL);
        }
        ChatType type = selectedChats.get(player);
        if (type == ChatType.PARTY) {
            Party party = Party.findParty(player);
            if (party == null) {
                selectedChats.put(player, ChatType.ALL);
                player.sendMessage(Utils.chat("&cYou are no longer in a party and have been moved to the ALL chat"));
                return;
            }
            party.sendChatMessage(Ranks.getInstance().format(player, message));
        } else {
            Bukkit.broadcastMessage(Ranks.getInstance().format(player, message));
        }
    }

    public static void setChatType(Player player, ChatType type) {
        selectedChats.put(player, type);
    }

    public static ChatType getChatType(Player player) {
        return selectedChats.get(player);
    }

    public enum ChatType {
        ALL,
        PARTY
    }
}
