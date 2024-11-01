package dev.mv.lobby.party;

import dev.mv.lobby.Lobby;
import dev.mv.ptk.Utils;
import dev.mv.utilsx.collection.Vec;
import jline.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Party {
    private static HashMap<Player, Party> parties = new HashMap<>();

    @Nullable
    public static Party findParty(Player member) {
        return parties.get(member);
    }

    public static void format(Player player, String... messages) {
        player.sendMessage(Utils.chat("&9&l------------------------------"));
        for (String msg : messages) {
            player.sendMessage(Utils.chat(msg));
        }
        player.sendMessage(Utils.chat("&9&l------------------------------"));
    }

    public void settings(String[] args) {
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

    public void disband() {
        players.forEach(p -> {
            p.sendMessage(Utils.chat("&cParty has been disbanded!"));
        });
        destroy();
    }

    public void invite(Player inviter, String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            inviter.sendMessage(Utils.chat("&cThe player was not found! Please specify a valid human entity!"));
            return;
        }
        if (hasPlayer(player)) {
            inviter.sendMessage(Utils.chat("&cThis player is already in your party!"));
            return;
        }
        if (Invite.findInvites(player).any(i -> i.getParty() == this)) {
            inviter.sendMessage(Utils.chat("&cThis player already has an active invite to your party!"));
            return;
        }
        Party.format(leader, "&eSent a party invite to &b" + player.getName() + "&e!");
        Invite invite = new Invite(this, player, inviter);
        invites.push(invite);
    }

    public void inviteSuccess(Invite invite) {
        leader.sendMessage(Utils.chat("&6%s joined your party!", invite.getInvitee().getName()));
        players.push(invite.getInvitee());
        parties.put(invite.getInvitee(), this);
        invites.remove(invite);
    }

    public void inviteDeclined(Invite invite) {
        leader.sendMessage(Utils.chat("&6%s Declined your party!", invite.getInvitee().getName()));
        invites.remove(invite);
    }

    public boolean isReady() {
        return disconnectedPlayers.isEmpty();
    }

    public void purge() {
        disconnectedPlayers.forEach((player, t) -> {
            t.cancel();
            remove(player);
            players.forEach(p -> p.sendMessage(Utils.chat("&6%s has been removed from your party because they disconnected!", player.getName())));
        });
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
        leader.sendMessage(Utils.chat("&6You have been promoted to party leader!"));
        players.forEach(p -> {
            if (p != leader) p.sendMessage(Utils.chat("&6%s has been promoted to party leader!", leader.getName()));
        });
    }

    public void disconnect(Player player) {
        if (!hasPlayer(player)) return;
        if (leader == player) {
            players.forEach(p -> {
                p.sendMessage(Utils.chat("&cParty leader has left, party will be disbanded in 60 seconds!"));
            });
        } else {
            players.forEach(p -> {
                p.sendMessage(Utils.chat("&c%s has left, they will be removed from the party in 60 seconds!", player.getName()));
            });
        }
        BukkitTask task = Bukkit.getScheduler().runTaskLater(Lobby.INSTANCE, () -> {
            if (!hasPlayer(player)) return;
            if (leader == player) {
                players.forEach(p -> {
                    p.sendMessage(Utils.chat("&cParty has been disbanded because the party leader disconnected!"));
                });
                destroy();
            } else {
                remove(player);
                players.forEach(p -> p.sendMessage(Utils.chat("&6%s has been removed from your party because they disconnected!", player.getName())));
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
        players.forEach(p -> p.sendMessage(Utils.chat("&6%s has been kicked from your party!", player.getName())));
        player.setVelocity(new Vector(100, 100, 100));
        player.sendMessage("Kick!!!");
    }

    public void leave(Player player) {
        if (!hasPlayer(player)) return;
        if (leader == player) {
            players.forEach(p -> {
                p.sendMessage(Utils.chat("&cParty has been disbanded because the party leader left!"));
            });
            destroy();
        } else {
            remove(player);
            players.forEach(p -> p.sendMessage(Utils.chat("&6%s has left your party!", player.getName())));
        }
    }

    private void remove(Player player) {
        parties.remove(player);
        players.remove(player);
    }

    private void destroy() {
        players.forEach(parties::remove);
        invites.forEach(Invite::partyDestroyed);
    }
}
