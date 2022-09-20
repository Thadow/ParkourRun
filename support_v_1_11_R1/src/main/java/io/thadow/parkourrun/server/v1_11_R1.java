package io.thadow.parkourrun.server;

import io.thadow.parkourrun.api.server.VersionSupport;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

public class v1_11_R1 extends VersionSupport {

    public v1_11_R1(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setJoinSignBackgroundBlockData(BlockState block, byte data) {
        block.getBlock().getRelative(((org.bukkit.material.Sign) block.getData()).getAttachedFace()).setData(data, true);
    }

    @Override
    public void setJoinSignBackground(BlockState b, org.bukkit.Material material) {
        b.getLocation().getBlock().getRelative(((org.bukkit.material.Sign) b.getData()).getAttachedFace()).setType(material);
    }
}
