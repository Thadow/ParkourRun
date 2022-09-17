package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

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
            if (arena.getArenaID().equals(arenaName)) {
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
            if (addPlayer(player, arena.getArenaID())) {
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
        File dir = new File(Main.getInstance().getDataFolder(), "/Arenas");
        if (dir.exists()) {
            List<File> files = new ArrayList<>();
            File[] fls = dir.listFiles();
            for (File fl : Objects.requireNonNull(fls)) {
                if (fl.isFile()) {
                    if (fl.getName().endsWith(".yml")) {
                        files.add(fl);
                    }
                }
            }

            for (File file : files) {
                Arena arena = new Arena(file.getName().replace(".yml", ""));
                arenas.add(arena);
            }
        }
    }

    public boolean createArena(String arenaID) {
        File file = new File(Main.getInstance().getDataFolder(), "/Arenas/" + arenaID + ".yml");
        if (!file.exists()) {
            Arena arena = new Arena(arenaID);
            getArenas().add(arena);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteArena(String arenaID) {
        File arenaFile = new File(Main.getInstance().getDataFolder(), "/Arenas/" + arenaID + ".yml");
        if (!arenaFile.exists()) {
            return false;
        }

        Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
        ArenaManager.getArenaManager().getArenas().remove(arena);
        FileUtils.deleteQuietly(arenaFile);
        return true;
    }
}
