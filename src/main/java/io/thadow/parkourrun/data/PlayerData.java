package io.thadow.parkourrun.data;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLStorage;

public class PlayerData {

    private final String player;
    private final String uuid;
    private int wins;
    private int loses;

    public PlayerData(String player, String uuid, int wins, int loses) {
        this.player = player;
        this.uuid = uuid;
        this.wins = wins;
        this.loses = loses;
    }

    public String getPlayer() {
        return player;
    }

    public String getUUID() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public int getLoses() {
        return loses;
    }

    public void addLose() {
        this.loses++;
        if (Main.isMySQLEnabled()) {
            MySQLStorage.updatePlayer(this);
        }
    }

    public void addWin() {
        this.wins++;
        if (Main.isMySQLEnabled()) {
            MySQLStorage.updatePlayer(this);
        }
    }
}
