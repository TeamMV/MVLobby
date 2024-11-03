package dev.mv.lobby.commands;

import dev.mv.lobby.party.Party;
import dev.mv.lobby.party.Invite;
import dev.mv.lobby.rank.Ranks;
import dev.mv.ptk.Utils;
import dev.mv.ptk.command.AbstractCommand;
import dev.mv.ptk.command.Command;
import dev.mv.ptk.command.CommandRoute;
import dev.mv.ptk.command.CommandRoutes;
import dev.mv.utilsx.collection.Vec;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command("party")
public class PartyCommand extends AbstractCommand {
    public PartyCommand() {
        super(new CommandRoutes.Builder()
                .withRoute()
                .withNamed("leave")
                .then()
                .withRoute()
                .withNamed("list")
                .then()
                .withRoute()
                .withNamed("l")
                .then()
                .withRoute()
                .withNamed("purge")
                .then()
                .withRoute()
                .withNamed("kick")
                .withType(CommandRoute.ArgumentType.STRING)
                .withTabCompleter(PartyCommand::getPartyPlayers)
                .then()
                .withRoute()
                .withNamed("invite")
                .withType(CommandRoute.ArgumentType.STRING)
                .withPlayerTabCompleter()
                .then()
                .withRoute()
                .withNamed("i")
                .withType(CommandRoute.ArgumentType.STRING)
                .withPlayerTabCompleter()
                .then()
                .withRoute()
                .withNamed("accept")
                .withType(CommandRoute.ArgumentType.STRING)
                .withTabCompleter(PartyCommand::getInvitePlayers)
                .then()
                .withRoute()
                .withNamed("decline")
                .withType(CommandRoute.ArgumentType.STRING)
                .withTabCompleter(PartyCommand::getInvitePlayers)
                .then()
                .withRoute()
                .withNamed("transfer")
                .withType(CommandRoute.ArgumentType.STRING)
                .withTabCompleter(PartyCommand::getPartyPlayers)
                .then()
                .withRoute()
                .withNamed("disband")
                .then()
                .withRoute()
                .withNamed("settings")
                .withType(CommandRoute.ArgumentType.EXTRA)
                .then()
                .withRoute()
                .withNamed("chat")
                .withType(CommandRoute.ArgumentType.EXTRA)
                .then()
                .withRoute()
                .withType(CommandRoute.ArgumentType.STRING)
                .withPlayerTabCompleter()
                .then()
                .build()
        );
    }

    private static Vec<String> getPartyPlayers(Player player) {
        Party party = Party.findParty(player);
        if (party == null) {
            return new Vec<>();
        }
        return party.getPlayers().iterCopied().map(Player::getName).collect();
    }

    private static Vec<String> getInvitePlayers(Player player) {
        return Invite.findInvites(player).map(i -> i.getInviter().getName()).collect();
    }

    public void call(Player player, String invitee) {
        call_invite(player, invitee);
    }

    public void call_i(Player player, String invitee) {
        call_invite(player, invitee);
    }

    public void call_invite(Player player, String invitee) {
        Party party = Party.findParty(player);
        if (party != null) {
            if (party.canInvite(player)) {
                party.invite(player, invitee);
            } else {
                player.sendMessage(Utils.chat("&cYou are unable to invite players!"));
            }
        } else {
            party = new Party(player);
            party.invite(player, invitee);
        }
    }

    public void call_accept(Player player, String inviter) {
        Invite invite = Invite.findInvite(player, inviter);
        if (invite != null) {
            if (Party.findParty(player) != null) {
                player.sendMessage(Utils.chat("&cYou are already in a party. Leave it before joining a new one!"));
                return;
            }
            invite.accept();
        } else {
            player.sendMessage(Utils.chat("&cYou haven't been invited to this party!"));
        }
    }

    public void call_decline(Player player, String inviter) {
        Invite invite = Invite.findInvite(player, inviter);
        if (invite != null) {
            invite.decline();
        } else {
            player.sendMessage(Utils.chat("&cYou haven't been invited to this party!"));
        }
    }

    public void call_transfer(Player player, String to) {
        Party party = Party.findParty(player);
        if (party == null) {
            player.sendMessage(Utils.chat("&cYou do not have a party!"));
        } else if (!party.isLeader(player)) {
            player.sendMessage(Utils.chat("&cYou are not the leader of this party!"));
        } else {
            Player newLeader = Bukkit.getPlayer(to);
            if (party.hasPlayer(newLeader)) {
                party.transfer(newLeader);
            } else {
                player.sendMessage(Utils.chat("&cThis player isn't in your party!"));
            }
        }
    }

    public void call_disband(Player player) {
        Party party = Party.findParty(player);
        if (party == null) {
            player.sendMessage(Utils.chat("&cYou do not have a party!"));
        } else if (!party.isLeader(player)) {
            player.sendMessage(Utils.chat("&cYou are not the leader of this party!"));
        } else {
            party.disband();
        }
    }

    public void call_purge(Player player) {
        Party party = Party.findParty(player);
        if (party == null) {
            player.sendMessage(Utils.chat("&cYou do not have a party!"));
        } else if (!party.isLeader(player)) {
            player.sendMessage(Utils.chat("&cYou are not the leader of this party!"));
        } else {
            party.purge();
        }
    }

    public void call_kick(Player player, String name) {
        Party party = Party.findParty(player);
        if (party == null) {
            player.sendMessage(Utils.chat("&cYou do not have a party!"));
        } else if (!party.isLeader(player)) {
            player.sendMessage(Utils.chat("&cYou are not the leader of this party!"));
        } else {
            Player kicked = Bukkit.getPlayer(name);
            if (!party.hasPlayer(kicked)) {
                player.sendMessage(Utils.chat("&cThis player is not in your party!"));
            }
            party.kick(kicked);
        }
    }

    public void call_leave(Player player) {
        Party party = Party.findParty(player);
        if (party == null) {
            player.sendMessage(Utils.chat("&cYou are not in a party!"));
        } else {
            party.leave(player);
        }
    }

    public void call_l(Player player) {
        call_list(player);
    }

    public void call_list(Player player) {
        Party party = Party.findParty(player);
        if (party != null) {
            player.sendMessage(Utils.chat("&6-----------------------------"));
            player.sendMessage(Utils.chat("&6Leader: &3%s", party.getLeader().getName()));
            StringBuilder builder = new StringBuilder(Utils.chat("&6Member: "));
            party.getPlayers().iter().filter(p -> !party.isLeader(p)).forEach(p -> {
                builder.append(Utils.chat("&3%s&6, ", p.getName()));
            });
            builder.setLength(builder.length() - 2);
            player.sendMessage(builder.toString());
            player.sendMessage(Utils.chat("&6-----------------------------"));
        } else {
            player.sendMessage(Utils.chat("&cYou are not contained in a party"));
        }
    }

    public void call_settings(Player player, String[] args) {
        Party party = Party.findParty(player);
        if (party == null) {
            player.sendMessage(Utils.chat("&cYou do not have a party!"));
        } else if (!party.isLeader(player)) {
            player.sendMessage(Utils.chat("&cYou are not the leader of this party!"));
        } else {
            if (args.length != 2) {
                player.sendMessage(Utils.chat("&cInvalid usage: /party settings <setting> <value>"));
                return;
            }
            party.settings(player, args);
        }
    }

    public void call_chat(Player player, String[] args) {
        Party party = Party.findParty(player);
        if (party == null) {
            player.sendMessage(Utils.chat("&cYou are not in a party!"));
            return;
        }
        String message = String.join(" ", args);
        party.sendChatMessage(Ranks.getInstance().format(player, message));
    }
}
