package io.thadow.proxy;

import io.thadow.proxy.debug.Debugger;
import io.thadow.proxy.debug.type.DebugType;
import io.thadow.proxy.sockethandler.ServerSocketHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {
    private static Main instance;
    private static boolean debug;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        saveDefaultConfig();
        debug = getConfig().getBoolean("Debug");
        int port = getConfig().getInt("Port Listener");
        if (!ServerSocketHandler.init(port)) {
            Bukkit.broadcastMessage("can't enable");
        }
        Debugger.debug(DebugType.INFO, "&7Listening for arenas on: " + port);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ServerSocketHandler.stopTasks();
    }

    public static Main getInstance() {
        return instance;
    }

    public static boolean isDebug() {
        return debug;
    }
}
