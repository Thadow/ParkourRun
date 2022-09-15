package io.thadow.parkourrun.listeners;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.data.PlayerData;
import io.thadow.parkourrun.managers.PlayerDataManager;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.type.mysql.Callback;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Main.isMySQLEnabled()) {
            MySQLStorage.getPlayer(player.getName(), new Callback() {
                @Override
                public void onEnd(PlayerData playerData) {
                    PlayerDataManager.getPlayerDataManager().removePlayerData(player.getName());
                    if (playerData != null) {
                        PlayerDataManager.getPlayerDataManager().addPlayerData(playerData);
                    } else {
                        Storage.getStorage().createPlayer(player);
                    }
                }
            });
        } else {
            Storage.getStorage().createPlayer(player);
        }
    }
}
