package dev.mv.lobby.party;

import dev.mv.lobby.Lobby;
import dev.mv.lobby.rank.Ranks;
import dev.mv.ptk.Utils;
import dev.mv.ptk.style.Chat;
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
            t1 = new TextComponent(Chat.format(invitee, "%s &+pinvited you to their party! You have &+n60s &+pto join.", Ranks.format(inviter)));
        } else {
            t1 = new TextComponent(Chat.format(invitee, "%s &+pinvited you to %s&+p's party! You have &+n60s &+pto join.", Ranks.format(inviter), Ranks.format(party.getLeader())));
        }
        TextComponent accept = new TextComponent(Chat.format(invitee, "&++[accept]"));
        TextComponent space = new TextComponent(" ");
        TextComponent decline = new TextComponent(Chat.format(invitee, "&+-[decline]"));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p accept " + party.getLeader().getName()));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Chat.format(invitee, "&++Join party"))));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p decline " + party.getLeader().getName()));
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Chat.format(invitee,"&+-Decline invite"))));

        Party.sendSystemMessage(invitee, () -> {
            invitee.spigot().sendMessage(t1);
            invitee.spigot().sendMessage(accept, space, decline);
        });

        task = Bukkit.getScheduler().runTaskLater(Lobby.getInstance(), () -> {
            Party.sendSystemMessage(invitee, "&+eYour party invite from %s &+ehas expired.", Ranks.format(inviter));
            invites.remove(this);
            party.inviteRemoved(this);
        }, 20 * 60);

        invites.push(this);
    }

    public void accept() {
        task.cancel();
        invites.remove(this);
        Party.sendSystemMessage(invitee, "&+pYou have joined %s&+p's party!", Ranks.format(party.getLeader()));
        party.inviteSuccess(this);
    }

    public void decline() {
        task.cancel();
        invites.remove(this);
        Party.sendSystemMessage(invitee, "&+pYou have declined %s&+p's party invite!", Ranks.format(party.getLeader()));
        party.inviteRemoved(this);
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
    }
}
