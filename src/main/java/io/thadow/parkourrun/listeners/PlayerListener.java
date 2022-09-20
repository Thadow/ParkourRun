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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
    public void onPlayerBreakSignEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("parkourrun.commands.admin")) {
            Block block = event.getBlock();
            Location location = block.getLocation();
            if (location.getBlock().getType().toString().endsWith("_SIGN") || location.getBlock().getType().toString().endsWith("_WALL_SIGN")) {
                for (Arena arena1 : ArenaManager.getArenaManager().getArenas()) {
                    if (arena1.getSigns().contains(block)) {
                        arena1.getSigns().remove(block);
                        Sign sign = (Sign) event.getBlock().getState();
                        String arenaNameStrip;
                        String arenaID = sign.getLine(0);
                        arenaID = ChatColor.stripColor(arenaID);
                        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                        if (arena == null) {
                            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                        }
                        if (arena == null) {
                            arenaID = sign.getLine(1);
                            arenaID = ChatColor.stripColor(arenaID);
                        }
                        arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                        if (arena == null) {
                            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                        }
                        if (arena == null) {
                            arenaID = sign.getLine(2);
                            arenaID = ChatColor.stripColor(arenaID);
                        }
                        arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                        if (arena == null) {
                            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                        }
                        if (arena == null) {
                            arenaID = sign.getLine(3);
                            arenaID = ChatColor.stripColor(arenaID);
                        }
                        arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                        if (arena == null) {
                            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                        }
                        if (arena == null) {
                            return;
                        }
                        arenaNameStrip = ChatColor.stripColor(arena.getArenaDisplayName());
                        List<String> locations = Main.getSignsConfiguration().getStringList("Locations");
                        locations.remove(arenaNameStrip + ";" + stringLocationConfigFormat(event.getBlock().getLocation()));
                        SignsConfiguration.signsConfiguration.save();
                        SignsConfiguration.signsConfiguration.set("Locations", locations);
                        Bukkit.broadcastMessage(arenaNameStrip + ";" + stringLocationConfigFormat(event.getBlock().getLocation()));
                        SignsConfiguration.signsConfiguration.save();
                        player.sendMessage("§a▪ §7Sign removed for arena: " + arenaNameStrip);
                    }
                }
            }
        }
    }

    public boolean areEquals(Location location1, Location location2) {
        if (location1.getX() == location2.getX()) {
            if (location1.getY() == location2.getY()) {
                if (location1.getZ() == location2.getZ()) {
                    if (location1.getYaw() == location2.getYaw()) {
                        if (location1.getPitch() == location2.getPitch()) {
                            if (location1.getWorld() == location2.getWorld()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void checkSign(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("parkourrun.commands.admin")) {
            for (Arena arena : ArenaManager.getArenaManager().getArenas()) {
                for (Block signBlock : arena.getSigns()) {
                    Location location = signBlock.getLocation();
                    for (String locations : Main.getSignsConfiguration().getStringList("Locations")) {
                        String[] locationsSplit = locations.split(";");
                        if (!(location.getBlock().getType().toString().endsWith("_SIGN")) || !(location.getBlock().getType().toString().endsWith("_WALL_SIGN")))
                            return;
                        Sign sign = (Sign) location.getBlock().getState();
                        Arena needUpdate = ArenaManager.getArenaManager().getArenaByID(locationsSplit[0]);
                        if (areEquals(sign.getBlock().getLocation(), signBlock.getLocation())) {
                            needUpdate.refreshSigns(arena);
                            SignsManager.updateBlock(arena);
                            int line = 0;
                            String waiting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Waiting");
                            String starting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Starting");
                            String playing = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Playing");
                            String ending = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Ending");
                            String disabled = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Disabled");
                            String status;
                            switch (arena.getArenaStatus()) {
                                case WAITING:
                                    status = waiting;
                                    break;
                                case STARTING:
                                    status = starting;
                                    break;
                                case PLAYING:
                                    status = playing;
                                    break;
                                case ENDING:
                                    status = ending;
                                    break;
                                case DISABLED:
                                    status = disabled;
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + arena.getArenaStatus());
                            }
                            for (String string : Main.getSignsConfiguration().getStringList("Format")) {
                                event.setLine(line, Utils.colorize(string.replace("[current]", String.valueOf(arena.getPlayers().size())).replace("[max]",
                                        String.valueOf(arena.getMaxPlayers())).replace("[arena]", arena.getArenaDisplayName()).replace("[status]", status)));
                                line++;
                            }
                            sign.update(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event == null)
            return;
        Player player = event.getPlayer();
        if (!player.hasPermission("parkourrun.commands.admin")) {
            return;
        }
        if (Objects.requireNonNull(event.getLine(0)).equalsIgnoreCase("[parkourrun]")) {
            File dir = new File(Main.getInstance().getDataFolder(), "/Arenas");
            boolean exists = false;
            if (dir.exists()) {
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    if (file.isFile()) {
                        if (file.getName().contains(".yml")) {
                            if (Objects.equals(event.getLine(1), file.getName().replace(".yml", ""))) {
                                exists = true;
                            }
                        }
                    }
                }
                List<String> locations;
                if (Main.getSignsConfiguration().get("Locations") == null) {
                    locations = new ArrayList<>();
                } else {
                    locations = new ArrayList<>(Main.getSignsConfiguration().getStringList("Locations"));
                }
                if (exists) {
                    if (locations.contains(event.getLine(1) + ";" + stringLocationConfigFormat(event.getBlock().getLocation())))
                        return;
                    locations.add(event.getLine(1) + ";" + stringLocationConfigFormat(event.getBlock().getLocation()));
                    SignsConfiguration.signsConfiguration.set("Locations", locations);
                    SignsConfiguration.signsConfiguration.save();
                }
                Arena arena = ArenaManager.getArenaManager().getArenaByID(event.getLine(1));
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(event.getLine(1));
                }
                if (arena != null) {
                    player.sendMessage("§a▪ §7Sign saved for arena: " + event.getLine(1));
                    arena.addSign(event.getBlock().getLocation());
                    Sign sign = (Sign) event.getBlock().getState();
                    int line = 0;
                    String waiting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Waiting");
                    String starting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Starting");
                    String playing = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Playing");
                    String ending = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Ending");
                    String disabled = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Disabled");
                    String status;
                    switch (arena.getArenaStatus()) {
                        case WAITING:
                            status = waiting;
                            break;
                        case STARTING:
                            status = starting;
                            break;
                        case PLAYING:
                            status = playing;
                            break;
                        case ENDING:
                            status = ending;
                            break;
                        case DISABLED:
                            status = disabled;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + arena.getArenaStatus());
                    }
                    for (String string : Main.getSignsConfiguration().getStringList("Format")) {
                        event.setLine(line, Utils.colorize(string.replace("[current]", String.valueOf(arena.getPlayers().size())).replace("[max]",
                                String.valueOf(arena.getMaxPlayers())).replace("[arena]", arena.getArenaDisplayName()).replace("[status]", status)));
                        line++;
                    }
                    sign.update(true);
                }
            } else {
                player.sendMessage("§c▪ §7You didn't set any arena yet!");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            if (b.getState() instanceof Sign) {
                for (Arena arena1 : ArenaManager.getArenaManager().getArenas()) {
                    if (arena1.getSigns().contains(b)) {
                        Sign sign = (Sign) b.getState();
                        String arenaID = sign.getLine(0);
                        arenaID = ChatColor.stripColor(arenaID);
                        Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                        if (arena == null) {
                            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                        }
                        if (arena == null) {
                            arenaID = sign.getLine(1);
                            arenaID = ChatColor.stripColor(arenaID);
                        }
                        arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                        if (arena == null) {
                            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                        }
                        if (arena == null) {
                            arenaID = sign.getLine(2);
                            arenaID = ChatColor.stripColor(arenaID);
                        }
                        arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                        if (arena == null) {
                            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                        }
                        if (arena == null) {
                            arenaID = sign.getLine(3);
                            arenaID = ChatColor.stripColor(arenaID);
                        }
                        arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                        if (arena == null) {
                            arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                        }
                        Utils.handleJoin(e.getPlayer(), arena);
                    }
                }
            }
        }
    }

    public String stringLocationConfigFormat(Location loc) {
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + (double) loc.getYaw() + ";" + (double) loc.getPitch() + ";" + loc.getWorld().getName();
    }
}
