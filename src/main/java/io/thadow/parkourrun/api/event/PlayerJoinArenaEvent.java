package io.thadow.parkourrun.api.event;

import io.thadow.parkourrun.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinArenaEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private Arena arena;
    private Player player;

    public PlayerJoinArenaEvent(Arena arena, Player player) {
        this.arena = arena;
        this.player = player;
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
