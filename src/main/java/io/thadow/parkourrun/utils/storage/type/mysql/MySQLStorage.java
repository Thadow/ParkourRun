package io.thadow.parkourrun.utils.storage.type.mysql;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.data.PlayerData;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MySQLStorage {

    public static void createTable() {
        try {
            PreparedStatement statement = MySQLConntection.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS pkr_data (`UUID` varchar(200), `PLAYER_NAME` varchar(50), `WINS` INT(5), `LOSES` INT(5))");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PlayerData> getPlayers() {
        ArrayList<PlayerData> players = new ArrayList<>();
        try {
            PreparedStatement statement = MySQLConntection.getConnection().prepareStatement("SELECT * FROM pkr_data");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String uuid = resultSet.getString("UUID");
                int wins = resultSet.getInt("WINS");
                int loses = resultSet.getInt("LOSES");
                String name = resultSet.getString("PLAYER_NAME");
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
                PreparedStatement statement = MySQLConntection.getConnection().prepareStatement("SELECT * FROM pkr_data WHERE uuid=?");
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

    public static boolean containsPlayer(String uuid) {
        try {
            String query = "SELECT * FROM pkr_data WHERE (uuid=?)";
            PreparedStatement statement = MySQLConntection.getConnection().prepareStatement(query);
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public static void createPlayer(String name, String uuid, int wins, int loses) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement insert = MySQLConntection.getConnection()
                        .prepareStatement("INSERT INTO pkr_data (UUID,PLAYER_NAME,WINS,LOSES) VALUE (?,?,?,?)");
                insert.setString(1, uuid);
                insert.setString(2, name);
                insert.setInt(3, wins);
                insert.setInt(4, loses);
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
                PreparedStatement statement = MySQLConntection.getConnection().prepareStatement("UPDATE pkr_data SET player_name=?, wins=?, loses=? WHERE (uuid=?)");
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

