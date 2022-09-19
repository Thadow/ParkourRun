package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.utils.lib.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Score;

import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {
    private static ScoreboardManager scoreboardManager = new ScoreboardManager();

    public ScoreboardManager() {}

    public void startScoreboards() {
        Scoreboard.run();
    }

    public Map<UUID, Scoreboard> getScoreboards() {
        return Scoreboard.scoreboards;
    }

    public static ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
