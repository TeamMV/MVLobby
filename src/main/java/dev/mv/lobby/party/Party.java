package dev.mv.lobby.party;

import com.sun.jna.platform.unix.solaris.LibKstat;
import dev.mv.lobby.Lobby;
import dev.mv.lobby.rank.Ranks;
import dev.mv.ptk.style.Chat;
import dev.mv.utilsx.collection.Vec;
import jline.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class Party {
    private static HashMap<Player, Party> parties = new HashMap<>();

    @Nullable
    public static Party findParty(Player member) {
        return parties.get(member);
    }

    public static void sendSystemMessage(Player player, String[] messages) {
        Chat.send(player, "&+S&l---------------------------------------------");
        for (String msg : messages) {
            Chat.send(player, msg);
        }
        Chat.send(player, "&+S&l---------------------------------------------");
    }

    public static void sendSystemMessage(Player player, String message, Object... args) {
        Chat.send(player, "&+S&l---------------------------------------------");
        Chat.send(player, message, args);
        Chat.send(player, "&+S&l---------------------------------------------");
    }

    public static void sendSystemMessage(Player player, Runnable between) {
        Chat.send(player, "&+S&l---------------------------------------------");
        between.run();
        Chat.send(player, "&+S&l---------------------------------------------");
    }

    public void settings(Player player) {
        //nothing for nwo
    }

    public enum InvitePerms {
        LEADER,
        EVERYONE
    }

    private Player leader;
    private Vec<Player> players;
    private HashMap<Player, BukkitTask> disconnectedPlayers;
    private Vec<Invite> invites;
    private InvitePerms invitePerms;

    public Party(Player leader) {
        this.leader = leader;
        this.players = new Vec<>(leader);
        this.invites = new Vec<>();
        this.disconnectedPlayers = new HashMap<>();

        invitePerms = InvitePerms.LEADER;

        parties.put(leader, this);
    }

    public void sendChatMessage(String message) {
        players.forEach(p -> {
            Chat.send(p, "&+SParty &+D> %s", message);
        });
    }

    public void disband() {
        players.forEach(p -> {
            Party.sendSystemMessage(p, "&+eThe party was disbanded.");
        });
        destroy();
    }

    public void invite(Player inviter, String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            Chat.send(inviter, "&+eThe player was not found! Please specify a valid human entity!");
            return;
        }
        if (hasPlayer(player)) {
            Chat.send(inviter, "&+eTThis player is already in your party!");
            return;
        }
        if (Invite.findInvites(player).any(i -> i.getParty() == this)) {
            Chat.send(inviter, "&+eThis player already has an active invite to your party!");
            return;
        }
        Party.sendSystemMessage(inviter, "&+pSent a party invite to %s&+p!", Ranks.format(player));
        players.forEach(p -> {
            if (!p.equals(inviter)) {
                Party.sendSystemMessage(p, "%s &+psent a party invite to %s&+p!", Ranks.format(inviter), Ranks.format(player));
            }
        });
        Invite invite = new Invite(this, player, inviter);
        invites.push(invite);
    }

    public void inviteSuccess(Invite invite) {
        players.forEach(p -> Party.sendSystemMessage(p, "%s &+phas joined your party!", Ranks.format(invite.getInvitee())));
        players.push(invite.getInvitee());
        parties.put(invite.getInvitee(), this);
        invites.remove(invite);
    }

    public void inviteRemoved(Invite invite) {
        invites.remove(invite);
        if (players.len() == 1 && invites.isEmpty()) {
            destroy();
            Party.sendSystemMessage(players.get(0), "&+eThe party was disbanded because all members have left and all invites have expired!");
        }

    }

    public boolean isReady() {
        return disconnectedPlayers.isEmpty();
    }

    public void purge() {
        Vec<Player> removed = new Vec<>();
        disconnectedPlayers.forEach((player, t) -> {
            t.cancel();
            remove(player);
            removed.push(player);
        });
        Vec<String> names = removed.iter().map(Ranks::format).collect();
        players.forEach(p -> Party.sendSystemMessage(p, names.iterCopied().map(s -> s + " &+ehas been removed from your party because they disconnected").<String[]>collect()));
    }

    public Player getLeader() {
        return leader;
    }

    public boolean isLeader(Player potentialLeader) {
        return leader.equals(potentialLeader);
    }

    public boolean canInvite(Player player) {
        if (players.indexOf(player) == -1) return false;
        if (invitePerms == InvitePerms.EVERYONE) return true;
        return isLeader(player);
    }

    public Vec<Player> getPlayers() {
        return players;
    }

    public boolean hasPlayer(Player player) {
        return players.indexOf(player) >= 0;
    }

    public void transfer(Player player) {
        leader = player;
        Party.sendSystemMessage(leader, "&+eYou have been promoted to party leader!");
        players.forEach(p -> {
            if (!p.equals(leader)) Party.sendSystemMessage(p, "%s &+phas been promoted to party leader!", Ranks.format(leader));
        });
    }

    public void disconnect(Player player) {
        if (!hasPlayer(player)) return;
        if (leader == player) {
            players.forEach(p -> {
                Party.sendSystemMessage(p, "%s &+ehas disconnected, the party will be disbanded in 60 seconds!", Ranks.format(player));
            });
        } else {
            players.forEach(p -> {
                Party.sendSystemMessage(p, "%s &+ehas disconnected, they will be removed from the party in 60 seconds!", Ranks.format(player));
            });
        }
        BukkitTask task = Bukkit.getScheduler().runTaskLater(Lobby.getInstance(), () -> {
            if (!hasPlayer(player)) return;
            if (leader == player) {
                players.forEach(p -> {
                    Party.sendSystemMessage(p, "&+eThe party was disbanded because the leader disconnected!");
                });
                destroy();
            } else {
                players.forEach(p -> Party.sendSystemMessage(p, "%s &+ehas been removed from the party because they disconnected!", Ranks.format(player)));
                remove(player);
            }
        }, 60 * 20);
        disconnectedPlayers.put(player, task);
    }

    public void reconnect(Player player) {
        if (!hasPlayer(player)) return;
        BukkitTask task = disconnectedPlayers.get(player);
        if (task != null) task.cancel();
        disconnectedPlayers.remove(player);
    }

    public void kick(Player player) {
        if (!hasPlayer(player)) return;
        remove(player);
        players.forEach(p -> Party.sendSystemMessage(p, "%s &+phas been kicked from your party!", Ranks.format(player)));
    }

    public void leave(Player player) {
        if (!hasPlayer(player)) return;
        if (leader == player) {
            players.forEach(p -> {
                Party.sendSystemMessage(p, "&+eParty has been disbanded because the party leader left!");
            });
            destroy();
        } else {
            players.forEach(p -> Party.sendSystemMessage(p, "%s &+phas left your party!", Ranks.format(player)));
            remove(player);
        }
    }

    private void remove(Player player) {
        parties.remove(player);
        players.remove(player);
        if (players.len() == 1 && invites.isEmpty()) {
            destroy();
            Party.sendSystemMessage(players.get(0), "&+eThe party was disbanded because all members have left and all invites have expired!");
        }
    }

    private void destroy() {
        players.forEach(parties::remove);
        invites.forEach(Invite::partyDestroyed);
    }
}
