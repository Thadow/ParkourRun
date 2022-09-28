package io.thadow.parkourrun.listeners;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.api.proxy.BungeeArena;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.items.Items;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.managers.CheckpointManager;
import io.thadow.parkourrun.managers.ConfigurationManager;
import io.thadow.parkourrun.managers.SignsManager;
import io.thadow.parkourrun.menu.Menu;
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
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
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

        player.getInventory().clear();
        if (Main.isBungeecord() && !Main.isLobbyServer()) {
            if (ArenaManager.getArenaManager().getArenas().size() >= 1) {
                Arena arena = ArenaManager.getArenaManager().getArenas().get(0);
                ArenaManager.getArenaManager().handleJoin(player, arena);
            }
        }

        if (Main.isBungeecord()) {
            if (Main.isLobbyServer()) {
                String gamemode = Main.getInstance().getConfiguration().getString("Configuration.Lobby.Configurations.GameMode");
                player.setGameMode(GameMode.valueOf(gamemode));
                boolean fly = Main.getInstance().getConfiguration().getBoolean("Configuration.Lobby.Configurations.Disable Flight");
                boolean setFliying = Main.getInstance().getConfiguration().getBoolean("Configuration.Lobby.Configurations.Set Flying");
                if (fly) {
                    player.setAllowFlight(false);
                }
                if (setFliying) {
                    player.setFlying(true);
                }
                Items.giveLobbyItemsTo(player);
            }
        } else {
            String gamemode = Main.getInstance().getConfiguration().getString("Configuration.Lobby.Configurations.GameMode");
            player.setGameMode(GameMode.valueOf(gamemode));
            boolean fly = Main.getInstance().getConfiguration().getBoolean("Configuration.Lobby.Configurations.Disable Flight");
            boolean setFliying = Main.getInstance().getConfiguration().getBoolean("Configuration.Lobby.Configurations.Set Flying");
            if (fly) {
                player.setAllowFlight(false);
            }
            if (setFliying) {
                player.setFlying(true);
            }
            Items.giveLobbyItemsTo(player);
        }
    }

    @EventHandler
    public void onPlayerPreJoin2(PlayerLoginEvent event) {
        if (Main.isBungeecord() && !Main.isLobbyServer()) {
            if (ArenaManager.getArenaManager().getArenas().size() >= 1) {
                if (ArenaManager.getArenaManager().getArenas().get(0).getArenaStatus() == ArenaStatus.PLAYING) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Playing");
                    message = Utils.format(message);
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.colorize(message));
                }
                if (ArenaManager.getArenaManager().getArenas().get(0).getArenaStatus() == ArenaStatus.ENDING) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Ending");
                    message = Utils.format(message);
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.colorize(message));
                }
                if (ArenaManager.getArenaManager().getArenas().get(0).getArenaStatus() == ArenaStatus.RESTARTING) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Restarting");
                    message = Utils.format(message);
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.colorize(message));
                }
                if (ArenaManager.getArenaManager().getArenas().get(0).getArenaStatus() == ArenaStatus.DISABLED && !event.getPlayer().hasPermission("parkourrun.commands.admin")) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Arena Disabled");
                    message = Utils.format(message);
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.colorize(message));
                }
            }
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
                        arena.setWaitingZoneCorner1(block.getLocation());
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
                        arena.setWaitingZoneCorner2(block.getLocation());
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
                            String restarting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Restarting");
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
                                case RESTARTING:
                                    status = restarting;
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
                    String restarting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Restarting");
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
                        case RESTARTING:
                            status = restarting;
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
        Arena playerArena = ArenaManager.getArenaManager().getArena(player);
        if (playerArena == null) {
            if (Main.getInstance().getConfig().getBoolean("Configuration.Lobby.Configurations.Disable Move Inv Items")) {
                event.setCancelled(true);
            }
        } else {
            if (Main.getInstance().getConfig().getBoolean("Configuration.Arenas.Configurations.Disable Move Inv Items")) {
                event.setCancelled(true);
            }
        }
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
            int nextDataMaterial = Main.getInstance().getConfiguration().getInt("Configuration.Arena Selector.Menu.Next Page Item Data");
            ItemStack nextPageItem = Main.VERSION_HANDLER.createItemStack(nextMaterial, 1, (short) nextDataMaterial);
            if (event.getSlot() == 53 && selectedItem == nextPageItem) {
                int newPage = (Utils.getArenaSelectorPlayers().get(player) + 1);
                ArenaSelectorMenu.open(player, newPage);
                return;
            }
            String backMaterial = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Back Page Item");
            int backDataMaterial = Main.getInstance().getConfiguration().getInt("Configuration.Arena Selector.Menu.Back Page Item Data");
            ItemStack backPageItem = Main.VERSION_HANDLER.createItemStack(backMaterial, 1, (short) backDataMaterial);
            if (event.getSlot() == 45 && selectedItem == backPageItem) {
                int newPage = (Utils.getArenaSelectorPlayers().get(player) - 1);
                ArenaSelectorMenu.open(player, newPage);
                return;
            }
            if (!Main.VERSION_HANDLER.isCustomItem(selectedItem)) {
                return;
            }
            String data = Main.VERSION_HANDLER.getData(selectedItem);
            if (Main.isBungeecord()) {
                if(!data.contains("server=")) {
                    return;
                }
            } else {
                if(!data.contains("arenaID=")) {
                    return;
                }
            }

            if (Main.isBungeecord()) {
                String server = data.split("=")[1];
                if (!Utils.isBungeeArenaTest(server)) {
                    return;
                }
                BungeeArena arena = null;
                for (BungeeArena arenas : Utils.bungeeArenasTest) {
                    if (arenas.getServerID().equals(server)) {
                        arena = arenas;
                    }
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    player.closeInventory();
                    return;
                }
                if (arena.getStatus().equals("PLAYING")) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Playing");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    player.closeInventory();
                    return;
                }
                if (arena.getStatus().equals("RESTARTING")) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Restarting");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    player.closeInventory();
                    return;
                }
                if (arena.getStatus().equals("ENDING")) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Ending");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    player.closeInventory();
                    return;
                }
                if (!Utils.sendPlayerTo(player, server)) {
                    player.closeInventory();
                }
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
    public void onHungerLevel(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Arena arena = ArenaManager.getArenaManager().getArena(player);
            if (arena == null) {
                if (Main.getInstance().getConfiguration().getBoolean("Configuration.Lobby.Configurations.Disable Hunger")) {
                    event.setFoodLevel(20);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand.getType() == null) {
            return;
        }
        if (itemInHand.getType() == Material.AIR) {
            return;
        }

        if (event.getAction().equals(Action.PHYSICAL)) {
            return;
        }

        if (Main.VERSION_HANDLER.isCustomItem(itemInHand)) {
            String data = Main.VERSION_HANDLER.getData(itemInHand);
            if (data.equals("ArenaSelectorItem")) {
                event.setCancelled(true);
                Menu.openArenaSelectorMenuTo(player, 1);
                return;
            }
            if (data.equals("LeaveItemLobby")) {
                event.setCancelled(true);
                String lobbyID = Main.getInstance().getConfiguration().getString("Configuration.Items.Lobby.Leave Item.Lobby Server");
                Utils.sendPlayerTo(player, lobbyID);
                return;
            }
            if (data.equals("LeaveItemArena")) {
                Arena arena = ArenaManager.getArenaManager().getArena(player);
                if (arena != null) {
                    if (Main.isBungeecord()) {
                        if (!Main.isLobbyServer()) {
                            event.setCancelled(true);
                            String lobbyId = Main.getInstance().getConfiguration().getString("Configuration.BungeeCord.Lobby Server");
                            player.getInventory().clear();
                            Utils.sendPlayerTo(player, lobbyId);
                            return;
                        }
                    } else {
                        event.setCancelled(true);
                        ArenaManager.getArenaManager().removePlayer(player, arena.getArenaStatus() == ArenaStatus.ENDING);
                        player.teleport(Utils.getLobbyLocation());
                        Items.giveLobbyItemsTo(player);
                    }
                }
                return;
            }
            if (data.equals("BackCheckpointItem")) {
                Arena arena = ArenaManager.getArenaManager().getArena(player);
                if (arena != null) {
                    event.setCancelled(true);
                    if (arena.getCheckpoints().size() == 0) {
                        return;
                    }
                    if (arena.getCheckpoints() == null) {
                        return;
                    }
                    if (CheckpointManager.getCheckpointManager().getPlayerNextCheckpoint(player) == null) {
                        return;
                    }
                    int currentCheckpointID = CheckpointManager.getCheckpointManager().getPlayerCurrentCheckpoint(player);
                    if (currentCheckpointID == 0) {
                        return;
                    }
                    Location location = CheckpointManager.getCheckpointManager().getCheckpointLocation(arena, currentCheckpointID);
                    player.teleport(location);
                }
            }
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
