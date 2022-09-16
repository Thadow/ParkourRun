package io.thadow.parkourrun.utils.storage.type.mysql;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.data.PlayerData;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLStorage {

    public static void createTable() {
        try {
            PreparedStatement statement = MySQLConntection.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS parkourrun_data (`UUID` varchar(200), `PLAYER_NAME` varchar(50), `WINS` INT(5), `LOSES` INT(5))");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PlayerData> getPlayers() {
        ArrayList<PlayerData> players = new ArrayList<>();
        try {
            PreparedStatement statement = MySQLConntection.getConnection().prepareStatement("SELECT * FROM parkourrun_data");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String uuid = resultSet.getString("UUID");
                int wins = resultSet.getInt("WINS");
                int loses = resultSet.getInt("LOSES");
                String name = resultSet.getString("PLAYER_NAME");
                Bukkit.getConsoleSender().sendMessage("Player Found: " + name);
                Bukkit.getConsoleSender().sendMessage("UUID: " + uuid);
                Bukkit.getConsoleSender().sendMessage("Wins: " + wins);
                Bukkit.getConsoleSender().sendMessage("Loses: " + loses);
                players.add(new PlayerData(name, uuid, wins, loses));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public static void getPlayer(final String name, final Callback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            int wins = 0;
            int loses = 0;
            String uuid = "";
            boolean found = false;
            try {
                PreparedStatement statement = MySQLConntection.getConnection().prepareStatement("SELECT * FROM parkourrun_data WHERE player_name=?");
                statement.setString(1, name);
                ResultSet resultado = statement.executeQuery();
                if (resultado.next()) {
                    uuid = resultado.getString("UUID");
                    wins = resultado.getInt("WINS");
                    loses = resultado.getInt("LOSES");
                    found = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (found) {
                final PlayerData playerData = new PlayerData(name, uuid, wins, loses);
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> callback.onEnd(playerData));
            } else {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> callback.onEnd(null));
            }
        });
    }

    public static void createPlayer(String name, String uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement insert = MySQLConntection.getConnection()
                        .prepareStatement("INSERT INTO parkourrun_data (UUID,PLAYER_NAME,WINS,LOSES) VALUE (?,?,?,?)");
                insert.setString(1, uuid);
                insert.setString(2, name);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void updatePlayer(PlayerData playerData) {
        final String uuid = playerData.getUUID();
        final String name = playerData.getPlayer();
        final int wins = playerData.getWins();
        final int loses = playerData.getLoses();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = MySQLConntection.getConnection().prepareStatement("UPDATE parkourrun_data SET player_name=?, wins=?, loses=? WHERE (uuid=?)");
                statement.setString(1, name);
                statement.setInt(2, wins);
                statement.setInt(3, loses);
                statement.setString(4, uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}

