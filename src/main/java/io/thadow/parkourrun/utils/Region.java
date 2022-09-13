package io.thadow.parkourrun.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Region {
    private final Vector corner1;
    private final Vector corner2;

    public Region(Vector corner1, Vector corner2) {
        int x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        this.corner1 = new Vector(x1, y1, z1);
        this.corner2 = new Vector(x2, y2, z2);
    }

    public boolean isInside(Location location) {
        if (location == null) {
            return false;
        }
        return location.getBlockX() >= this.corner1.getBlockX() && location.getBlockX() <= this.corner2.getBlockX() && location.getBlockY() >= this.corner1.getBlockY() && location.getBlockY() <= this.corner2.getBlockY() && location.getBlockZ() >= this.corner1.getBlockZ() && location.getBlockZ() <= this.corner2.getBlockZ();
    }
}
