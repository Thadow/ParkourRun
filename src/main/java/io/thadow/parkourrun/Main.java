package io.thadow.parkourrun;

import io.thadow.parkourrun.api.PAPIExpansion;
import io.thadow.parkourrun.api.ParkourRunAPI;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.listeners.ArenaListener;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.listeners.PlayerListener;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.commands.LeaveCommand;
import io.thadow.parkourrun.commands.ParkourRunCommand;
import io.thadow.parkourrun.managers.PlayerDataManager;
import io.thadow.parkourrun.utils.configurations.ArenasConfiguration;
import io.thadow.parkourrun.utils.configurations.MessagesConfiguration;
import io.thadow.parkourrun.utils.configurations.ScoreboardConfiguration;
import io.thadow.parkourrun.utils.lib.scoreboard.Scoreboard;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.StorageType;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLConntection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;

public class Main extends JavaPlugin {
    private static Main instance;
    private static boolean mysql = false;
    private static boolean debug = false;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        saveDefaultConfig();
        setDebug(getConfiguration().getBoolean("Configuration.Debug"));
        ArenasConfiguration.registerConfiguration();
        MessagesConfiguration.registerConfiguration();
        ScoreboardConfiguration.registerConfiguration();
        ArenaManager.getArenaManager().loadArenas();
        getCommand("parkourrun").setExecutor(new ParkourRunCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
        registerListeners(new ArenaListener(), new PlayerListener());
        Scoreboard.run();
        if (getConfiguration().getString("Configuration.StorageType").equals("TRANSFORM")) {
            String from = getConfiguration().getString("Configuration.Transform.From");
            String to = getConfiguration().getString("Configuration.Transform.To");
            Bukkit.getConsoleSender().sendMessage("&aTransforming data!");
            PlayerDataManager.getPlayerDataManager().transformData(from, to);
            return;
        }
        if (getConfiguration().getString("Configuration.StorageType").equals("MySQL")) {
            Storage.getStorage().setupStorage(StorageType.MySQL);
        } else {
            if (getConfiguration().getString("Configuration.StorageType").equals("LOCAL")) {
                Storage.getStorage().setupStorage(StorageType.LOCAL);
            }
        }
        PlayerDataManager.getPlayerDataManager().loadPlayers();
        new ParkourRunAPI(this);
        new PAPIExpansion(this).register();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Arena arena : ArenaManager.getArenaManager().getArenas()) {
            if (arena.getArenaStatus() == ArenaStatus.PLAYING) {
                arena.finalizeArena(true);
            } else if (arena.getArenaStatus() == ArenaStatus.ENDING) {
                for (Player players : arena.getPlayers()) {
                    players.teleport(players.getWorld().getSpawnLocation());
                }
            }
        }
        PlayerDataManager.getPlayerDataManager().savePlayers();
        if (mysql) {
            try {
                MySQLConntection.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getInstance().getServer().getPluginManager().registerEvents(listener, getInstance()));
    }

    public static FileConfiguration getConfiguration() {
        return Main.getInstance().getConfig();
    }


    public static boolean isMySQLEnabled() {
        return mysql;
    }

    public static void setMysql(boolean mysql) {
        Main.mysql = mysql;
    }

    public static boolean isDebugEnabled() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Main.debug = debug;
    }
}
