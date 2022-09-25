package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignsManager {


    public static void updateBlock(Arena arena) {
        if (arena == null)
            return;
        for (Block sign2 : arena.getSigns()) {
            if (!(sign2.getState() instanceof Sign))
                return;
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
                case RESTARTING:
                    path = "Status.Restarting.Material";
                    data = "Status.Restarting.Data";
                    break;
                case DISABLED:
                    path = "Status.Disabled.Material";
                    data = "Status.Disabled.Data";
                    break;
            }
            Main.VERSION_HANDLER.setBackground(sign2.getState(), Material.valueOf(Main.getSignsConfiguration().getString(path)));
            Main.VERSION_HANDLER.setBlockData(sign2.getState(), (byte) Main.getSignsConfiguration().getInt(data));
            arena.refreshSigns(arena);
        }
    }
}
