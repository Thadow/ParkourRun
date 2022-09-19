package io.thadow.parkourrun.utils.lib.scoreboard;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.managers.PlayerDataManager;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.ScoreboardConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class Scoreboard {
    private static Scoreboard instance;
    private final Map<Player, PlayerBoard> boards = new HashMap<>();

    private Scoreboard() {
    }

    public PlayerBoard createBoard(Player player, String name) {
        return this.createBoard(player, null, name);
    }

    public PlayerBoard createBoard(Player player, org.bukkit.scoreboard.Scoreboard scoreboard, String name) {
        this.deleteBoard(player);
        PlayerBoard board = new PlayerBoard(player, scoreboard, name);
        this.boards.put(player, board);
        return board;
    }

    public void deleteBoard(Player player) {
        if (this.boards.containsKey(player)) {
            this.boards.get(player).delete();
        }
    }

    public void removeBoard(Player player) {
        this.boards.remove(player);
    }

    public boolean hasBoard(Player player) {
        return this.boards.containsKey(player);
    }

    public PlayerBoard getBoard(Player player) {
        return this.boards.get(player);
    }

    public Map<Player, PlayerBoard> getBoards() {
        return new HashMap<>(this.boards);
    }

    public static Scoreboard instance() {
        if (instance == null) {
            instance = new Scoreboard();
        }
        return instance;
    }

    public static void changeScoreboard(ScoreboardType type, Player player, Arena arena) {
        if (type == ScoreboardType.LOBBY && ScoreboardConfiguration.getBoolean("Scoreboards.Lobby.Enabled")) {
            PlayerBoard playerBoard = Scoreboard.instance().getBoard(player);
            if (playerBoard == null) {
                playerBoard = Scoreboard.instance().createBoard(player, "pkr_sb");
            }
            PlayerBoard board = playerBoard;
            List<String> lines = ScoreboardConfiguration.getListPath("Scoreboards.Lobby.Lines");
            ArrayList<String> newLines = new ArrayList<>();
            for (String line : lines) {
                line = Utils.replace(line, "%player_name%", player.getName());
                int wins = PlayerDataManager.getPlayerDataManager().getPlayerWins(player);
                line = Utils.replace(line, "%wins%", String.valueOf(wins));
                int loses = PlayerDataManager.getPlayerDataManager().getPlayerLoses(player);
                line = Utils.replace(line, "%loses%", String.valueOf(loses));
                line = Utils.colorize(line);
                newLines.add(line);
            }
            String title = ScoreboardConfiguration.getPath("Scoreboards.Lobby.Title");
            title = Utils.colorize(title);
            for (int i = 0; i < newLines.size(); ++i) {
                board.set(PlaceholderAPI.setPlaceholders(player, newLines.get(i)), newLines.size() - i);
                board.setName(title);
            }
        }
        if (ScoreboardConfiguration.getBoolean("Scoreboards.Waiting.Enabled") && type == ScoreboardType.WAITING) {
            PlayerBoard playerBoard = Scoreboard.instance().getBoard(player);
            if (playerBoard == null) {
                playerBoard = Scoreboard.instance().createBoard(player, "pkr_sb");
            }
            PlayerBoard board = playerBoard;
            List<String> lines = ScoreboardConfiguration.getListPath("Scoreboards.Waiting.Lines");
            ArrayList<String> newLines = new ArrayList<>();
            for (String line : lines) {
                line = Utils.replace(line, "%player_name%", player.getName());
                int wins = PlayerDataManager.getPlayerDataManager().getPlayerWins(player);
                line = Utils.replace(line, "%wins%", String.valueOf(wins));
                int loses = PlayerDataManager.getPlayerDataManager().getPlayerLoses(player);
                line = Utils.replace(line, "%loses%", String.valueOf(loses));
                line = Utils.replace(line, "%arenaName%", arena.getArenaDisplayName());
                line = Utils.colorize(line);
                newLines.add(line);
            }
            String title = ScoreboardConfiguration.getPath("Scoreboards.Waiting.Title");
            title = Utils.colorize(title);
            for (int i = 0; i < newLines.size(); ++i) {
                board.set(PlaceholderAPI.setPlaceholders(player, newLines.get(i)), newLines.size() - i);
                board.setName(title);
            }
        }
        if (ScoreboardConfiguration.getBoolean("Scoreboards.Starting.Enabled") && type == ScoreboardType.STARTING) {
            PlayerBoard playerBoard = Scoreboard.instance().getBoard(player);
            if (playerBoard == null) {
                playerBoard = Scoreboard.instance().createBoard(player, "pkr_sb");
            }
            PlayerBoard board = playerBoard;
            List<String> lines = ScoreboardConfiguration.getListPath("Scoreboards.Starting.Lines");
            ArrayList<String> newLines = new ArrayList<>();
            for (String line : lines) {
                line = Utils.replace(line, "%player_name%", player.getName());
                int wins = PlayerDataManager.getPlayerDataManager().getPlayerWins(player);
                line = Utils.replace(line, "%wins%", String.valueOf(wins));
                int loses = PlayerDataManager.getPlayerDataManager().getPlayerLoses(player);
                line = Utils.replace(line, "%loses%", String.valueOf(loses));
                line = Utils.replace(line, "%arenaName%", arena.getArenaDisplayName());
                line = Utils.replace(line, "%seconds%", String.valueOf(arena.getTime() + 1));
                line = Utils.colorize(line);
                newLines.add(line);
            }
            String title = ScoreboardConfiguration.getPath("Scoreboards.Starting.Title");
            title = Utils.colorize(title);
            for (int i = 0; i < newLines.size(); ++i) {
                board.set(PlaceholderAPI.setPlaceholders(player, newLines.get(i)), newLines.size() - i);
                board.setName(title);
            }
        }
        if (ScoreboardConfiguration.getBoolean("Scoreboards.In Game.Enabled") && type == ScoreboardType.PLAYING) {
            PlayerBoard playerBoard = Scoreboard.instance().getBoard(player);
            if (playerBoard == null) {
                playerBoard = Scoreboard.instance().createBoard(player, "pkr_sb");
            }
            PlayerBoard board = playerBoard;
            List<String> lines = ScoreboardConfiguration.getListPath("Scoreboards.In Game.Lines");
            ArrayList<String> newLines = new ArrayList<>();
            for (String line : lines) {
                line = Utils.replace(line, "%player_name%", player.getName());
                int wins = PlayerDataManager.getPlayerDataManager().getPlayerWins(player);
                line = Utils.replace(line, "%wins%", String.valueOf(wins));
                int loses = PlayerDataManager.getPlayerDataManager().getPlayerLoses(player);
                line = Utils.replace(line, "%loses%", String.valueOf(loses));
                line = Utils.replace(line, "%arenaName%", arena.getArenaDisplayName());
                line = Utils.replace(line, "%time%", Utils.getFormattedTime(arena.getMaxTime() + 1));
                line = Utils.colorize(line);
                newLines.add(line);
            }
            String title = ScoreboardConfiguration.getPath("Scoreboards.In Game.Title");
            title = Utils.colorize(title);
            for (int i = 0; i < newLines.size(); ++i) {
                board.set(PlaceholderAPI.setPlaceholders(player, newLines.get(i)), newLines.size() - i);
                board.setName(title);
            }
        }
        if (ScoreboardConfiguration.getBoolean("Scoreboards.Ending.Enabled") && type == ScoreboardType.ENDING) {
            PlayerBoard playerBoard = Scoreboard.instance().getBoard(player);
            if (playerBoard == null) {
                playerBoard = Scoreboard.instance().createBoard(player, "pkr_sb");
            }
            PlayerBoard board = playerBoard;
            List<String> lines = ScoreboardConfiguration.getListPath("Scoreboards.Ending.Lines");
            ArrayList<String> newLines = new ArrayList<>();
            for (String line : lines) {
                line = Utils.replace(line, "%player_name%", player.getName());
                int wins = PlayerDataManager.getPlayerDataManager().getPlayerWins(player);
                line = Utils.replace(line, "%wins%", String.valueOf(wins));
                int loses = PlayerDataManager.getPlayerDataManager().getPlayerLoses(player);
                line = Utils.replace(line, "%loses%", String.valueOf(loses));
                line = Utils.replace(line, "%arenaName%", arena.getArenaDisplayName());
                line = Utils.replace(line, "%time%", Utils.getFormattedTime(arena.getMaxTime()));
                line = arena.getWinner() != null ? Utils.replace(line, "%winner%", arena.getWinner().getName()) : Utils.replace(line, "%winner%", Main.getMessagesConfiguration().getString("Messages.Arena.Nobody"));
                line = Utils.colorize(line);
                newLines.add(line);
            }
            String title = ScoreboardConfiguration.getPath("Scoreboards.Ending.Title");
            title = Utils.colorize(title);
            for (int i = 0; i < newLines.size(); ++i) {
                board.set(PlaceholderAPI.setPlaceholders(player, newLines.get(i)), newLines.size() - i);
                board.setName(title);
            }
        }
    }

    public static void run() {
        int update = ScoreboardConfiguration.getInt("Scoreboards.Update");
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ArenaManager.getArenaManager().getArena(player) != null) {
                    Arena arena = ArenaManager.getArenaManager().getArena(player);
                    if (arena.getArenaStatus() == ArenaStatus.WAITING) {
                        for (Player players : arena.getPlayers()) {
                            Scoreboard.changeScoreboard(ScoreboardType.WAITING, players, arena);
                        }
                    }
                    if (arena.getArenaStatus() == ArenaStatus.STARTING) {
                        for (Player players : arena.getPlayers()) {
                            Scoreboard.changeScoreboard(ScoreboardType.STARTING, players, arena);
                        }
                    }
                    if (arena.getArenaStatus() == ArenaStatus.PLAYING) {
                        for (Player players : arena.getPlayers()) {
                            Scoreboard.changeScoreboard(ScoreboardType.PLAYING, players, arena);
                        }
                    }
                    if (arena.getArenaStatus() == ArenaStatus.ENDING) {
                        for (Player players : arena.getPlayers()) {
                            Scoreboard.changeScoreboard(ScoreboardType.ENDING, players, arena);
                        }
                    }
                } else {
                    Scoreboard.changeScoreboard(ScoreboardType.LOBBY, player, null);
                }
            }
        }, 0L, update);
    }
}