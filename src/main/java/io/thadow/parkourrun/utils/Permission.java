package io.thadow.parkourrun.utils;

import io.thadow.parkourrun.Main;
import org.bukkit.entity.Player;

public class Permission {

    public static void deny(Player player, String permNode) {
        String message = Main.getMessagesConfiguration().getString("No Permission");
        message = Utils.replace(message, "%permNode%", permNode);
        message = Utils.format(message);
        player.sendMessage(message);
    }
}
