package io.thadow.parkourrun.api.event;

import io.thadow.parkourrun.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaDisableEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private Arena arena;

    public ArenaDisableEvent(Arena arena) {
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
