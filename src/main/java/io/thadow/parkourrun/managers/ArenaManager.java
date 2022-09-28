package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
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

    public Arena getArenaByID(String arenaID) {
        for (Arena arena : arenas) {
            if (arena.getArenaID().equals(arenaID)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArenaByName(String name) {
        for (Arena arena : arenas) {
            String stripName = ChatColor.stripColor(arena.getArenaDisplayName());
            if (stripName.equals(name)) {
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

    public boolean addPlayer(Player player, String arenaID) {
        Arena arena = getArenaByID(arenaID);
        if (arena == null) {
            arena = getArenaByName(arenaID);
        }
        if (arena == null) {
            return false;
        }
        arena.addPlayer(player);
        return true;
    }

    public boolean joinRandom(Player player) {
        List<Arena> arenas = Utils.getSorted(getArenas());

        for (Arena arena : arenas) {
            if (arena.getArenaStatus() == ArenaStatus.STARTING) {
                if (arena.getPlayers().size() != arena.getMaxPlayers()) {
                    return handleJoin(player, arena);
                } else if (arena.getArenaStatus() == ArenaStatus.WAITING) {
                    return handleJoin(player, arena);
                }
            }
        }
        return false;
    }

    public boolean handleJoin(Player player, Arena arena) {
        if (!Main.isLobbyPresent()) {
            String message = Main.getMessagesConfiguration().getString("Unknown Lobby");
            message = Utils.format(message);
            player.sendMessage(message);
            return false;
        }
        if (arena == null) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
            message = Utils.format(message);
            player.sendMessage(message);
            return false;
        }
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Already In Arena");
            message = Utils.format(message);
            player.sendMessage(message);
            return false;
        }
        if (arena.getArenaStatus() == ArenaStatus.PLAYING) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Playing");
            message = Utils.format(message);
            player.sendMessage(message);
            return false;
        }
        if (arena.getArenaStatus() == ArenaStatus.RESTARTING) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Restarting");
            message = Utils.format(message);
            player.sendMessage(message);
            return false;
        }
        if (arena.getArenaStatus() == ArenaStatus.ENDING) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Ending");
            message = Utils.format(message);
            player.sendMessage(message);
            return false;
        }
        return addPlayer(player, arena.getArenaID());
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

        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        ArenaManager.getArenaManager().getArenas().remove(arena);
        FileUtils.deleteQuietly(arenaFile);
        return true;
    }
}
