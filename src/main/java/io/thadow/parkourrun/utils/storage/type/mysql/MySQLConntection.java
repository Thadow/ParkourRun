package io.thadow.parkourrun.utils.storage.type.mysql;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.StorageType;
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
    private boolean useSSL;

    public void setup(String host, int port, String database, String username, String password, boolean useSSL) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.useSSL = useSSL;
        openConnection();
    }

    public void openConnection() {
        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    Bukkit.getConsoleSender().sendMessage(Utils.colorize("&cError while connecting to the database."));
                    if (Main.isDebugEnabled()) {
                        Debugger.debug(DebugType.ALERT, "Error cause: Already connected/Connection not closed.");
                    }
                    Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aEnabling local storage..."));
                    Storage.getStorage().setupStorage(StorageType.LOCAL);
                    Main.setMysql(false);
                    return;
                }
                String url = "jdbc:mysql://" + host + "/" + database + "?useSSL=" + useSSL + "&autoReconnect=true";
                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection(url, username, password));
                Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aDatabase conntected."));
                MySQLStorage.createTable();
                Main.setMysql(true);
            }
        } catch (SQLException | ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&cError while connecting to the database."));
            if (Main.isDebugEnabled()) {
                Debugger.debug(DebugType.ALERT, "Error cause: " + e.getCause().toString());
            }
            Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aEnabling local storage..."));
            Storage.getStorage().setupStorage(StorageType.LOCAL);
            Main.setMysql(false);
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
