package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.data.PlayerData;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.StorageType;
import io.thadow.parkourrun.utils.storage.type.local.LocalStorage;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLStorage;
import org.bukkit.entity.Player;
import sun.util.resources.en.LocaleNames_en_SG;

import java.util.ArrayList;

public class PlayerDataManager {
    private static final PlayerDataManager playerDataManager = new PlayerDataManager();
    private ArrayList<PlayerData> players = new ArrayList<>();

    public PlayerDataManager() {}

    public static PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public void loadPlayers(){
        if (Main.isMySQLEnabled()) {
            this.players = MySQLStorage.getPlayers();
        } else {
            this.players = LocalStorage.getPlayers();
        }
    }

    public void savePlayers() {
        if (Main.isMySQLEnabled()) {
            return;
        }
        if (Storage.getStorage().getStorageType() == StorageType.LOCAL) {
            LocalStorage.savePlayers();
        }
    }

    public void transformData(String from, String to) {
        Storage.getStorage().transformData(from, to);
    }

    public void addPlayerData(PlayerData playerData) {
        players.add(playerData);
    }

    public void removePlayerData(String player) {
        for(int i=0;i<players.size();i++) {
            if(players.get(i).getPlayer().equals(player)) {
                players.remove(i);
            }
        }
    }

    public PlayerData getPlayerData(String name) {
        for(PlayerData playerData : getPlayers()) {
            if(playerData.getPlayer().equals(name)) {
                return playerData;
            }
        }
        return null;
    }

    public ArrayList<PlayerData> getPlayers() {
        return players;
    }

    public Integer getPlayerWins(Player player) {
        return getPlayerData(player.getName()).getWins();
    }

    public Integer getPlayerLoses(Player player) {
        return getPlayerData(player.getName()).getLoses();
    }
}
