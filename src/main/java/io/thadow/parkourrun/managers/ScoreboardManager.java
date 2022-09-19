package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.utils.lib.scoreboard.fast.Scoreboard;

public class ScoreboardManager {
    private static ScoreboardManager scoreboardManager = new ScoreboardManager();

    public ScoreboardManager() {}

    public void startScoreboards(int what) {
        if (what == 1) {
            io.thadow.parkourrun.utils.lib.scoreboard.fast.Scoreboard.run();
        } else if (what == 2) {
            Scoreboard.run();
        } else {
            io.thadow.parkourrun.utils.lib.scoreboard.fast.Scoreboard.run();
        }
    }

    public static ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
