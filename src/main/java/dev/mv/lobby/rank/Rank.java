package dev.mv.lobby.rank;

import org.bukkit.entity.Player;

public interface Rank {
    String format(Player player);
    String[] permissions();
    int commandLevel();
}
