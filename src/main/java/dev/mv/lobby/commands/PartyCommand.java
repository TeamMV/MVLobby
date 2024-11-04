package dev.mv.lobby.commands;

import dev.mv.lobby.party.Party;
import dev.mv.lobby.party.Invite;
import dev.mv.lobby.rank.Ranks;
import dev.mv.ptk.command.AbstractCommand;
import dev.mv.ptk.command.Command;
import dev.mv.ptk.command.CommandRoute;
import dev.mv.ptk.command.CommandRoutes;
import dev.mv.ptk.style.Chat;
import dev.mv.ptk.utils.A;
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
                Chat.send(player, "&+eYou cannot invite players to your party!");
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
                Chat.send(player, "&+eYou are already in a party! Leave it before joining a new one!");
                return;
            }
            invite.accept();
        } else {
            Chat.send(player, "&+eYou haven't been invited to this party!");
        }
    }

    public void call_decline(Player player, String inviter) {
        Invite invite = Invite.findInvite(player, inviter);
        if (invite != null) {
            invite.decline();
        } else {
            Chat.send(player, "&+eYou haven't been invited to this party!");
        }
    }

    public void call_transfer(Player player, String to) {
        Party party = Party.findParty(player);
        if (party == null) {
            Chat.send(player, "&+eYou are not in a party!");
        } else if (!party.isLeader(player)) {
            Chat.send(player, "&+eYou must be the party leader to do this!");
        } else {
            Player newLeader = Bukkit.getPlayer(to);
            if (party.hasPlayer(newLeader)) {
                party.transfer(newLeader);
            } else {
                Chat.send(player, "&+eThis player isn't in your party!");
            }
        }
    }

    public void call_disband(Player player) {
        Party party = Party.findParty(player);
        if (party == null) {
            Chat.send(player, "&+eYou are not in a party!");
        } else if (!party.isLeader(player)) {
            Chat.send(player, "&+eYou must be the party leader to do this!");
        } else {
            party.disband();
        }
    }

    public void call_purge(Player player) {
        Party party = Party.findParty(player);
        if (party == null) {
            Chat.send(player, "&+eYou are not in a party!");
        } else if (!party.isLeader(player)) {
            Chat.send(player, "&+eYou must be the party leader to do this!");
        } else {
            party.purge();
        }
    }

    public void call_kick(Player player, String name) {
        Party party = Party.findParty(player);
        if (party == null) {
            Chat.send(player, "&+eYou are not in a party!");
        } else if (!party.isLeader(player)) {
            Chat.send(player, "&+eYou must be the party leader to do this!");
        } else {
            Player kicked = Bukkit.getPlayer(name);
            if (!party.hasPlayer(kicked)) {
                Chat.send(player, "&+eThis player is not in your party!");
            }
            party.kick(kicked);
        }
    }

    public void call_leave(Player player) {
        Party party = Party.findParty(player);
        if (party == null) {
            Chat.send(player, "&+eYou are not in a party!");
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
            StringBuilder builder = new StringBuilder(Chat.format(player, "&+pMembers (%d): ", party.getPlayers().len() - 1));
            party.getPlayers().iter().filter(p -> !party.isLeader(p)).forEach(p -> {
                builder.append(Chat.format(player, "%s&+p, ", Ranks.format(p)));
            });
            builder.setLength(builder.length() - 2);

            Party.sendSystemMessage(player, A.s(
                    "&+pLeader: %s".formatted(Ranks.format(party.getLeader())),
                    builder.toString()
            ));
        } else {
            Chat.send(player, "&+eYou are not contained in a party");
        }
    }

    public void call_settings(Player player) {
        Party party = Party.findParty(player);
        if (party == null) {
            Chat.send(player, "&+eYou are not in a party!");
        } else if (!party.isLeader(player)) {
            Chat.send(player, "&+eYou must be the party leader to do this!");
        } else {
            party.settings(player);
        }
    }

    public void call_chat(Player player, String[] args) {
        Party party = Party.findParty(player);
        if (party == null) {
            Chat.send(player, "&+eYou are not in a party!");
            return;
        }
        String message = String.join(" ", args);
        party.sendChatMessage(Ranks.format(player, message));
    }
}
