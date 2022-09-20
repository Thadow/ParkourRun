package io.thadow.parkourrun.listeners;

import io.thadow.parkourrun.api.event.ArenaChangeStatusEvent;
import io.thadow.parkourrun.api.event.ArenaDisableEvent;
import io.thadow.parkourrun.api.event.ArenaEnableEvent;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.managers.SignsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaEventsListener implements Listener {

    @EventHandler
    public void onArenaChangeStatusEvent(ArenaChangeStatusEvent event) {
        Arena arena = event.getArena();
        SignsManager.updateBlock(arena);
    }

    @EventHandler
    public void onArenaEnableEvent(ArenaEnableEvent event) {
        Arena arena = event.getArena();
        SignsManager.updateBlock(arena);
    }

    @EventHandler
    public void onArenaDisableEvent(ArenaDisableEvent event) {
        Arena arena = event.getArena();
        SignsManager.updateBlock(arena);
    }
}
