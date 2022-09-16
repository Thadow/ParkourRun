package io.thadow.parkourrun.utils.debug;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import org.bukkit.Bukkit;

public class Debugger {

    public static void debug(DebugType type, String message) {
        if (Main.isDebugEnabled()) {
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
