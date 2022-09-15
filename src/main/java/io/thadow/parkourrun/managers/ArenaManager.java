package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.ArenasConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaManager {
    private static final ArenaManager arenaManager = new ArenaManager();
    private final List<Arena> arenas = new ArrayList<>();

    public ArenaManager() {
    }

    public static ArenaManager getArenaManager() {
        return arenaManager;
    }

    public Arena getArena(String arenaName) {
        for (Arena arena : arenas) {
            if (arena.getArenaName().equals(arenaName)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(Player player) {
        for (Arena arenas : arenas) {
            if (arenas.getPlayers().contains(player)) {
                return arenas;
            }
        }
        return null;
    }

    public boolean addPlayer(Player player, String arenaName) {
        Arena arena = getArena(arenaName);
        if (arena == null) {
            return false;
        }
        arena.addPlayer(player);
        return true;
    }

    public void joinRandom(Player player) {
        List<Arena> arenas = Utils.getSorted(getArenas());

        for (Arena arena : arenas) {
            if (addPlayer(player, arena.getArenaName())) {
                break;
            }
        }
    }


    public void removePlayer(Player player, boolean silent) {
        Arena arena = null;
        for (Arena arenas : arenas) {
            if (arenas.getPlayers().contains(player)) {
                arena = arenas;
            }
        }

        if (arena == null || !arena.getPlayers().contains(player)) {
            return;
        }
        if (silent) {
            arena.removePlayerSilent(player);
        } else {
            arena.removePlayer(player);
        }
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public void loadArenas() {
        if (ArenasConfiguration.getConfiguration().getConfigurationSection("Arenas") == null)
            return;
        for (String arena : ArenasConfiguration.getConfiguration().getConfigurationSection("Arenas").getKeys(false)) {
            String arenaDisplayName = ArenasConfiguration.getConfiguration().getString("Arenas." + arena + ".Arena Name");
            int minPlayers = ArenasConfiguration.getConfiguration().getInt("Arenas." + arena + ".Min Players");
            int maxPlayers = ArenasConfiguration.getConfiguration().getInt("Arenas." + arena + ".Max Players");
            int waitToStartTime = ArenasConfiguration.getConfiguration().getInt("Arenas." + arena + ".Wait Time To Start");
            int waitToReEnableTime = ArenasConfiguration.getConfiguration().getInt("Arenas." + arena + ".Wait Time To Re-Enable");
            int maxTime = ArenasConfiguration.getConfiguration().getInt("Arenas." + arena + ".Max Time");
            String[] spawnLocationSplit = ArenasConfiguration.getConfiguration().getString("Arenas." + arena + ".Spawn Location").split(";");
            String[] waitLocationSplit = ArenasConfiguration.getConfiguration().getString("Arenas." + arena + ".Wait Location").split(";");
            String winZoneCorner1 = ArenasConfiguration.getConfiguration().getString("Arenas." + arena + ".Win Zone Corner 1");
            String winZoneCorner2 = ArenasConfiguration.getConfiguration().getString("Arenas." + arena + ".Win Zone Corner 2");
            String arenaZoneCorner1 = ArenasConfiguration.getConfiguration().getString("Arenas." + arena + ".Arena Zone Corner 1");
            String arenaZoneCorner2 = ArenasConfiguration.getConfiguration().getString("Arenas." + arena + ".Arena Zone Corner 2");
            World spawnWorld = Bukkit.getWorld(spawnLocationSplit[0]);
            double spawnX = Double.parseDouble(spawnLocationSplit[1]);
            double spawnY = Double.parseDouble(spawnLocationSplit[2]);
            double spawnZ = Double.parseDouble(spawnLocationSplit[3]);
            float spawnYaw = Float.parseFloat(spawnLocationSplit[4]);
            float spawnPitch = Float.parseFloat(spawnLocationSplit[5]);
            Location spawnLocation = new Location(spawnWorld, spawnX, spawnY, spawnZ, spawnPitch, spawnYaw);
            World waitWorld = Bukkit.getWorld(waitLocationSplit[0]);
            double waitX = Double.parseDouble(waitLocationSplit[1]);
            double waitY = Double.parseDouble(waitLocationSplit[2]);
            double waitZ = Double.parseDouble(waitLocationSplit[3]);
            float waitYaw = Float.parseFloat(waitLocationSplit[4]);
            float waitPitch = Float.parseFloat(waitLocationSplit[5]);
            Location waitLocation = new Location(waitWorld, waitX, waitY, waitZ, waitPitch, waitYaw);
            Map<Integer, String> checkpoints = new HashMap<>();
            int totalCheckpoints = ArenasConfiguration.getConfiguration().getInt("Arenas." + arena + ".Total Checkpoints");
            if (ArenasConfiguration.getConfiguration().contains("Arenas." + arena + ".Checkpoints.1")) {
                for (int checkpoint = 1; checkpoint <= totalCheckpoints; checkpoint++) {
                    if (ArenasConfiguration.getConfiguration().contains("Arenas." + arena + ".Checkpoints." + checkpoint + ".Location")
                            && ArenasConfiguration.getConfiguration().contains("Arenas." + arena + ".Checkpoints." + checkpoint + ".Corner 1")
                            && ArenasConfiguration.getConfiguration().contains("Arenas." + arena + ".Checkpoints." + checkpoint + ".Corner 2")) {
                        Bukkit.getConsoleSender().sendMessage("ID: " + checkpoint);
                        String location = "Arenas." + arena + ".Checkpoints." + checkpoint + ".Location";
                        String corner1 = "Arenas." + arena + ".Checkpoints." + checkpoint + ".Corner 1";
                        String corner2 = "Arenas." + arena + ".Checkpoints." + checkpoint + ".Corner 2";
                        String full = ArenasConfiguration.getConfiguration().getString(location) + "/-/" + ArenasConfiguration.getConfiguration().getString(corner1) + "/-/" + ArenasConfiguration.getConfiguration().getString(corner2);
                        checkpoints.put(checkpoint, full);
                        Bukkit.getConsoleSender().sendMessage("Checkpoint: " + full);
                    }
                }
            }
            boolean enabled = ArenasConfiguration.getConfiguration().getBoolean("Arenas." + arena + ".Enabled");
            Arena arenita = new Arena(arena, arenaDisplayName, minPlayers, maxPlayers, spawnLocation, waitLocation, waitToStartTime, waitToReEnableTime, maxTime, winZoneCorner1, winZoneCorner2, arenaZoneCorner1, arenaZoneCorner2, checkpoints, enabled);
            getArenas().add(arenita);
            Bukkit.getConsoleSender().sendMessage("-----------------------------------------");
            Bukkit.getConsoleSender().sendMessage("Arena Encontrada: " + arenaDisplayName);
            Bukkit.getConsoleSender().sendMessage("Min Players: " + minPlayers);
            Bukkit.getConsoleSender().sendMessage("Max Players: " + maxPlayers);
            Bukkit.getConsoleSender().sendMessage("Spawn Location: " + spawnLocation);
            Bukkit.getConsoleSender().sendMessage("Wait Time To Start: " + waitToStartTime);
            Bukkit.getConsoleSender().sendMessage("Wait Time To Re-Enable: " + waitToReEnableTime);
            Bukkit.getConsoleSender().sendMessage("Max Time: " + maxTime);
            Bukkit.getConsoleSender().sendMessage("-----------------------------------------");
        }
    }
}
