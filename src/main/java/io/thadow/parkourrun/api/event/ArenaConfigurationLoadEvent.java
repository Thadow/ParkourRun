package io.thadow.parkourrun.api.event;

import io.thadow.parkourrun.arena.Arena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaConfigurationLoadEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private Arena arena;
    private FileConfiguration configuration;

    public ArenaConfigurationLoadEvent(Arena arena, FileConfiguration configuration) {
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
