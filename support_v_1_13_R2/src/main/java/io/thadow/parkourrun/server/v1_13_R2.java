package io.thadow.parkourrun.server;

import io.thadow.parkourrun.api.server.VersionSupport;
import org.bukkit.block.BlockState;
import org.bukkit.material.Sign;
import org.bukkit.plugin.Plugin;

public class v1_13_R2 extends VersionSupport {

    public v1_13_R2(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    @Override
    public void setJoinSignBackgroundBlockData(BlockState blockState, byte b) { }

    @Override
    public void setJoinSignBackground(org.bukkit.block.BlockState b, org.bukkit.Material material) {
        b.getLocation().getBlock().getRelative(((Sign) b.getData()).getAttachedFace()).setType(material);
    }
}