package io.thadow.parkourrun.menu;

import io.thadow.parkourrun.menu.menus.ArenaSelectorMenu;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Menu {

    public static void openArenaSelectorMenuTo(Player player, int page) {
        ArenaSelectorMenu.open(player, page);
    }
}
