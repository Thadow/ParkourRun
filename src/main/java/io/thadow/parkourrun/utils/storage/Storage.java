package io.thadow.parkourrun.utils.storage;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.data.PlayerData;
import io.thadow.parkourrun.managers.PlayerDataManager;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.MainConfiguration;
import io.thadow.parkourrun.utils.storage.type.local.LocalStorage;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLConntection;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

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
            MySQLStorage.createPlayer(target.getName(), target.getUniqueId().toString(), 0, 0);
            PlayerDataManager.getPlayerDataManager().addPlayerData(new PlayerData(target.getName(), target.getUniqueId().toString(), 0, 0));
        }
    }

    public void removePlayer(Player sender, Player target) {
        if (getStorageType() == StorageType.LOCAL) {
            if (LocalStorage.containsPlayer(target)) {
                LocalStorage.removePlayerData(target);
            }
        }
    }

    public void setupStorage(StorageType type) {
        if (type == StorageType.LOCAL) {
            this.type = StorageType.LOCAL;
            LocalStorage.setup();
        } else if (type == StorageType.MySQL) {
            this.type = StorageType.MySQL;
            String host = Main.getInstance().getConfiguration().getString("Configuration.MySQL.Host");
            int port = Main.getInstance().getConfiguration().getInt("Configuration.MySQL.Port");
            String database = Main.getInstance().getConfiguration().getString("Configuration.MySQL.Database");
            String username = Main.getInstance().getConfiguration().getString("Configuration.MySQL.Username");
            String password = Main.getInstance().getConfiguration().getString("Configuration.MySQL.Password");
            boolean useSSL = Main.getInstance().getConfiguration().getBoolean("Configuration.MySQL.SSL");
            MySQLConntection mySQLConntection = new MySQLConntection();
            mySQLConntection.setup(host, port, database, username, password, useSSL);
        }
    }


    public void transformData(String from, String to) {
        int created = 0;
        int updated = 0;
        int loaded;
        if (from.equalsIgnoreCase("MySQL") && to.equalsIgnoreCase("LOCAL")) {
            this.type = StorageType.TRANSFORM;
            String host = Main.getInstance().getConfiguration().getString("Configuration.MySQL.Host");
            int port = Main.getInstance().getConfiguration().getInt("Configuration.MySQL.Port");
            String database = Main.getInstance().getConfiguration().getString("Configuration.MySQL.Database");
            String username = Main.getInstance().getConfiguration().getString("Configuration.MySQL.Username");
            String password = Main.getInstance().getConfiguration().getString("Configuration.MySQL.Password");
            boolean useSSL = Main.getInstance().getConfiguration().getBoolean("Configuration.MySQL.SSL");
            MySQLConntection mySQLConntection = new MySQLConntection();
            mySQLConntection.setup(host, port, database, username, password, useSSL);
            LocalStorage.setup();

            List<PlayerData> playerDataList = MySQLStorage.getPlayers();
            loaded = playerDataList.size();
            for (PlayerData playerData : playerDataList) {
                String uuid = playerData.getUUID();
                String player_name = playerData.getPlayer();
                int wins = playerData.getWins();
                int loses = playerData.getLoses();
                LocalStorage.get().set("Players." + uuid + ".Name", player_name);
                LocalStorage.get().set("Players." + uuid + ".Wins", wins);
                LocalStorage.get().set("Players." + uuid + ".Loses", loses);
                updated = updated + 1;
            }
            LocalStorage.save();
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aData transform completed."));
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aPlayers updated: " + updated));
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aLoaded: " + loaded));
            int total = updated;
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aTotal changed: " + total));
            Main.getInstance().getConfiguration().set("Configuration.StorageType", "LOCAL");
            MainConfiguration.mainConfiguration.save();
            Bukkit.getConsoleSender().sendMessage("&aPlease restart the server.");
        } else if (from.equalsIgnoreCase("LOCAL") && to.equalsIgnoreCase("MySQL")) {
            this.type = StorageType.TRANSFORM;
            String host = Main.getMessagesConfiguration().getString("Configuration.MySQL.Host");
            int port = Main.getMessagesConfiguration().getInt("Configuration.MySQL.Port");
            String database = Main.getMessagesConfiguration().getString("Configuration.MySQL.Database");
            String username = Main.getMessagesConfiguration().getString("Configuration.MySQL.Username");
            String password = Main.getMessagesConfiguration().getString("Configuration.MySQL.Password");
            boolean useSSL = Main.getMessagesConfiguration().getBoolean("Configuration.MySQL.SSL");
            MySQLConntection mySQLConntection = new MySQLConntection();
            mySQLConntection.setup(host, port, database, username, password, useSSL);
            LocalStorage.setup();

            List<PlayerData> playerDataList = LocalStorage.getPlayers();
            loaded = playerDataList.size();
            for (PlayerData playerData : playerDataList) {
                String uuid = playerData.getUUID();
                String player_name = playerData.getPlayer();
                int wins = playerData.getWins();
                int loses = playerData.getLoses();
                PlayerData data = new PlayerData(player_name, uuid, wins, loses);
                if (!MySQLStorage.containsPlayer(uuid)) {
                    MySQLStorage.createPlayer(player_name, uuid, wins, loses);
                    created = created + 1;
                } else {
                    MySQLStorage.updatePlayer(data);
                    updated = updated + 1;
                }
            }
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aData transform completed."));
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aPlayers created: " + created));
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aPlayers updated: " + updated));
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aLoaded: " + loaded));
            int total = created + updated;
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aTotal changed: " + total));
            Main.getInstance().getConfiguration().set("Configuration.StorageType", "MySQL");
            MainConfiguration.mainConfiguration.save();
            Bukkit.getConsoleSender().sendMessage("&aPlease restart the server.");
        }
    }

    public StorageType getStorageType() {
        return type;
    }

    public static Storage getStorage() {
        return storage;
    }
}
