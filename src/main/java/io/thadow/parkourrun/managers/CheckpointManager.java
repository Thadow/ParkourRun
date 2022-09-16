package io.thadow.parkourrun.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import io.thadow.parkourrun.arena.Arena;

public class CheckpointManager {
    private static final CheckpointManager checkpointManager;

    public static CheckpointManager getCheckpointManager() {
        return CheckpointManager.checkpointManager;
    }

    public String getCheckpointCorners(final Arena arena, final Integer id) {
        if (arena == null) {
            return null;
        }
        return arena.getCheckpointCorners(id);
    }

    public Integer getTotalCheckpoints(final Arena arena) {
        if (arena == null) {
            return 0;
        }
        return arena.getCheckpoints().size();
    }

    public Integer getPlayerCurrentCheckpoint(final Player player) {
        if (ArenaManager.getArenaManager().getArena(player) == null) {
            return 0;
        }
        final Arena arena = ArenaManager.getArenaManager().getArena(player);
        return arena.getPlayerCurrentCheckpoint(player);
    }

    public Integer getPlayerNextCheckpoint(final Player player) {
        if (ArenaManager.getArenaManager().getArena(player) == null) {
            return 0;
        }
        final Arena arena = ArenaManager.getArenaManager().getArena(player);
        return arena.getNextPlayerCheckpoint(player);
    }

    public void setPlayerCurrentCheckpoint(final Arena arena, final Player player, final Integer id) {
        if (arena == null) {
            return;
        }
        arena.setCurrentPlayerCheckpoint(player, id);
    }

    public void setPlayerNextCheckpoint(final Arena arena, final Player player, final Integer id) {
        if (arena == null) {
            return;
        }
        arena.setNextPlayerCheckpoint(player, id);
    }

    public Location getCheckpointLocation(final Arena arena, final Integer id) {
        if (arena == null) {
            return null;
        }
        final String[] checkpointSplit = arena.getCheckpoints().get(id).split("/-/");
        final String[] location = checkpointSplit[0].split(";");
        final double checkpointX = Double.parseDouble(location[1]);
        final double checkpointY = Double.parseDouble(location[2]);
        final double checkpointZ = Double.parseDouble(location[3]);
        final float checkpointPitch = Float.parseFloat(location[4]);
        final float checkpointYaw = Float.parseFloat(location[5]);
        return new Location(Bukkit.getWorld(location[0]), checkpointX, checkpointY, checkpointZ, checkpointYaw, checkpointPitch);
    }

    static {
        checkpointManager = new CheckpointManager();
    }
}