package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.managers.ConfigurationManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardsConfiguration extends ConfigurationManager {
    public static ScoreboardsConfiguration scoreboardsConfiguration;

    public ScoreboardsConfiguration() {
        super("scoreboards", Main.getInstance().getDataFolder().getPath());
    }

    public static void init() {
        scoreboardsConfiguration = new ScoreboardsConfiguration();
        YamlConfiguration configuration = scoreboardsConfiguration.getConfiguration();

        configuration.addDefault("Scoreboards.Lobby.Enabled", true);
        configuration.addDefault("Scoreboards.Lobby.Update", 10);
        configuration.addDefault("Scoreboards.Lobby.Title", "&eParkourRun");
        List<String> lobbyLines = new ArrayList<>();
        lobbyLines.add("%server_time_yyyy/MM/dd%");
        lobbyLines.add("&7");
        lobbyLines.add("%player_name%");
        lobbyLines.add("&7Wins: &a%wins%");
        lobbyLines.add("&7Loses: &c%loses%");
        lobbyLines.add("&7");
        lobbyLines.add("&7mc.myserver.com");
        configuration.addDefault("Scoreboards.Lobby.Lines", lobbyLines);

        configuration.addDefault("Scoreboards.Waiting.Enabled", true);
        configuration.addDefault("Scoreboards.Waiting.Update", 10);
        configuration.addDefault("Scoreboards.Waiting.Title", "&eWaiting");
        List<String> waitingLines = new ArrayList<>();
        waitingLines.add("%server_time_yyyy/MM/dd%");
        waitingLines.add("&7");
        waitingLines.add("&aArena&7: &a%arenaName%");
        waitingLines.add("&7");
        waitingLines.add("&7Waiting...");
        waitingLines.add("&7");
        waitingLines.add("&7mc.myserver.com");
        configuration.addDefault("Scoreboards.Waiting.Lines", waitingLines);

        configuration.addDefault("Scoreboards.Starting.Enabled", true);
        configuration.addDefault("Scoreboards.Starting.Update", 10);
        configuration.addDefault("Scoreboards.Starting.Title", "&eStarting");
        List<String> startingLines = new ArrayList<>();
        startingLines.add("%server_time_yyyy/MM/dd%");
        startingLines.add("&7");
        startingLines.add("&aArena&7: &a%arenaName%");
        startingLines.add("&7");
        startingLines.add("&eStarting in %seconds%");
        startingLines.add("&7");
        startingLines.add("&7mc.myserver.com");
        configuration.addDefault("Scoreboards.Starting.Lines", startingLines);

        configuration.addDefault("Scoreboards.Playing.Enabled", true);
        configuration.addDefault("Scoreboards.Playing.Update", 10);
        configuration.addDefault("Scoreboards.Playing.Title", "&eParkourRun");
        List<String> playingLines = new ArrayList<>();
        playingLines.add("%server_time_yyyy/MM/dd%");
        playingLines.add("&7");
        playingLines.add("&aArena&7: &a%arenaName%");
        playingLines.add("&7");
        playingLines.add("&eEnding in: %time%");
        playingLines.add("&7");
        playingLines.add("&7mc.myserver.com");
        configuration.addDefault("Scoreboards.Playing.Lines", playingLines);

        configuration.addDefault("Scoreboards.Ending.Enabled", true);
        configuration.addDefault("Scoreboards.Ending.Update", 10);
        configuration.addDefault("Scoreboards.Ending.Title", "&eParkourRun");
        List<String> endingLines = new ArrayList<>();
        endingLines.add("%server_time_yyyy/MM/dd%");
        endingLines.add("&7");
        endingLines.add("&aArena67: &a%arenaName%");
        endingLines.add("&7");
        endingLines.add("&eEnded In: %time%");
        endingLines.add("&7");
        endingLines.add("&eGanador: &a%winner%");
        endingLines.add("&7");
        endingLines.add("&7my.server.com");
        configuration.addDefault("Scoreboards.Ending.Lines", endingLines);

        configuration.options().copyDefaults(true);
        scoreboardsConfiguration.save();
    }
}
