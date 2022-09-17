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
    private Main main;

    public ParkourRunAPI(Main main) {
        this.main = main;
    }

    public static int getPlayerWins(String playerName) {
        PlayerData playerData = PlayerDataManager.getPlayerDataManager().getPlayerData(playerName);
        if (playerData != null) {
            return playerData.getWins();
        } else {
            return 0;
        }
    }

    public static int getPlayerLoses(String playerName) {
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

    public static ArenaStatus getPlayerArenaStatus(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getArenaStatus();
        } else {
            return null;
        }
    }

    public static List<Player> getArenaPlayers(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
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
        Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
        if (arena != null) {
            return arena.getPlayers().size();
        } else {
            return null;
        }
    }

    public static Integer getArenaTotalPlayersByPlayer(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getPlayers().size();
        } else {
            return null;
        }
    }

    public static Integer getArenaMinPlayers(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
        if (arena != null) {
            return arena.getMinPlayers();
        } else {
            return null;
        }
    }

    public static Integer getArenaMinPlayersByPlayer(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getMinPlayers();
        } else {
            return null;
        }
    }

    public static Integer getArenaMaxPlayers(String arenaID) {
        Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
        if (arena != null) {
            return arena.getMaxPlayers();
        } else {
            return null;
        }
    }

    public static Integer getArenaMaxPlayersByPlayer(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena != null) {
            return arena.getMaxPlayers();
        } else {
            return null;
        }
    }
}
