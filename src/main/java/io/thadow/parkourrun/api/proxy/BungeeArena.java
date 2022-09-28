package io.thadow.parkourrun.api.proxy;

public class BungeeArena {

    private String serverID;
    private String arenaID;
    private String arenaName;
    private String status;
    private int currentPlayers;
    private int maxPlayers;

    public BungeeArena(String serverID, String arenaID, String arenaName, String status, int currentPlayers, int maxPlayers) {
        this.serverID = serverID;
        this.arenaID = arenaID;
        this.arenaName = arenaName;
        this.status = status;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
    }

    public String getServerID() {
        return serverID;
    }

    public String getArenaID() {
        return arenaID;
    }

    public String getArenaName() {
        return arenaName;
    }

    public String getStatus() {
        return status;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
