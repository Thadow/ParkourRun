package io.thadow.parkourrun;

import io.thadow.parkourrun.api.PAPIExpansion;
import io.thadow.parkourrun.api.ParkourRunAPI;
import io.thadow.parkourrun.api.server.VersionSupport;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.listeners.ArenaListener;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.listeners.PlayerListener;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.commands.LeaveCommand;
import io.thadow.parkourrun.commands.ParkourRunCommand;
import io.thadow.parkourrun.managers.ConfigurationManager;
import io.thadow.parkourrun.managers.PlayerDataManager;
import io.thadow.parkourrun.managers.ScoreboardManager;
import io.thadow.parkourrun.utils.configurations.MessagesConfiguration;
import io.thadow.parkourrun.utils.configurations.ScoreboardConfiguration;
import io.thadow.parkourrun.utils.configurations.SignsConfiguration;
import io.thadow.parkourrun.utils.storage.ActionCooldown;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.StorageType;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLConntection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;

@SuppressWarnings("all")
public class Main extends JavaPlugin {
    private static Main instance;
    public static VersionSupport nms;
    private static boolean mysql = false;
    private static boolean debug = false;
    private static boolean versionSupported = false;
    private static final String version = Bukkit.getServer().getClass().getName().split("\\.")[3];

    @Override
    public void onLoad() {
        super.onLoad();
        Class supp;

        try {
            supp = Class.forName("io.thadow.parkourrun.server." + version);
        } catch (ClassNotFoundException e) {
            this.getLogger().severe("I can't run on your version: " + version);
            versionSupported = false;
            return;
        }
        try {
            this.getLogger().info("Loadin support for: " + version);
            nms = (VersionSupport) supp.getConstructor(Class.forName("org.bukkit.plugin.Plugin"), String.class).newInstance(this, version);
            versionSupported = true;
        } catch (InstantiationException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            this.getLogger().severe("Could not load support for server version: " + version);
            versionSupported = false;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        if (!versionSupported) {
            this.getLogger().severe("I can't run on your version: " + version);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        saveDefaultConfig();
        MessagesConfiguration.init();
        SignsConfiguration.init();
        setDebug(getConfig().getBoolean("Configuration.Debug"));
        ScoreboardConfiguration.registerConfiguration();
        getCommand("parkourrun").setExecutor(new ParkourRunCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
        registerListeners(new ArenaListener(), new PlayerListener());
        ScoreboardManager.getScoreboardManager().startScoreboards();
        ActionCooldown.createCooldown("cantWinMessage", 5);
        if (getConfig().getString("Configuration.StorageType").equals("TRANSFORM")) {
            String from = getConfig().getString("Configuration.Transform.From");
            String to = getConfig().getString("Configuration.Transform.To");
            Bukkit.getConsoleSender().sendMessage("&aTransforming data!");
            PlayerDataManager.getPlayerDataManager().transformData(from, to);
            return;
        }
        if (getConfig().getString("Configuration.StorageType").equals("MySQL")) {
            Storage.getStorage().setupStorage(StorageType.MySQL);
        } else {
            if (getConfig().getString("Configuration.StorageType").equals("LOCAL")) {
                Storage.getStorage().setupStorage(StorageType.LOCAL);
            }
        }
        ArenaManager.getArenaManager().loadArenas();
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


    public static FileConfiguration getMessagesConfiguration() {
        return MessagesConfiguration.messagesConfiguration.getConfiguration();
    }

    public static FileConfiguration getSignsConfiguration() {
        return SignsConfiguration.signsConfiguration.getConfiguration();
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
