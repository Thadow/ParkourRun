package io.thadow.parkourrun.api.event;

import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaChangeStatusEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private Arena arena;
    private ArenaStatus oldStatus, newStatus;

    public ArenaChangeStatusEvent(Arena arena, ArenaStatus oldStatus, ArenaStatus newStatus) {
        this.arena = arena;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Arena getArena() {
        return arena;
    }

    public ArenaStatus getOldStatus() {
        return oldStatus;
    }

    public ArenaStatus getNewStatus() {
        return newStatus;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
