package io.thadow.parkourrun.listeners;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.api.event.*;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.managers.SignsManager;
import io.thadow.parkourrun.socket.DataSenderSocket;
import io.thadow.parkourrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaEventsListener implements Listener {

    @EventHandler
    public void onArenaChangeStatusEvent(ArenaChangeStatusEvent event) {
        Arena arena = event.getArena();
        SignsManager.updateBlock(arena);
        String dataMessage = Utils.getUpdateMessage(arena);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> DataSenderSocket.sendMessage(dataMessage));
    }

    @EventHandler
    public void onArenaEnableEvent(ArenaEnableEvent event) {
        Arena arena = event.getArena();
        SignsManager.updateBlock(arena);
        String dataMessage = Utils.getUpdateMessage(arena);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> DataSenderSocket.sendMessage(dataMessage));
    }

    @EventHandler
    public void onArenaDisableEvent(ArenaDisableEvent event) {
        Arena arena = event.getArena();
        SignsManager.updateBlock(arena);
        String dataMessage = Utils.getUpdateMessage(arena);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> DataSenderSocket.sendMessage(dataMessage));
    }

    @EventHandler
    public void onPlayerJoinArenaEvent(PlayerJoinArenaEvent event) {
        Arena arena = event.getArena();
        SignsManager.updateBlock(arena);
        String dataMessage = Utils.getUpdateMessage(arena);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> DataSenderSocket.sendMessage(dataMessage));
    }

    @EventHandler
    public void onPlayerQuitArenaEvent(PlayerQuitArenaEvent event) {
        Arena arena = event.getArena();
        SignsManager.updateBlock(arena);
        String dataMessage = Utils.getUpdateMessage(arena);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> DataSenderSocket.sendMessage(dataMessage));
    }
}
