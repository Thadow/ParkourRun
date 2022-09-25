package io.thadow.proxy.debug;

import io.thadow.proxy.Main;
import io.thadow.proxy.debug.type.DebugType;
import io.thadow.proxy.utils.Utils;
import org.bukkit.Bukkit;

public class Debugger {
    public static void debug(DebugType type, String message) {
        if (Main.isDebug()) {
            switch (type) {
                case INFO:
                    Bukkit.getConsoleSender().sendMessage(Utils.colorize("&7[DEBUG / INFO] " + message));
                    break;
                case WARN:
                    Bukkit.getConsoleSender().sendMessage(Utils.colorize("&6[DEBUG / WARN] " + message));
                    break;
                case ALERT:
                    Bukkit.getConsoleSender().sendMessage(Utils.colorize("&c[DEBUG / ALERT] " + message));
            }
        }
    }
}
