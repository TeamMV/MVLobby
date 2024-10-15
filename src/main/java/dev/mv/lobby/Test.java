package dev.mv.lobby;

import dev.mv.ptk.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public class Test {
    public static void main(String[] args) {
        World world = null;
        State<Integer> players = new State<>(Bukkit.getOnlinePlayers().size());

        NPC npc = Lobby.createNPC("my_npc")
                .withNewEntity()
                    .withPosition(new Location(world, 0, 0, 0))
                    .withType(EntityType.PIG)
                    .build()
                .withDisplayName()
                    .string("&6Currently ")
                    .value(players, "&7")
                    .string(" online!")
                    .build()
                .withClickAction()
                    .build()
                .build();
    }
}
