package io.thadow.parkourrun.listeners;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.lib.scoreboard.ScoreboardType;
import io.thadow.parkourrun.utils.lib.scoreboard.fast.Scoreboard;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.StorageType;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Main.isMySQLEnabled()) {
            if (!MySQLStorage.containsPlayer(player.getUniqueId().toString())) {
                MySQLStorage.createPlayer(player.getName(), player.getUniqueId().toString(), 0, 0);
            }
        } else {
            Storage.getStorage().createPlayer(player);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerPreJoin(PlayerPreLoginEvent event) {
        if (Storage.getStorage().getStorageType() == StorageType.TRANSFORM) {
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, Utils.colorize("&aData has been transformed, please restart the server."));
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (Scoreboard.scoreboards.containsKey(event.getPlayer().getUniqueId())) {
            Scoreboard scoreboard = Scoreboard.scoreboards.get(event.getPlayer().getUniqueId());
            scoreboard.delete();
            Scoreboard.scoreboards.remove(event.getPlayer().getUniqueId());
        }
    }
}
