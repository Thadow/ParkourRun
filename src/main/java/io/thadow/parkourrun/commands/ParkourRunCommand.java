package io.thadow.parkourrun.commands;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.CheckpointManager;
import io.thadow.parkourrun.menu.Menu;
import io.thadow.parkourrun.utils.Permission;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.MainConfiguration;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

public class ParkourRunCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            if (player.hasPermission("parkourrun.commands.join")) {
                if (ArenaManager.getArenaManager().getArena(player) != null && Main.isBungeecord()) {
                    return true;
                }
                String arenaID = args[1];
                if (arenaID.equalsIgnoreCase("random")) {
                    if (Main.isBungeecord() && Main.isLobbyServer()) {
                        String arena = Utils.getAvailableBungeeArena();
                        if (!arena.equals("NO ARENAS AVAILABLE")) {
                            Utils.sendPlayerTo(player, arena);
                        } else {
                                String message = Main.getMessagesConfiguration().getString("Messages.Arena.No Arenas Available");
                                message = Utils.format(message);
                                player.sendMessage(message);
                        }
                        return true;
                    }
                    if (!ArenaManager.getArenaManager().joinRandom(player)) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.No Arenas Available");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    return true;
                }
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    if (Utils.isBungeeArena(arenaID)) {
                            if (Utils.bungeeArenas.containsKey(arenaID)) {
                                String[] split = Utils.bungeeArenas.get(arenaID).split("/-/");
                                if (split[3] == null) {
                                    Debugger.debug(DebugType.ALERT, "&cError while trying to connect to a server");
                                    return true;
                                }
                                if (split[3].equals("PLAYING")) {
                                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Playing");
                                    message = Utils.format(message);
                                    player.sendMessage(message);
                                    return true;
                                }
                                if (split[3].equals("ENDING")) {
                                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Ending");
                                    message = Utils.format(message);
                                    player.sendMessage(message);
                                    return true;
                                }
                                if (split[3].equals("RESTARTING")) {
                                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Restarting");
                                    message = Utils.format(message);
                                    player.sendMessage(message);
                                    return true;
                                }
                                if (split[3].equals("DISABLED")) {
                                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Arena Disabled");
                                    message = Utils.format(message);
                                    player.sendMessage(message);
                                    return true;
                                }
                                Utils.sendPlayerTo(player, arenaID);
                                return true;
                            }
                    } else {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                }
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                ArenaManager.getArenaManager().handleJoin(player, arena);
            } else {
                Permission.deny(player, "parkourrun.commands.join");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("build")) {
            if (player.hasPermission("parkourrun.commands.build")) {
                if (Utils.isInBuildingPlayers(player)) {
                    Utils.getBuildingPlayers().remove(player);
                    String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Build Disabled");
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    Utils.getBuildingPlayers().add(player);
                    String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Build Enabled");
                    message = Utils.format(message);
                    player.sendMessage(message);
                }
            } else {
                Permission.deny(player, "parkourrun.commands.build");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("gui")) {
            if (player.hasPermission("parkourrun.commands.gui")) {
                if (Main.isBungeecord() && !Main.isLobbyServer()) {
                    Arena arena = ArenaManager.getArenaManager().getArena(player);
                    if (arena != null) {
                        return true;
                    }
                }
                    Menu.openArenaSelectorMenuTo(player, 1);
            } else {
                Permission.deny(player, "parkourrun.commands.gui");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setSpawn")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String locationString = player.getWorld().getName() + ";" + numberFormat.format(player.getLocation().getX()) + ";"
                        + numberFormat.format(player.getLocation().getY()) + ";" + numberFormat.format(player.getLocation().getZ()) + ";"
                        + numberFormat.format(player.getLocation().getYaw()) + ";" + numberFormat.format(player.getLocation().getPitch());
                arena.setSpawn(player.getLocation(), locationString);
                String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Spawn Location Set");
                message = Utils.replace(message, "%world%", player.getWorld().getName());
                message = Utils.replace(message, "%x%", numberFormat.format(player.getLocation().getX()));
                message = Utils.replace(message, "%y%", numberFormat.format(player.getLocation().getY()));
                message = Utils.replace(message, "%z%", numberFormat.format(player.getLocation().getZ()));
                message = Utils.replace(message, "%pitch%", numberFormat.format(player.getLocation().getPitch()));
                message = Utils.replace(message, "%yaw%", numberFormat.format(player.getLocation().getYaw()));
                message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setWaitSpawn")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String locationString = player.getWorld().getName() + ";" + numberFormat.format(player.getLocation().getX()) + ";"
                        + numberFormat.format(player.getLocation().getY()) + ";" + numberFormat.format(player.getLocation().getZ()) + ";"
                        + numberFormat.format(player.getLocation().getYaw()) + ";" + numberFormat.format(player.getLocation().getPitch());
                arena.setWaitLocation(player.getLocation(), locationString);
                String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Wait Location Set");
                message = Utils.replace(message, "%world%", player.getWorld().getName());
                message = Utils.replace(message, "%x%", numberFormat.format(player.getLocation().getX()));
                message = Utils.replace(message, "%y%", numberFormat.format(player.getLocation().getY()));
                message = Utils.replace(message, "%z%", numberFormat.format(player.getLocation().getZ()));
                message = Utils.replace(message, "%pitch%", numberFormat.format(player.getLocation().getPitch()));
                message = Utils.replace(message, "%yaw%", numberFormat.format(player.getLocation().getYaw()));
                message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("setLobby")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && !Main.isLobbyServer()) {
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String locationString = player.getWorld().getName() + ";" + numberFormat.format(player.getLocation().getX()) + ";"
                        + numberFormat.format(player.getLocation().getY()) + ";" + numberFormat.format(player.getLocation().getZ()) + ";"
                        + numberFormat.format(player.getLocation().getYaw()) + ";" + numberFormat.format(player.getLocation().getPitch());
                String[] split = locationString.split(";");
                Main.getInstance().getConfiguration().set("Configuration.Lobby.Location.World", split[0]);
                Main.getInstance().getConfiguration().set("Configuration.Lobby.Location.X", split[1]);
                Main.getInstance().getConfiguration().set("Configuration.Lobby.Location.Y", split[2]);
                Main.getInstance().getConfiguration().set("Configuration.Lobby.Location.Z", split[3]);
                Main.getInstance().getConfiguration().set("Configuration.Lobby.Location.Yaw", split[4]);
                Main.getInstance().getConfiguration().set("Configuration.Lobby.Location.Pitch", split[5]);
                MainConfiguration.mainConfiguration.save();
                Main.setIsLobbyPresent(true);
                String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Lobby Location Set");
                message = Utils.replace(message, "%world%", split[0]);
                message = Utils.replace(message, "%x%", split[1]);
                message = Utils.replace(message, "%y%", split[2]);
                message = Utils.replace(message, "%z%", split[3]);
                message = Utils.replace(message, "%yaw%", split[4]);
                message = Utils.replace(message, "%pitch%", split[5]);
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setMinPlayers")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                    if (arena == null) {
                        arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                    }
                    if (arena == null) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setMinPlayers(Integer.parseInt(args[1]));
                    if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        sendInfoMessage(player, arena);
                    }
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Min Players Set");
                    message = Utils.replace(message, "%minPlayers%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = Main.getMessagesConfiguration().getString("Messages.Commands.Invalid Number");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setMaxPlayers")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                    if (arena == null) {
                        arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                    }
                    if (arena == null) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setMaxPlayers(Integer.parseInt(args[1]));
                    if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        sendInfoMessage(player, arena);
                    }
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Max Players Set");
                    message = Utils.replace(message, "%maxPlayers%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = Main.getMessagesConfiguration().getString("Messages.Commands.Invalid Number");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setWaitTime")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                    if (arena == null) {
                        arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                    }
                    if (arena == null) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setTime(Integer.parseInt(args[1]), true, true);
                    if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        sendInfoMessage(player, arena);
                    }
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Wait Time Set");
                    message = Utils.replace(message, "%time%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = Main.getMessagesConfiguration().getString("Messages.Commands.Invalid Number");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setReEnableTime")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                    if (arena == null) {
                        arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                    }
                    if (arena == null) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setReEnableTime(Integer.parseInt(args[1]));
                    if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        sendInfoMessage(player, arena);
                    }
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Re-Enable Time Set");
                    message = Utils.replace(message, "%time%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = Main.getMessagesConfiguration().getString("Messages.Commands.Invalid Number");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setEndingTime")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                    if (arena == null) {
                        arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                    }
                    if (arena == null) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setEndingTime(Integer.parseInt(args[1]));
                    if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        sendInfoMessage(player, arena);
                    }
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Ending Time Set");
                    message = Utils.replace(message, "%time%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = Main.getMessagesConfiguration().getString("Messages.Commands.Invalid Number");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setMaxTime")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                    if (arena == null) {
                        arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                    }
                    if (arena == null) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setMaxTime(Integer.parseInt(args[1]), true, true);
                    if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        sendInfoMessage(player, arena);
                    }
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Max Time Set");
                    message = Utils.replace(message, "%time%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = Main.getMessagesConfiguration().getString("Messages.Commands.Invalid Number");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("setArenaName")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                StringBuilder nombre = new StringBuilder(args[2]);
                for (int i = 3; i < args.length; i++) {
                    nombre.append(" ").append(args[i]);
                }
                arena.setArenaDisplayName(String.valueOf(nombre));
                if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                    sendInfoMessage(player, arena);
                }
                String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Arena Name Set");
                message = Utils.replace(message, "%arenaName%", arena.getArenaDisplayName());
                message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setWinCorner") && args[1].equalsIgnoreCase("1")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                Utils.getSelectingCorners().put(player, arenaID + "/-/Win Zone Corner 1");
                String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Selecting Corner");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setWinCorner") && args[1].equalsIgnoreCase("2")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                Utils.getSelectingCorners().put(player, arenaID + "/-/Win Zone Corner 2");
                String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Selecting Corner");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setArenaCorner") && args[1].equalsIgnoreCase("1")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                Utils.getSelectingCorners().put(player, arenaID + "/-/Arena Zone Corner 1");
                String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Selecting Corner");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setArenaCorner") && args[1].equalsIgnoreCase("2")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                Utils.getSelectingCorners().put(player, arenaID + "/-/Arena Zone Corner 2");
                String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Selecting Corner");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setWaitingCorner") && args[1].equalsIgnoreCase("1")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                Utils.getSelectingCorners().put(player, arenaID + "/-/Waiting Zone Corner 1");
                String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Selecting Corner");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
    } else if (args.length == 3 && args[0].equalsIgnoreCase("setWaitingCorner") && args[1].equalsIgnoreCase("2")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                Utils.getSelectingCorners().put(player, arenaID + "/-/Waiting Zone Corner 2");
                String message = Main.getMessagesConfiguration().getString("Messages.Commands.Main Command.Selecting Corner");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }

        } else if (args.length == 2 && args[0].equalsIgnoreCase("addCheckPoint")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                Material underBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
                String underBlockName = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Checkpoints.Under Block");
                if (underBlock == Material.valueOf(underBlockName)) {
                    String checkpointLocation = player.getLocation().getWorld().getName() + ";" + player.getLocation().getX() + ";" + player.getLocation().getY() + ";" + player.getLocation().getZ()
                            + ";" + player.getLocation().getYaw() + ";" + player.getLocation().getPitch();
                    int newY = player.getLocation().getBlockY() + 1;
                    int newYdown = player.getLocation().getBlockY() - 1;
                    int newX1 = player.getLocation().getBlockX() - 1;
                    int newZ1 = player.getLocation().getBlockZ() + 1;
                    int newX2 = player.getLocation().getBlockX() + 1;
                    int newZ2 = player.getLocation().getBlockZ() - 1;
                    String corner1 = newX1 + ";" + newY + ";" + newZ1;
                    String corner2 = newX2 + ";" + newYdown + ";" + newZ2;
                    int totalCheckpoints = CheckpointManager.getCheckpointManager().getTotalCheckpoints(arena);
                    if (totalCheckpoints == 0) {
                        arena.addCheckpoint(1, checkpointLocation + "/-/" + corner1 + "/-/" + corner2);
                        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                            sendInfoMessage(player, arena);
                        }
                        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Checkpoint Added");
                        message = Utils.replace(message, "%checkpointID%", "1");
                        message = Utils.replace(message, "%totalCheckpoints%", "1");
                        message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                        message = Utils.format(message);
                        player.sendMessage(message);
                    } else {
                        for (int i = 1; i <= totalCheckpoints + 1; i++) {
                            if (!arena.getConfiguration().contains("Checkpoints." + i + ".Location")) {
                                arena.addCheckpoint(i, checkpointLocation + "/-/" + corner1 + "/-/" + corner2);
                                if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                                    sendInfoMessage(player, arena);
                                }
                                String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Checkpoint Added");
                                message = Utils.replace(message, "%checkpointID%", String.valueOf(i));
                                message = Utils.replace(message, "%totalCheckpoints%", String.valueOf(arena.getCheckpoints().size()));
                                message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                                message = Utils.format(message);
                                player.sendMessage(message);
                            }
                        }
                    }
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("deleteLastCheckpoint")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() != ArenaStatus.DISABLED) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Modify");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                int lastCheckpoint = CheckpointManager.getCheckpointManager().getTotalCheckpoints(arena);
                if (lastCheckpoint <= 0) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Checkpoint.No Checkpoints");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                arena.deleteCheckpoint(lastCheckpoint);
                if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                    sendInfoMessage(player, arena);
                }
                String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Checkpoint Deleted");
                message = Utils.replace(message, "%checkpointID%", String.valueOf(lastCheckpoint));
                message = Utils.replace(message, "%totalCheckpoints%", String.valueOf(arena.getCheckpoints().size()));
                message = Utils.replace(message, "%arenaID%", arena.getArenaID());
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("createArena")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Create On Lobby");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (!Main.isLobbyPresent() && !Main.isLobbyServer()) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Unknown Lobby");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                String arenaID = args[1];
                if (ArenaManager.getArenaManager().createArena(arenaID)) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Arena Created");
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Arena Already Exists");
                    message = Utils.format(message);
                    player.sendMessage(message);
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("deleteArena")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[1];
                if (ArenaManager.getArenaManager().deleteArena(arenaID)) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Arena Deleted");
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("disableArena")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() == ArenaStatus.PLAYING || arena.getArenaStatus() == ArenaStatus.ENDING) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Can't Disable");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (!arena.isEnabled()) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Arena Already Disabled");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                arena.setEnabled(false, true);
                String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Arena Disabled.Message");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("enableArena")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (Main.isBungeecord() && Main.isLobbyServer()) {
                    return true;
                }
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArenaByID(arenaID);
                if (arena == null) {
                    arena = ArenaManager.getArenaManager().getArenaByName(arenaID);
                }
                if (arena == null) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.isEnabled()) {
                    String message = Main.getMessagesConfiguration().getString("Messages.Arena.Arena Already Enabled");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                arena.setEnabled(true, false);
                String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Arena Enabled");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else {
            List<String> messageList = Main.getMessagesConfiguration().getStringList("Messages.Commands.Main Command.Usages");
            for (String line : messageList) {
                line = Utils.format(line);
                player.sendMessage(line);
            }
        }
        return false;
    }

    public void sendInfoMessage(Player player, Arena arena) {
        List<String> messageList = Main.getMessagesConfiguration().getStringList("Messages.Arena.Parameter Changed.Show Info.Message");
        String arenaName = arena.getArenaDisplayName();
        boolean enabled = arena.isEnabled();
        int waitTime = arena.getTime();
        int reEnableTime = arena.getReEnableTime();
        int endingTime = arena.getEndingTime();
        int minPlayers = arena.getMinPlayers();
        int maxPlayers = arena.getMaxPlayers();
        int maxTime = arena.getMaxTime();
        int totalCheckpoints = CheckpointManager.getCheckpointManager().getTotalCheckpoints(arena);
        for (String line : messageList) {
            line = Utils.replace(line, "%enabled%", String.valueOf(enabled));
            line = Utils.replace(line, "%arenaID%", arena.getArenaID());
            line = Utils.replace(line, "%arenaName%", arenaName);
            line = Utils.replace(line, "%waitTime%", String.valueOf(waitTime));
            line = Utils.replace(line, "%reEnableTime%", String.valueOf(reEnableTime));
            line = Utils.replace(line, "%endingTime%", String.valueOf(endingTime));
            line = Utils.replace(line, "%minPlayers%", String.valueOf(minPlayers));
            line = Utils.replace(line, "%maxPlayers%", String.valueOf(maxPlayers));
            line = Utils.replace(line, "%maxTime%", String.valueOf(maxTime));
            line = Utils.replace(line, "%totalCheckpoints%", String.valueOf(totalCheckpoints));
            line = Utils.colorize(line);
            player.sendMessage(line);
        }
    }
}
