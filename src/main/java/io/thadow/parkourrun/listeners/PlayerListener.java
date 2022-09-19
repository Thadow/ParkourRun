package io.thadow.parkourrun.listeners;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.managers.ConfigurationManager;
import io.thadow.parkourrun.managers.SignsManager;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.SignsConfiguration;
import io.thadow.parkourrun.utils.lib.scoreboard.Scoreboard;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.StorageType;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e == null) return;
        Player p = e.getPlayer();
        if (Objects.requireNonNull(e.getLine(0)).equalsIgnoreCase("[parkourrun]")) {
            File dir = new File(Main.getInstance().getDataFolder(), "/Arenas");
            boolean exists = false;
            if (dir.exists()) {
                for (File f : Objects.requireNonNull(dir.listFiles())) {
                    if (f.isFile()) {
                        if (f.getName().contains(".yml")) {
                            if (Objects.equals(e.getLine(1), f.getName().replace(".yml", ""))) {
                                exists = true;
                            }
                        }
                    }
                }
                List<String> s;
                if (Main.getSignsConfiguration().get("Locations") == null) {
                    s = new ArrayList<>();
                } else {
                    s = new ArrayList<>(Main.getSignsConfiguration().getStringList("Locations"));
                }
                if (exists) {

                    s.add(e.getLine(1) + "," + stringLocationConfigFormat(e.getBlock().getLocation()));
                    SignsConfiguration.signsConfiguration.set("Locations", s);
                    SignsConfiguration.signsConfiguration.save();
                }
                Arena a = ArenaManager.getArenaManager().getArena(e.getLine(1));
                if (a != null) {
                    p.sendMessage("§a▪ §7Sign saved for arena: " + e.getLine(1));
                    SignsManager.getSingsManager().addSign(e.getBlock().getLocation());
                    Sign b = (Sign) e.getBlock().getState();
                    int line = 0;
                    for (String string : Main.getSignsConfiguration().getStringList("Format")) {
                        e.setLine(line, Utils.colorize(string.replace("[current]", String.valueOf(a.getPlayers().size())).replace("[max]",
                                String.valueOf(a.getMaxPlayers())).replace("[arena]", a.getArenaDisplayName()).replace("[status]", a.getArenaStatus().toString())));
                        line++;
                    }
                    b.update(true);
                }
            } else {
                p.sendMessage("§c▪ §7You didn't set any arena yet!");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e == null) return;
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            if (b.getState() instanceof Sign) {
                for (Arena arena : ArenaManager.getArenaManager().getArenas()) {
                    if (SignsManager.getSigns().contains(b)) {
                        arena.addPlayer(e.getPlayer());
                        return;
                    }
                }
            }
        }
    }

    public String stringLocationConfigFormat(Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + (double) loc.getYaw() + "," + (double) loc.getPitch() + "," + loc.getWorld().getName();
    }
}
