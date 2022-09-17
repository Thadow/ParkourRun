package io.thadow.parkourrun.commands;

import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.CheckpointManager;
import io.thadow.parkourrun.utils.Permission;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.MessagesConfiguration;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                String arenaID = args[1];
                if (arenaID.equalsIgnoreCase("random")) {
                    ArenaManager.getArenaManager().joinRandom(player);
                    return true;
                }
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (ArenaManager.getArenaManager().getArena(player) != null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Already In Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() == ArenaStatus.PLAYING) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.In Game");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() == ArenaStatus.ENDING) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Ending");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                ArenaManager.getArenaManager().addPlayer(player, arenaID);
            } else {
                Permission.deny(player, "parkourrun.commands.join");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setSpawn")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String locationString = player.getWorld().getName() + ";" + numberFormat.format(player.getLocation().getX()) + ";"
                        + numberFormat.format(player.getLocation().getY()) + ";" + numberFormat.format(player.getLocation().getZ()) + ";"
                        + numberFormat.format(player.getLocation().getYaw()) + ";" + numberFormat.format(player.getLocation().getPitch());
                ArenaManager.getArenaManager().getArena(arenaID).setSpawn(player.getLocation(), locationString);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Spawn Location Set");
                message = Utils.replace(message, "%world%", player.getWorld().getName());
                message = Utils.replace(message, "%x%", numberFormat.format(player.getLocation().getX()));
                message = Utils.replace(message, "%y%", numberFormat.format(player.getLocation().getY()));
                message = Utils.replace(message, "%z%", numberFormat.format(player.getLocation().getZ()));
                message = Utils.replace(message, "%pitch%", numberFormat.format(player.getLocation().getPitch()));
                message = Utils.replace(message, "%yaw%", numberFormat.format(player.getLocation().getYaw()));
                message = Utils.replace(message, "%arenaID%", arenaID);
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setWaitSpawn")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String locationString = player.getWorld().getName() + ";" + numberFormat.format(player.getLocation().getX()) + ";"
                        + numberFormat.format(player.getLocation().getY()) + ";" + numberFormat.format(player.getLocation().getZ()) + ";"
                        + numberFormat.format(player.getLocation().getYaw()) + ";" + numberFormat.format(player.getLocation().getPitch());
                ArenaManager.getArenaManager().getArena(arenaID).setWaitLocation(player.getLocation(), locationString);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Wait Location Set");
                message = Utils.replace(message, "%world%", player.getWorld().getName());
                message = Utils.replace(message, "%x%", numberFormat.format(player.getLocation().getX()));
                message = Utils.replace(message, "%y%", numberFormat.format(player.getLocation().getY()));
                message = Utils.replace(message, "%z%", numberFormat.format(player.getLocation().getZ()));
                message = Utils.replace(message, "%pitch%", numberFormat.format(player.getLocation().getPitch()));
                message = Utils.replace(message, "%yaw%", numberFormat.format(player.getLocation().getYaw()));
                message = Utils.replace(message, "%arenaID%", arenaID);
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setMinPlayers")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                    if (arena == null) {
                        String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setMinPlayers(Integer.parseInt(args[1]));
                    String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Min Players Set");
                    message = Utils.replace(message, "%minPlayers%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arenaID);
                    message = Utils.format(message);
                    player.sendMessage(message);
                    if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                            MessagesConfiguration.sendInfoMessage(player, arena, arenaID);
                        }
                    }
                } else {
                    player.sendMessage("Numero invalido");
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setMaxPlayers")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                    if (arena == null) {
                        String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setMaxPlayers(Integer.parseInt(args[1]));
                    String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Max Players Set");
                    message = Utils.replace(message, "%maxPlayers%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arenaID);
                    message = Utils.format(message);
                    player.sendMessage(message);
                    if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        MessagesConfiguration.sendInfoMessage(player, arena, arenaID);
                    }
                } else {
                    player.sendMessage("Numero invalido");
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setWaitTime")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                    if (arena == null) {
                        String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setTime(Integer.parseInt(args[1]));
                    String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Wait Time Set");
                    message = Utils.replace(message, "%time%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arenaID);
                    message = Utils.format(message);
                    player.sendMessage(message);
                    if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                            MessagesConfiguration.sendInfoMessage(player, arena, arenaID);
                        }
                    }
                } else {
                    player.sendMessage("Numero invalido");
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setWaitTimeToReEnable")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                if (NumberUtils.isNumber(args[1])) {
                    String arenaID = args[2];
                    Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                    if (arena == null) {
                        String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                        message = Utils.format(message);
                        player.sendMessage(message);
                        return true;
                    }
                    arena.setReEnableCount(Integer.parseInt(args[1]));
                    String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Wait Time To Re-Enable");
                    message = Utils.replace(message, "%time%", args[1]);
                    message = Utils.replace(message, "%arenaID%", arenaID);
                    message = Utils.format(message);
                    player.sendMessage(message);
                    if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                            MessagesConfiguration.sendInfoMessage(player, arena, arenaID);
                        }
                    }
                } else {
                    player.sendMessage("Numero invalido");
                    return true;
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("setArenaName")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                StringBuilder nombre = new StringBuilder(args[2]);
                for (int i = 3; i < args.length; i++) {
                    nombre.append(" ").append(args[i]);
                }
                arena.setArenaDisplayName(String.valueOf(nombre));
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Arena Name Set");
                message = Utils.replace(message, "%arenaName%", arena.getArenaDisplayName());
                message = Utils.replace(message, "%arenaID%", arenaID);
                message = Utils.format(message);
                player.sendMessage(message);
                if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                    if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        MessagesConfiguration.sendInfoMessage(player, arena, arenaID);
                    }
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setWinZone") && args[1].equalsIgnoreCase("pos1")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String format = numberFormat.format(player.getLocation().getBlockX()) + ";" + numberFormat.format(player.getLocation().getBlockY()) + ";" + numberFormat.format(player.getLocation().getBlockZ());
                arena.setWinCorner1(format);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Win Zone Set");
                message = Utils.replace(message, "%x%", numberFormat.format(player.getLocation().getBlockX()));
                message = Utils.replace(message, "%y%", numberFormat.format(player.getLocation().getBlockY()));
                message = Utils.replace(message, "%z%", numberFormat.format(player.getLocation().getBlockZ()));
                message = Utils.replace(message, "%corner%", "1");
                message = Utils.replace(message, "%arenaID%", arenaID);
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setWinZone") && args[1].equalsIgnoreCase("pos2")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String format = numberFormat.format(player.getLocation().getBlockX()) + ";" + numberFormat.format(player.getLocation().getBlockY()) + ";" + numberFormat.format(player.getLocation().getBlockZ());
                arena.setWinCorner2(format);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Win Zone Set");
                message = Utils.replace(message, "%x%", numberFormat.format(player.getLocation().getBlockX()));
                message = Utils.replace(message, "%y%", numberFormat.format(player.getLocation().getBlockY()));
                message = Utils.replace(message, "%z%", numberFormat.format(player.getLocation().getBlockZ()));
                message = Utils.replace(message, "%corner%", "2");
                message = Utils.replace(message, "%arenaID%", arenaID);
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setArenaZone") && args[1].equalsIgnoreCase("pos1")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String format = numberFormat.format(player.getLocation().getBlockX()) + ";" + numberFormat.format(player.getLocation().getBlockY()) + ";" + numberFormat.format(player.getLocation().getBlockZ());
                arena.setArenaCorner1(format);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Arena Zone Set");
                message = Utils.replace(message, "%x%", numberFormat.format(player.getLocation().getBlockX()));
                message = Utils.replace(message, "%y%", numberFormat.format(player.getLocation().getBlockY()));
                message = Utils.replace(message, "%z%", numberFormat.format(player.getLocation().getBlockZ()));
                message = Utils.replace(message, "%corner%", "1");
                message = Utils.replace(message, "%arenaID%", arenaID);
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setArenaZone") && args[1].equalsIgnoreCase("pos2")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[2];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String format = numberFormat.format(player.getLocation().getBlockX()) + ";" + numberFormat.format(player.getLocation().getBlockY()) + ";" + numberFormat.format(player.getLocation().getBlockZ());
                arena.setArenaCorner2(format);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Arena Zone Set");
                message = Utils.replace(message, "%x%", numberFormat.format(player.getLocation().getBlockX()));
                message = Utils.replace(message, "%y%", numberFormat.format(player.getLocation().getBlockY()));
                message = Utils.replace(message, "%z%", numberFormat.format(player.getLocation().getBlockZ()));
                message = Utils.replace(message, "%corner%", "2");
                message = Utils.replace(message, "%arenaID%", arenaID);
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("addCheckPoint")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                Material underBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
                if (underBlock == Material.EMERALD_BLOCK) {
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
                    int totalCheckpoints = CheckpointManager.getCheckpointManager().getTotalCheckpoints(ArenaManager.getArenaManager().getArena(arenaID));
                    if (totalCheckpoints == 0) {
                        arena.addCheckpoint(1, checkpointLocation + "/-/" + corner1 + "/-/" + corner2);
                        String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Checkpoint Added");
                        message = Utils.replace(message, "%checkpointID%", "1");
                        message = Utils.replace(message, "%totalCheckpoints%", String.valueOf(totalCheckpoints));
                        message = Utils.replace(message, "%arenaID%", arenaID);
                        message = Utils.format(message);
                        player.sendMessage(message);
                        if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                            if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                                MessagesConfiguration.sendInfoMessage(player, arena, arenaID);
                            }
                        }
                    } else {
                        for (int i = 1; i <= totalCheckpoints + 1; i++) {
                            if (!arena.configContains("Arenas." + arenaID + ".Checkpoints." + i + ".Location")) {
                                arena.addCheckpoint(i, checkpointLocation + "/-/" + corner1 + "/-/" + corner2);
                                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Checkpoint Added");
                                message = Utils.replace(message, "%checkpointID%", String.valueOf(i));
                                message = Utils.replace(message, "%totalCheckpoints%", String.valueOf(totalCheckpoints));
                                message = Utils.replace(message, "%arenaID%", arenaID);
                                message = Utils.format(message);
                                player.sendMessage(message);
                                if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                                    if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                                        MessagesConfiguration.sendInfoMessage(player, arena, arenaID);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("deleteCheckpoint")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                int lastCheckpoint = CheckpointManager.getCheckpointManager().getTotalCheckpoints(arena);
                arena.deleteCheckpoint(lastCheckpoint);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Checkpoint Deleted");
                message = Utils.replace(message, "%checkpointID%", String.valueOf(lastCheckpoint));
                message = Utils.replace(message, "%totalCheckpoints%", String.valueOf(lastCheckpoint - 1));
                message = Utils.replace(message, "%arenaID%", arenaID);
                message = Utils.format(message);
                player.sendMessage(message);
                if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                    if (MessagesConfiguration.getBoolean("Messages.Arena.Parameter Changed.Show Info.Enabled")) {
                        MessagesConfiguration.sendInfoMessage(player, arena, arenaID);
                    }
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("createArena")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                if (ArenaManager.getArenaManager().createArena(player, arenaID)) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Arena Created");
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Arena Already Exists");
                    message = Utils.format(message);
                    player.sendMessage(message);
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("deleteArena")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                if (ArenaManager.getArenaManager().deleteArena(player, arenaID)) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Arena Deleted");
                    message = Utils.format(message);
                    player.sendMessage(message);
                } else {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                }
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("disableArena")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.getArenaStatus() == ArenaStatus.PLAYING || arena.getArenaStatus() == ArenaStatus.ENDING) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Can't Disable");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (!arena.isEnabled()) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Arena Already Disabled");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                arena.setEnabled(false);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Arena Disabled.Message");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("enableArena")) {
            if (player.hasPermission("parkourrun.commands.admin")) {
                String arenaID = args[1];
                Arena arena = ArenaManager.getArenaManager().getArena(arenaID);
                if (arena == null) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Unknown Arena");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                if (arena.isEnabled()) {
                    String message = MessagesConfiguration.getPath("Messages.Arena.Arena Already Enabled");
                    message = Utils.format(message);
                    player.sendMessage(message);
                    return true;
                }
                arena.setEnabled(true);
                String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Arena Enabled");
                message = Utils.format(message);
                player.sendMessage(message);
            } else {
                Permission.deny(player, "parkourrun.commands.admin");
            }
        } else {
            List<String> messageList = MessagesConfiguration.getListPath("Messages.Commands.Main Command.Usages");
            for (String line : messageList) {
                line = Utils.format(line);
                player.sendMessage(line);
            }
        }
        return false;
    }
}
