package io.thadow.parkourrun.api;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.data.PlayerData;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.managers.PlayerDataManager;
import org.bukkit.entity.Player;

import java.util.List;

public class ParkourRunAPI {

    public static Integer getPlayerWins(String playerName) {
        PlayerData playerData = PlayerDataManager.getPlayerDataManager().getPlayerData(playerName);
        if (playerData != null) {
            return playerData.getWins();
        } else {
            return 0;
        }
    }

    public static Integer getPlayerLoses(String playerName) {
        PlayerData playerData = PlayerDataManager.getPlayerDataManager().getPlayerData(playerName);
        if (playerData != null) {
            return playerData.getLoses();
        } else {
            return 0;
        }
    }

    public static Arena getPlayerArena(Player player) {
        return ArenaManager.getArenaManager().getArena(player);
    }

    public static String getPlayerArenaID(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getArenaID();
        } else {
            return "Unknown Arena";
        }
    }

    public static String getPlayerArenaDisplayName(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getArenaDisplayName();
        } else {
            return "Unknown Arena";
        }
    }

    public static String getArenaDisplayName(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
        if (arena == null) {
            arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        }
        if (arena != null) {
            return arena.getArenaDisplayName();
        } else {
            return "Unknown Arena";
        }
    }

    public static ArenaStatus getArenaStatus(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        if (arena == null) {
            ArenaManager.getArenaManager().getArenaByName(arenaID);
        }
        if (arena != null) {
            return arena.getArenaStatus();
        } else {
            return null;
        }
    }

    public static String getFormattedArenaStatus(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        if (arena == null) {
            arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        }
        if (arena != null) {
            String waiting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Waiting");
            String starting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Starting");
            String playing = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Playing");
            String ending = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Ending");
            String restarting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Restarting");
            String disabled = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Disabled");
            String status;
            switch (arena.getArenaStatus()) {
                case WAITING:
                    status = waiting;
                    break;
                case STARTING:
                    status = starting;
                    break;
                case PLAYING:
                    status = playing;
                    break;
                case ENDING:
                    status = ending;
                    break;
                case RESTARTING:
                    status = restarting;
                    break;
                case DISABLED:
                    status = disabled;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + arena.getArenaStatus());
            }
            return status;
        } else {
            return "Unknown Arena";
        }
    }

    public static ArenaStatus getPlayerArenaStatus(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getArenaStatus();
        } else {
            return null;
        }
    }

    public static List<Player> getArenaPlayers(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        if (arena == null) {
            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
        }
        if (arena != null) {
            return arena.getPlayers();
        } else {
            return null;
        }
    }

    public static List<Player> getArenaPlayersByPlayer(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getPlayers();
        } else {
            return null;
        }
    }

    public static List<PlayerData> getPlayersData() {
        return PlayerDataManager.getPlayerDataManager().getPlayers();
    }

    public static Integer getArenaTotalPlayers(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        if (arena == null) {
            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
        }
        if (arena != null) {
            return arena.getPlayers().size();
        } else {
            return 0;
        }
    }

    public static Integer getArenaTotalPlayersByPlayer(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getPlayers().size();
        } else {
            return 0;
        }
    }

    public static Integer getArenaMinPlayers(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        if (arena == null) {
            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
        }
        if (arena != null) {
            return arena.getMinPlayers();
        } else {
            return 0;
        }
    }

    public static Integer getArenaMinPlayersByPlayer(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getMinPlayers();
        } else {
            return 0;
        }
    }

    public static Integer getArenaMaxPlayers(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
        if (arena == null) {
            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
        }
        if (arena != null) {
            return arena.getMaxPlayers();
        } else {
            return 0;
        }
    }

    public static Integer getArenaMaxPlayersByPlayer(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getMaxPlayers();
        } else {
            return 0;
        }
    }
}
