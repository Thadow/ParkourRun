package io.thadow.parkourrun.utils.storage.type.mysql;

import io.thadow.parkourrun.utils.Utils;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConntection {
    private static Connection connection;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public void setup(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        openConnection();
        MySQLStorage.createTable();
    }

    public void openConnection() {
        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    Bukkit.getConsoleSender().sendMessage(Utils.colorize("&cError while connecting to the database."));
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://"+this.host+":"+this.port+"/"+this.database,this.username,this.password));
                Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aDatabase conntected."));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getDatabase() {
        return database;
    }

    public static void setConnection(Connection connection) {
        MySQLConntection.connection = connection;
    }

    public static Connection getConnection() {
        return connection;
    }
}
