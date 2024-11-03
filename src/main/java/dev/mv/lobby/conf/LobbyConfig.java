package dev.mv.lobby.conf;

import dev.mv.lobby.Lobby;
import dev.mv.lobby.components.NPC;
import dev.mv.ptk.PluginToolkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.List;

public class LobbyConfig {
    private static LobbyConfig INSTANCE;

    private FileConfiguration config;

    private World lobbyWorld;
    private Location lobbySpawn;
    private PluginToolkit toolkit;

    private String ip;

    private LobbyConfig() {
        toolkit = Lobby.getInstance();

        toolkit.saveDefaultConfig();

        config = toolkit.getConfig();
        config.addDefault("lobbyWorld", "lobby");
        config.addDefault("lobbyWorldSpawn.x", 0);
        config.addDefault("lobbyWorldSpawn.y", 0);
        config.addDefault("lobbyWorldSpawn.z", 0);

        config.addDefault("npcs", "");
        config.addDefault("ip", "mvteam.dev");
        config.options().copyDefaults(true);
        toolkit.saveConfig();

        ip = config.getString("ip");

        String lobbyWorldName = config.getString("lobbyWorld");

        if (lobbyWorldName != null) lobbyWorld = Bukkit.getWorld(lobbyWorldName);
        if (lobbyWorld == null) {
            lobbyWorld = Bukkit.createWorld(new WorldCreator(lobbyWorldName));
            if (lobbyWorld == null) {
                throw new RuntimeException("World '" + lobbyWorldName + "' does not exist!");
            }
        }

        lobbySpawn = getLocation(lobbyWorld, "lobbyWorldSpawn");

        ConfigurationSection npcList = config.getConfigurationSection("npcs");
        for (String npcId : npcList.getKeys(false)) {
            new NPC(npcId, this);
        }
    }

    public Location getLocation(World world, String name) {
        double x = config.getDouble(name.concat(".x"), 0);
        double y = config.getDouble(name.concat(".y"), 0);
        double z = config.getDouble(name.concat(".z"), 0);
        return new Location(world, x, y, z);
    }

    public static LobbyConfig getInstance() {
        if (INSTANCE == null) INSTANCE = new LobbyConfig();
        return INSTANCE;
    }

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public String getFromNpc(String id, String path) {
        return config.getString("npcs." + id + "." + path);
    }

    public FileConfiguration getInternal() {
        return config;
    }

    public String getServerIp() {
        return ip;
    }
}
