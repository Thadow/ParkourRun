package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;

public class SignsManager {
    private static SignsManager signsManager = new SignsManager();
    private static List<Block> signs = new ArrayList<>();

    public static SignsManager getSingsManager() {
        return signsManager;
    }

    public void addSign(Location location) {
        if (location == null)
            return;
        if (location.getBlock().getType().toString().endsWith("_SIGN") || location.getBlock().getType().toString().endsWith("_WALL_SIGN")) {
            signs.add(location.getBlock());
            for (Arena arena : ArenaManager.getArenaManager().getArenas()) {
                refreshSigns(arena);
                updateBlock(arena);
            }
        }
    }

    public static List<Block> getSigns() {
        return signs;
    }

    public synchronized void refreshSigns(Arena arena) {
        for (Block b : signs) {
            if (b == null) continue;
            if (!(b.getType().toString().endsWith("_SIGN") || b.getType().toString().endsWith("_WALL_SIGN"))) continue;
            if (!(b.getState() instanceof Sign)) continue;
            Sign s = (Sign) b.getState();
            if (s == null) return;
            int line = 0;
            for (String string : Main.getSignsConfiguration().getStringList("Format")) {
                if (string == null) continue;
                if (arena.getPlayers() == null) continue;
                s.setLine(line, Utils.colorize(string.replace("[current]", String.valueOf(arena.getPlayers().size()))
                        .replace("[max]", String.valueOf(arena.getMaxPlayers())).replace("[arena]", arena.getArenaDisplayName())
                        .replace("[status]", arena.getArenaStatus().toString())));
                line++;
            }
            try {
                s.update(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateBlock(Arena arena) {
        if (arena == null) return;
        for (Block s : signs) {
            if (!(s.getState() instanceof Sign)) continue;
            String path = "", data = "";
            switch (arena.getArenaStatus()) {
                case WAITING:
                    path = "Status.Waiting.Material";
                    data = "Status.Waiting.Data";
                    break;
                case STARTING:
                    path = "Status.Starting.Material";
                    data = "Status.Starting.Data";
                    break;
                case PLAYING:
                    path = "Status.Playing.Material";
                    data = "Status.Playing.Data";
                    break;
                case ENDING:
                    path = "Status.Ending.Material";
                    data = "Status.Ending.Data";
                    break;
            }
            Main.nms.setJoinSignBackground(s.getState(), Material.valueOf(Main.getSignsConfiguration().getString(path)));
            Main.nms.setJoinSignBackgroundBlockData(s.getState(), (byte) Main.getSignsConfiguration().getInt(data));
        }
    }
}
