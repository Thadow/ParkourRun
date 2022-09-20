package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.SignsConfiguration;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;

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
                case DISABLED:
                    path = "Status.Disabled.Material";
                    data = "Status.Disabled.Data";
                    break;
            }
            Main.nms.setJoinSignBackground(sign2.getState(), Material.valueOf(Main.getSignsConfiguration().getString(path)));
            Main.nms.setJoinSignBackgroundBlockData(sign2.getState(), (byte) Main.getSignsConfiguration().getInt(data));
            arena.refreshSigns(arena);
        }
    }
}
