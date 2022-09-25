package io.thadow.parkourrun.utils;

import com.google.gson.JsonObject;
import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class Utils {
    private static final Map<Player, String> selectingCorners = new HashMap<>();
    private static final List<Player> buildingPlayers = new ArrayList<>();
    private static final Map<Player, Integer> arenaSelectorPlayers = new HashMap<>();
    public static final Map<String, String> bungeeArenas = new HashMap<>();
    public static final List<String> arenaData = new ArrayList<>();

    public static void removeBungeeArena(String serverID, String from) {
        if (bungeeArenas.containsKey(serverID)) {
            arenaData.removeIf(string -> string.startsWith(serverID));
            bungeeArenas.remove(serverID);
            Debugger.debug(DebugType.INFO, "&aArena Info removed from: " + from);
        }
    }

    public static void updateBungeeArena(String data, String from) {
        String serverID = data.split("/-/")[0];
        if (bungeeArenas.containsKey(serverID)) {
            if (bungeeArenas.get(serverID).equals(data)) {
                return;
            }
        } else {
            bungeeArenas.put(serverID, data);
            arenaData.add(data);
            Debugger.debug(DebugType.INFO, "&aArena Info added from: " + from);
            return;
        }
        if (bungeeArenas.containsKey(serverID)) {
            arenaData.removeIf(string -> string.startsWith(serverID));
            bungeeArenas.remove(serverID);
            Debugger.debug(DebugType.INFO, "&aArena Info updated from: " + from);
        } else {
            bungeeArenas.put(serverID, data);
            arenaData.add(data);
            Debugger.debug(DebugType.INFO, "&aArena Info added from: " + from);
        }
    }

    public static String getAvailableBungeeArena() {
        for (String arena : bungeeArenas.values()) {
            String[] split = arena.split("/-/");
            if (split[3].equals("WAITING")) {
                return split[0];
            }
            if (split[3].equals("STARTING")) {
                return split[0];
            }
        }
        return "NO ARENAS AVAILABLE";
    }

    public static boolean isBungeeArena(String server) {
            return bungeeArenas.containsKey(server);
    }

    public static boolean sendPlayerTo(Player player, String server) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(Main.getInstance(), "BungeeCord", baos.toByteArray());
            baos.close();
            out.close();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String getRemoveMessage() {
        String serverID = Main.getInstance().getConfiguration().getString("Configuration.BungeeCord.Server ID");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Type", "REMOVE");
        jsonObject.addProperty("serverID", serverID);
        return jsonObject.toString();
    }

    public static String getUpdateMessage(Arena arena) {
        if (arena == null) {
            return "";
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Type", "UPDATE");
        String serverID = Main.getInstance().getConfiguration().getString("Configuration.BungeeCord.Server ID");
        jsonObject.addProperty("serverID", serverID);
        jsonObject.addProperty("arenaID", arena.getArenaID());
        jsonObject.addProperty("arenaName", arena.getArenaDisplayName());
        jsonObject.addProperty("status", arena.getArenaStatus().toString());
        jsonObject.addProperty("currentPlayers", arena.getPlayers().size());
        jsonObject.addProperty("maxPlayers", arena.getMaxPlayers());
        return jsonObject.toString();
    }

    public static List<Player> getBuildingPlayers() {
        return buildingPlayers;
    }

    public static boolean isInBuildingPlayers(Player player) {
        return buildingPlayers.contains(player);
    }

    public static boolean isSelectingCorners(Player player) {
        return selectingCorners.containsKey(player);
    }

    public static boolean isInArenaSelectorPlayers(Player player) {
        return arenaSelectorPlayers.containsKey(player);
    }

    public static Map<Player, Integer> getArenaSelectorPlayers() {
        return arenaSelectorPlayers;
    }

    public static Map<Player, String> getSelectingCorners() {
        return selectingCorners;
    }


    public static String colorize(String message) {
     return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> colorize(List<String> list) {
        List<String> colorizedList = new ArrayList<>();
        for (String line : list) {
            line = colorize(line);
            colorizedList.add(line);
        }
        return colorizedList;
    }

    public static String format(String message) {
        message = replace(message,"%prefix%", Main.getMessagesConfiguration().getString("Prefix"));
        message = colorize(message);
        return message;
    }

    public static void playSound(Player player, String path) {
        String[] split = path.split(";");
        try {
            Sound sound = Sound.valueOf(split[0]);
            int volumen = Integer.parseInt(split[1]);
            float pitch = Float.parseFloat(split[2]);
            player.playSound(player.getLocation(), sound, volumen, pitch);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getFormattedTime(int time) {
        int minutes = time / 60;
        int seconds = time - (minutes * 60);
        String seconds_string;
        String minutes_string;
        if (seconds >= 0 && seconds <= 9) {
            seconds_string = "0" + seconds;
        } else {
            seconds_string = String.valueOf(seconds);
        }
        if (minutes >= 0 && minutes <= 9) {
            minutes_string = "0" + minutes;
        } else {
            minutes_string = String.valueOf(minutes);
        }

        return minutes_string + ":" + seconds_string;
    }

    public static List<Arena> getSorted(List<Arena> arenas) {
        List<Arena> sorted = new ArrayList<>(arenas);
        sorted.sort(new Comparator<>() {
            public int compare(Arena arena_1, Arena arena_2) {
                if (arena_1.getArenaStatus() == ArenaStatus.STARTING && arena_2.getArenaStatus() == ArenaStatus.STARTING) {
                    return Integer.compare(arena_2.getPlayers().size(), arena_1.getPlayers().size());
                } else if (arena_1.getArenaStatus() == ArenaStatus.STARTING && arena_2.getArenaStatus() != ArenaStatus.STARTING) {
                    return -1;
                } else if (arena_2.getArenaStatus() == ArenaStatus.STARTING && arena_1.getArenaStatus() != ArenaStatus.STARTING) {
                    return 1;
                } else if (arena_1.getArenaStatus() == ArenaStatus.WAITING && arena_2.getArenaStatus() == ArenaStatus.WAITING) {
                    return Integer.compare(arena_2.getPlayers().size(), arena_1.getPlayers().size());
                } else if (arena_1.getArenaStatus() == ArenaStatus.WAITING && arena_2.getArenaStatus() != ArenaStatus.WAITING) {
                    return -1;
                } else if (arena_2.getArenaStatus() == ArenaStatus.WAITING && arena_1.getArenaStatus() == ArenaStatus.WAITING) {
                    return 1;
                } else if (arena_1.getArenaStatus() == ArenaStatus.PLAYING && arena_2.getArenaStatus() == ArenaStatus.PLAYING) {
                    return 0;
                } else if (arena_1.getArenaStatus() == ArenaStatus.PLAYING && arena_2.getArenaStatus() != ArenaStatus.PLAYING) {
                    return -1;
                } else return 1;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Arena;
            }
        });
        return sorted;
    }

    public static boolean isNearBlock(Player p, int radius, String name) {
        for (Block b : getNearbyBlocks(p.getLocation(), radius)) {
            if (b.getType() == Material.valueOf(name)) {
                return true;
            }
        }
        return false;
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        final List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static Location getLobbyLocation() {
        String world = Main.getInstance().getConfiguration().getString("Configuration.Lobby.Location.World");
        double x = Double.parseDouble(Main.getInstance().getConfiguration().getString("Configuration.Lobby.Location.X"));
        double y = Double.parseDouble(Main.getInstance().getConfiguration().getString("Configuration.Lobby.Location.Y"));
        double z = Double.parseDouble(Main.getInstance().getConfiguration().getString("Configuration.Lobby.Location.Z"));
        float yaw = Float.parseFloat(Main.getInstance().getConfiguration().getString("Configuration.Lobby.Location.Yaw"));
        float pitch = Float.parseFloat(Main.getInstance().getConfiguration().getString("Configuration.Lobby.Location.Pitch"));
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public static String replace(String message, String from, String to) {
        return message.replace(from, to);
    }
}
