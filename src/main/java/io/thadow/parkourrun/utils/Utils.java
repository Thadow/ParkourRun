package io.thadow.parkourrun.utils;

import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.utils.configurations.MessagesConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Utils {
    public static String colorize(String message) {
     return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String format(String message) {
        message = replace(message,"%prefix%", MessagesConfiguration.getPrefix());
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
        sorted.sort(new Comparator<Arena>() {
            public int compare(Arena arena_1, Arena arena_2) {
                if (arena_1.getArenaStatus() == ArenaStatus.STARTING  && arena_2.getArenaStatus() == ArenaStatus.STARTING) {
                    return Integer.compare(arena_2.getPlayers().size(), arena_1.getPlayers().size());
                } else if (arena_1.getArenaStatus() == ArenaStatus.STARTING  && arena_2.getArenaStatus() != ArenaStatus.STARTING) {
                    return -1;
                } else if (arena_2.getArenaStatus() == ArenaStatus.STARTING  && arena_1.getArenaStatus() != ArenaStatus.STARTING) {
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


    public static String replace(String message, String from, String to) {
        return message.replace(from, to);
    }
}
