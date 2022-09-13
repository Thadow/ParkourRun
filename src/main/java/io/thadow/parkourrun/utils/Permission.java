package io.thadow.parkourrun.utils;

import io.thadow.parkourrun.utils.configurations.MessagesConfiguration;
import org.bukkit.entity.Player;

public class Permission {

    public static void deny(Player player, String permNode) {
        String message = MessagesConfiguration.getPath("No Permission");
        message = Utils.replace(message, "%permNode%", permNode);
        message = Utils.format(message);
        player.sendMessage(message);
    }
}
