package io.thadow.parkourrun.api.server;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

public abstract class VersionSupport {

    private static String name2;

    private Plugin plugin;

    public VersionSupport(Plugin plugin, String versionName) {
        name2 = versionName;
        this.plugin = plugin;
    }

    public abstract void setJoinSignBackgroundBlockData(BlockState b, byte data);

    public abstract void setJoinSignBackground(BlockState b, Material material);
}
