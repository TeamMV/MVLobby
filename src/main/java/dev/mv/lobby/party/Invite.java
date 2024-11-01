package dev.mv.lobby.party;

import dev.mv.lobby.Lobby;
import dev.mv.ptk.Utils;
import dev.mv.utilsx.collection.Vec;
import dev.mv.utilsx.sequence.Sequence;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class Invite {
    private static Vec<Invite> invites = new Vec<>();

    public static Invite findInvite(Player invitee, String inviter) {
        return invites.iterCopied().find(i -> i.invitee.equals(invitee) && i.inviter.getName().equals(inviter)).getUnchecked();
    }

    public static Sequence<Invite> findInvites(Player invitee) {
        return invites.iterCopied().filter(i -> i.invitee.equals(invitee));
    }

    private Party party;
    private Player invitee;
    private Player inviter;
    private BukkitTask task;

    public Invite(Party party, Player invitee, Player inviter) {
        this.party = party;
        this.invitee = invitee;
        this.inviter = inviter;

        TextComponent t1;
        if (inviter.equals(party.getLeader())) {
            t1 = new TextComponent(Utils.chat("&6%s invited you to their party! ", inviter.getName()));
        } else {
            t1 = new TextComponent(Utils.chat("&6%s invited you to %s's party! ", inviter.getName(), party.getLeader().getName()));
        }
        TextComponent accept = new TextComponent(Utils.chat("&e&l[accept]"));
        TextComponent space = new TextComponent(" ");
        TextComponent decline = new TextComponent(Utils.chat("&c&l[decline]"));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p accept " + party.getLeader().getName()));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.chat("&6Join party"))));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p decline " + party.getLeader().getName()));
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.chat("&6Decline party"))));
        TextComponent t2 = new TextComponent(Utils.chat("&6 You have &360s."));

        invitee.spigot().sendMessage(t1, accept, space, decline, t2);
        task = Bukkit.getScheduler().runTaskLater(Lobby.INSTANCE, () -> {
            invitee.sendMessage(Utils.chat("&cYou did not accept the party!"));
            invites.remove(this);
        }, 20 * 60);

        invites.push(this);
    }

    public void accept() {
        task.cancel();
        invites.remove(this);
        invitee.sendMessage(Utils.chat("&6Joined %s's party!", party.getLeader().getName()));
        party.inviteSuccess(this);
    }

    public void decline() {
        task.cancel();
        invites.remove(this);
        invitee.sendMessage(Utils.chat("&6Declined %s's party!", party.getLeader().getName()));
        party.inviteDeclined(this);
    }

    public Player getInvitee() {
        return invitee;
    }

    public Player getInviter() {
        return inviter;
    }

    public Party getParty() {
        return party;
    }

    public void partyDestroyed() {
        task.cancel();
        invites.remove(this);
        invitee.sendMessage(Utils.chat("&c%s's party has been destroyed! You cannot join now :(", party.getLeader().getName()));
    }
}
