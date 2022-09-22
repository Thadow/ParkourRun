package io.thadow.parkourrun.menu;

import io.thadow.parkourrun.menu.menus.ArenaSelectorMenu;
import org.bukkit.entity.Player;

public class Menu {

    public static void openArenaSelectorMenuFor(Player player, int page) {
        ArenaSelectorMenu.open(player, page);
    }
}
