package io.thadow.parkourrun.utils.storage;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.data.PlayerData;
import io.thadow.parkourrun.managers.PlayerDataManager;
import io.thadow.parkourrun.utils.storage.type.local.LocalStorage;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLConntection;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLStorage;
import org.bukkit.entity.Player;

public class Storage {
    private StorageType type;
    private static final Storage storage = new Storage();


    public void addWin(Player player) {
        if (getStorageType() == StorageType.LOCAL) {
            LocalStorage.addWin(player);
        } else if (getStorageType() == StorageType.MySQL) {
            PlayerData playerData = PlayerDataManager.getPlayerDataManager().getPlayerData(player.getName());
            if (playerData == null) {
                playerData = new PlayerData(player.getName(), player.getUniqueId().toString(), 0, 0);
                PlayerDataManager.getPlayerDataManager().addPlayerData(playerData);
            }
            playerData.addWin();
        }
    }

    public void addLose(Player player) {
        if (getStorageType() == StorageType.LOCAL) {
            LocalStorage.addLose(player);
        } else if (getStorageType() == StorageType.MySQL) {
            PlayerData playerData = PlayerDataManager.getPlayerDataManager().getPlayerData(player.getName());
            if (playerData == null) {
                playerData = new PlayerData(player.getName(), player.getUniqueId().toString(), 0, 0);
                PlayerDataManager.getPlayerDataManager().addPlayerData(playerData);
            }
            playerData.addLose();
        }
    }

    public void createPlayer(Player target) {
        if (getStorageType() == StorageType.LOCAL) {
            if (!LocalStorage.containsPlayer(target)) {
                LocalStorage.createPlayerData(target);
                PlayerDataManager.getPlayerDataManager().addPlayerData(new PlayerData(target.getName(), target.getUniqueId().toString(), 0, 0));
            }
        } else if (getStorageType() == StorageType.MySQL) {
            MySQLStorage.createPlayer(target.getName(), target.getUniqueId().toString());
            PlayerDataManager.getPlayerDataManager().addPlayerData(new PlayerData(target.getName(), target.getUniqueId().toString(), 0, 0));
        }
    }

    public void removePlayer(Player sender, Player target) {
        if (getStorageType() == StorageType.LOCAL) {
            if (!LocalStorage.containsPlayer(target)) {
                sender.sendMessage("El jugador no existe");
            } else {
                LocalStorage.removePlayerData(target);
                sender.sendMessage("Jugador removido correctamente");
            }
        }
    }

    public void setupStorage(StorageType type) {
        if (type == StorageType.LOCAL) {
            this.type = StorageType.LOCAL;
            LocalStorage.setup();
        } else if (type == StorageType.MySQL) {
            this.type = StorageType.MySQL;
            String host = Main.getConfiguration().getString("Configuration.MySQL.Host");
            int port = Main.getConfiguration().getInt("Configuration.MySQL.Port");
            String database = Main.getConfiguration().getString("Configuration.MySQL.Database");
            String username = Main.getConfiguration().getString("Configuration.MySQL.Username");
            String password = Main.getConfiguration().getString("Configuration.MySQL.Password");
            MySQLConntection mySQLConntection = new MySQLConntection();
            mySQLConntection.setup(host, port, database, username, password);
        }
    }

    public void save() {
        if (getStorageType() == StorageType.LOCAL) {
            LocalStorage.savePlayers();
        }
    }

    public StorageType getStorageType() {
        return type;
    }

    public static Storage getStorage() {
        return storage;
    }
}
