package dev.mv.lobby.commands;

import dev.mv.lobby.party.Invite;
import dev.mv.lobby.party.Party;
import dev.mv.utilsx.UtilsX;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartyCommandTabCompleter implements TabCompleter {
    private static List<String> commands = new ArrayList<>() {{
        add("i");
        add("invite");
        add("accept");
        add("decline");
        add("leave");
        add("list");
        add("l");
        add("transfer");
        add("settings");
        add("disband");
        add("purge");
        add("kick");
    }};

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> comp = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], commands, comp);
            StringUtil.copyPartialMatches(args[0], getPlayerNames(), comp);
        } else if (args.length == 2) {
            if (UtilsX.isAnyOf(args[0], "invite", "i")) {
                StringUtil.copyPartialMatches(args[0], getPlayerNames(), comp);
            } else if (UtilsX.isAnyOf(args[0], "transfer", "kick")) {
                Player player = (Player) sender;
                Party party = Party.findParty(player);
                if (party != null && party.isLeader(player)) {
                    List<String> playerNames = party.getPlayers().iterCopied().map(p -> p.getName()).collect();
                    StringUtil.copyPartialMatches(args[1], playerNames, comp);
                }
            } else if (UtilsX.isAnyOf(args[0], "accept", "decline")) {
                Player player = (Player) sender;
                List<String> playerNames = Invite.findInvites(player).map(i -> i.getInviter().getName()).collect();
                StringUtil.copyPartialMatches(args[1], playerNames, comp);
            }
        }
        return comp;
    }

    private List<String> getPlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
    }
}
