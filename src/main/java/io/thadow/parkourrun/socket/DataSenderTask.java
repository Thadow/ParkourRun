package io.thadow.parkourrun.socket;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DataSenderTask {

    public static void start() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> new BukkitRunnable() {
            @Override
            public void run() {
                for (Arena arena : ArenaManager.getArenaManager().getArenas()) {
                    if (arena.getArenaStatus() == ArenaStatus.DISABLED) {
                        String removeMessage = Utils.getRemoveMessage();
                        DataSenderSocket.sendMessage(removeMessage);
                    } else {
                        DataSenderSocket.sendMessage(Utils.getUpdateMessage(arena));
                    }
                }
            }
        }.runTaskAsynchronously(Main.getInstance()), 100L, 30L);
    }
}
