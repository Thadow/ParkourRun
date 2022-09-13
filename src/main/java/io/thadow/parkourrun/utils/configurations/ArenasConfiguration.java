package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.managers.CheckpointManager;
import io.thadow.parkourrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class ArenasConfiguration {
    private static FileConfiguration configuration = null;
    private static File configurationFile = null;

    public static FileConfiguration getConfiguration() {
        if (configuration == null)
            reloadConfiguration();
        return configuration;
    }

    public static void reloadConfiguration() {
        if (configuration == null)
            configurationFile = new File(Main.getInstance().getDataFolder(), "arenas.yml");
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
        Reader reader = new InputStreamReader(Main.getInstance().getResource("arenas.yml"));
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(reader);
        configuration.setDefaults(yamlConfiguration);
    }

    public static void registerConfiguration() {
        File configuration = new File(Main.getInstance().getDataFolder(), "arenas.yml");
        if (!configuration.exists()) {
            Main.getInstance().saveResource("arenas.yml", false);
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try {
                yamlConfiguration.load(configuration);
            } catch (InvalidConfigurationException | IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void saveConfiguration() {
        try {
            configuration.save(configurationFile);
        } catch (IOException | NullPointerException exception) {
            exception.printStackTrace();
        }
        reloadConfiguration();
    }

    public static boolean createArena(Player player, String arenaID, int minPlayers, int maxPlayers, String arenaName) {
        if (ArenaManager.getArenaManager().getArena(arenaID) != null) {
            String message = MessagesConfiguration.getPath("Messages.Arena.Arena Already Exists");
            message = Utils.format(message);
            player.sendMessage(message);
            return false;
        }
        getConfiguration().set("Arenas." + arenaID + ".Enabled", false);
        getConfiguration().set("Arenas." + arenaID + ".Arena Name", arenaName);
        getConfiguration().set("Arenas." + arenaID + ".Wait Time To Start", 30);
        getConfiguration().set("Arenas." + arenaID + ".Wait Time To Re-Enable", 5);
        getConfiguration().set("Arenas." + arenaID + ".Min Players", minPlayers);
        getConfiguration().set("Arenas." + arenaID + ".Max Players", maxPlayers);
        getConfiguration().set("Arenas." + arenaID + ".Max Time", 120);
        getConfiguration().set("Arenas." + arenaID + ".Spawn Location", "world;1;1;1;1.0;1.0");
        getConfiguration().set("Arenas." + arenaID + ".Wait Location", "world;1;1;1;1.0;1.0");
        getConfiguration().set("Arenas." + arenaID + ".Win Zone Corner 1", "1;1;1");
        getConfiguration().set("Arenas." + arenaID + ".Win Zone Corner 2", "1;1;1");
        getConfiguration().set("Arenas." + arenaID + ".Arena Zone Corner 1", "1;1;1");
        getConfiguration().set("Arenas." + arenaID + ".Arena Zone Corner 2", "1;1;1");
        getConfiguration().set("Arenas." + arenaID + ".Total Checkpoints", 0);
        saveConfiguration();
        Location spawnLocation = new Location(Bukkit.getWorld("world"), 1.0, 1.0, 1.0, 1.0f, 1.0f);
        Location waitLocation = new Location(Bukkit.getWorld("world"), 1.0, 1.0, 1.0, 1.0f, 1.0f);
        Arena arena = new Arena(arenaID, arenaName, minPlayers, maxPlayers, spawnLocation, waitLocation, 30, 5, 120, "1;1;1", "1;1;1", "1;1;1", "1;1;1", null, false);
        ArenaManager.getArenaManager().getArenas().add(arena);
        return true;
    }

    public static boolean deleteArena(String arenaID) {
        if (ArenaManager.getArenaManager().getArena(arenaID) != null) {
            getConfiguration().set("Arenas." + arenaID + ".Enabled", null);
            getConfiguration().set("Arenas." + arenaID + ".Arena Name", null);
            getConfiguration().set("Arenas." + arenaID + ".Wait Time To Start", null);
            getConfiguration().set("Arenas." + arenaID + ".Wait Time To Re-Enable", null);
            getConfiguration().set("Arenas." + arenaID + ".Min Players", null);
            getConfiguration().set("Arenas." + arenaID + ".Max Players", null);
            getConfiguration().set("Arenas." + arenaID + ".Max Time", null);
            getConfiguration().set("Arenas." + arenaID + ".Spawn Location", null);
            getConfiguration().set("Arenas." + arenaID + ".Wait Location", null);
            getConfiguration().set("Arenas." + arenaID + ".Win Zone Corner 1", null);
            getConfiguration().set("Arenas." + arenaID + ".Win Zone Corner 2", null);
            getConfiguration().set("Arenas." + arenaID + ".Arena Zone Corner 1", null);
            getConfiguration().set("Arenas." + arenaID + ".Arena Zone Corner 2", null);
            getConfiguration().set("Arenas." + arenaID + ".Total Checkpoints", null);
            Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
            int totalCheckpoints = CheckpointManager.getCheckpointManager().getTotalCheckpoints(arena);
            if (totalCheckpoints != 0) {
                for (int i = 1; i <= totalCheckpoints; i++) {
                    ArenasConfiguration.getConfiguration().set("Arenas." + arenaID + ".Checkpoints." + i + ".Location", null);
                    ArenasConfiguration.getConfiguration().set("Arenas." + arenaID + ".Checkpoints." + i + ".Corner 1", null);
                    ArenasConfiguration.getConfiguration().set("Arenas." + arenaID + ".Checkpoints." + i + ".Corner 2", null);
                    ArenasConfiguration.getConfiguration().set("Arenas." + arenaID + ".Checkpoints." + i, null);
                }
            }
            if (getConfiguration().contains("Arenas." + arenaID + ".Checkpoints")) {
                getConfiguration().set("Arenas." + arenaID + ".Checkpoints", null);
            }
            getConfiguration().set("Arenas." + arenaID, null);
            ArenaManager.getArenaManager().getArenas().remove(arena);
            ArenasConfiguration.saveConfiguration();
            return true;
        } else {
            return false;
        }
    }
}
