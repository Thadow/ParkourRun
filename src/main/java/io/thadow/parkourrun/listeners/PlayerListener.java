package io.thadow.parkourrun.listeners;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.managers.ConfigurationManager;
import io.thadow.parkourrun.managers.SignsManager;
import io.thadow.parkourrun.menu.menus.ArenaSelectorMenu;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.SignsConfiguration;
import io.thadow.parkourrun.utils.lib.scoreboard.Scoreboard;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.StorageType;
import io.thadow.parkourrun.utils.storage.type.mysql.MySQLStorage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.text.NumberFormat;
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
        if (Utils.isSelectingCorners(event.getPlayer())) {
            Utils.getSelectingCorners().remove(event.getPlayer());
        }
        if (Utils.isInBuildingPlayers(event.getPlayer())) {
            Utils.getBuildingPlayers().remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerBreakBlockEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("parkourrun.commands.admin")) {
            if (Utils.isSelectingCorners(player)) {
                event.setCancelled(true);
                String[] split = Utils.getSelectingCorners().get(player).split("/-/");
                String arenaID = split[0];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                String action = split[1];
                switch (action) {
                    case "Win Zone Corner 1": {
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMaximumFractionDigits(2);
                        Block block = event.getBlock();
                        String format = numberFormat.format(block.getLocation().getBlockX()) + ";" + numberFormat.format(block.getLocation().getBlockY()) + ";" + numberFormat.format(block.getLocation().getBlockZ());
                        arena.setWinCorner1(format);
                        Utils.getSelectingCorners().remove(player);
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Win Zone Set");
                        message = Utils.replace(message, "%x%", numberFormat.format(block.getLocation().getBlockX()));
                        message = Utils.replace(message, "%y%", numberFormat.format(block.getLocation().getBlockY()));
                        message = Utils.replace(message, "%z%", numberFormat.format(block.getLocation().getBlockZ()));
                        message = Utils.replace(message, "%corner%", "1");
                        message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                        message = Utils.format(message);
                        player.sendMessage(message);
                        break;
                    }
                    case "Win Zone Corner 2": {
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMaximumFractionDigits(2);
                        Block block = event.getBlock();
                        String format = numberFormat.format(block.getLocation().getBlockX()) + ";" + numberFormat.format(block.getLocation().getBlockY()) + ";" + numberFormat.format(block.getLocation().getBlockZ());
                        arena.setWinCorner2(format);
                        Utils.getSelectingCorners().remove(player);
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Win Zone Set");
                        message = Utils.replace(message, "%x%", numberFormat.format(block.getLocation().getBlockX()));
                        message = Utils.replace(message, "%y%", numberFormat.format(block.getLocation().getBlockY()));
                        message = Utils.replace(message, "%z%", numberFormat.format(block.getLocation().getBlockZ()));
                        message = Utils.replace(message, "%corner%", "2");
                        message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                        message = Utils.format(message);
                        player.sendMessage(message);
                        break;
                    }
                    case "Arena Zone Corner 1": {
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMaximumFractionDigits(2);
                        Block block = event.getBlock();
                        String format = numberFormat.format(block.getLocation().getBlockX()) + ";" + numberFormat.format(block.getLocation().getBlockY()) + ";" + numberFormat.format(block.getLocation().getBlockZ());
                        arena.setArenaCorner1(format);
                        Utils.getSelectingCorners().remove(player);
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Arena Zone Set");
                        message = Utils.replace(message, "%x%", numberFormat.format(block.getLocation().getBlockX()));
                        message = Utils.replace(message, "%y%", numberFormat.format(block.getLocation().getBlockY()));
                        message = Utils.replace(message, "%z%", numberFormat.format(block.getLocation().getBlockZ()));
                        message = Utils.replace(message, "%corner%", "1");
                        message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                        message = Utils.format(message);
                        player.sendMessage(message);
                        break;
                    }
                    case "Arena Zone Corner 2": {
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMaximumFractionDigits(2);
                        Block block = event.getBlock();
                        String format = numberFormat.format(block.getLocation().getBlockX()) + ";" + numberFormat.format(block.getLocation().getBlockY()) + ";" + numberFormat.format(block.getLocation().getBlockZ());
                        arena.setArenaCorner2(format);
                        Utils.getSelectingCorners().remove(player);
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Arena Zone Set");
                        message = Utils.replace(message, "%x%", numberFormat.format(block.getLocation().getBlockX()));
                        message = Utils.replace(message, "%y%", numberFormat.format(block.getLocation().getBlockY()));
                        message = Utils.replace(message, "%z%", numberFormat.format(block.getLocation().getBlockZ()));
                        message = Utils.replace(message, "%corner%", "2");
                        message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                        message = Utils.format(message);
                        player.sendMessage(message);
                        break;
                    }
                    case "Waiting Zone Corner 1": {
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMaximumFractionDigits(2);
                        Block block = event.getBlock();
                        String format = numberFormat.format(block.getLocation().getBlockX()) + ";" + numberFormat.format(block.getLocation().getBlockY()) + ";" + numberFormat.format(block.getLocation().getBlockZ());
                        arena.setWaitingZoneCorner1(format);
                        Utils.getSelectingCorners().remove(player);
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Waiting Zone Set");
                        message = Utils.replace(message, "%x%", numberFormat.format(block.getLocation().getBlockX()));
                        message = Utils.replace(message, "%y%", numberFormat.format(block.getLocation().getBlockY()));
                        message = Utils.replace(message, "%z%", numberFormat.format(block.getLocation().getBlockZ()));
                        message = Utils.replace(message, "%corner%", "1");
                        message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                        message = Utils.format(message);
                        player.sendMessage(message);
                        break;
                    }
                    case "Waiting Zone Corner 2": {
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMaximumFractionDigits(2);
                        Block block = event.getBlock();
                        String format = numberFormat.format(block.getLocation().getBlockX()) + ";" + numberFormat.format(block.getLocation().getBlockY()) + ";" + numberFormat.format(block.getLocation().getBlockZ());
                        arena.setWaitingZoneCorner2(format);
                        Utils.getSelectingCorners().remove(player);
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Waiting Zone Set");
                        message = Utils.replace(message, "%x%", numberFormat.format(block.getLocation().getBlockX()));
                        message = Utils.replace(message, "%y%", numberFormat.format(block.getLocation().getBlockY()));
                        message = Utils.replace(message, "%z%", numberFormat.format(block.getLocation().getBlockZ()));
                        message = Utils.replace(message, "%corner%", "2");
                        message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                        message = Utils.format(message);
                        player.sendMessage(message);
                        break;
                    }
                }
            }
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
                        if (locations.contains(arenaNameStrip + ";" + stringLocationConfigFormat(event.getBlock().getLocation()))) {
                            locations.remove(arenaNameStrip + ";" + stringLocationConfigFormat(event.getBlock().getLocation()));
                            SignsConfiguration.signsConfiguration.set("Locations", locations);
                            SignsConfiguration.signsConfiguration.save();
                            String message = Main.getMessagesConfiguration().getString("Messages.Signs.Sign Removed");
                            message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                            message = Utils.format(message);
                            player.sendMessage(message);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void checkSign(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("parkourrun.commands.admin")) {
            for (Arena arena : ArenaManager.getArenaManager().getArenas()) {
                for (Block signBlock : arena.getSigns()) {
                    for (String locations : Main.getSignsConfiguration().getStringList("Locations")) {
                        String[] locationsSplit = locations.split(";");
                        Arena needUpdate = ArenaManager.getArenaManager().getArenaByID(locationsSplit[0]);
                        if (needUpdate == null) {
                            needUpdate = ArenaManager.getArenaManager().getArenaByName(locationsSplit[0]);
                        }
                        if (areEquals(event.getBlock().getLocation(), signBlock.getLocation())) {
                            Sign sign = (Sign) event.getBlock().getState();
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
                if (exists && !locations.contains(event.getLine(1) + ";" + stringLocationConfigFormat(event.getBlock().getLocation()))) {
                    locations.add(event.getLine(1) + ";" + stringLocationConfigFormat(event.getBlock().getLocation()));
                    SignsConfiguration.signsConfiguration.set("Locations", locations);
                    SignsConfiguration.signsConfiguration.save();
                }
                Arena arena = ArenaManager.getArenaManager().getArenaByID(event.getLine(1));
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(event.getLine(1));
                }
                if (arena != null) {
                    arena.addSign(event.getBlock().getLocation());
                    String message = Main.getMessagesConfiguration().getString("Messages.Signs.Sign Added");
                    message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                    message = Utils.format(message);
                    player.sendMessage(message);
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
                String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                message = Utils.format(message);
                player.sendMessage(message);
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
                        ArenaManager.getArenaManager().handleJoin(e.getPlayer(), arena);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerClickInventoryEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if (Utils.isInArenaSelectorPlayers(player)) {
            event.setCancelled(true);
            ItemStack selectedItem = event.getCurrentItem();
            if (selectedItem == null) {
                return;
            }
            if (selectedItem.getType() == Material.AIR) {
                return;
            }
            String nextMaterial = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Next Page Item");
            ItemStack nextPageItem = Main.VERSION_HANDLER.createItemStack(nextMaterial, 1, (short) 0);
            if (event.getSlot() == 53 && selectedItem == nextPageItem) {
                int newPage = (Utils.getArenaSelectorPlayers().get(player) + 1);
                ArenaSelectorMenu.open(player, newPage);
                return;
            }
            String backMaterial = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Back Page Item");
            ItemStack backPageItem = Main.VERSION_HANDLER.createItemStack(backMaterial, 1, (short) 0);
            if (event.getSlot() == 45 && selectedItem == backPageItem) {
                int newPage = (Utils.getArenaSelectorPlayers().get(player) - 1);
                ArenaSelectorMenu.open(player, newPage);
                return;
            }
            if (!Main.VERSION_HANDLER.isCustomItem(selectedItem)) {
                return;
            }

            String data = Main.VERSION_HANDLER.getData(selectedItem);
            if(!data.contains("arenaID=")) {
                return;
            }

            String arenaID = data.split("=")[1];
            Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
            if (arena == null) {
                return;
            }

            ArenaManager.getArenaManager().handleJoin(player, arena);
            if (Utils.isInArenaSelectorPlayers(player)) {
                Utils.getArenaSelectorPlayers().remove(player);
            }
            player.closeInventory();
        }
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        if (Utils.isInArenaSelectorPlayers((Player) event.getPlayer())) {
            Utils.getArenaSelectorPlayers().remove((Player) event.getPlayer());
        }
    }

    public String stringLocationConfigFormat(Location loc) {
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + (double) loc.getYaw() + ";" + (double) loc.getPitch() + ";" + loc.getWorld().getName();
    }

    public boolean areEquals(Location location1, Location location2) {
        if (location1.getX() == location2.getX()) {
            if (location1.getY() == location2.getY()) {
                if (location1.getZ() == location2.getZ()) {
                    if (location1.getYaw() == location2.getYaw()) {
                        if (location1.getPitch() == location2.getPitch()) {
                            return location1.getWorld() == location2.getWorld();
                        }
                    }
                }
            }
        }
        return false;
    }
}
