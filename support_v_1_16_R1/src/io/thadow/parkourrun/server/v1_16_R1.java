package io.thadow.parkourrun.server;

import io.thadow.parkourrun.api.server.VersionSupport;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.plugin.Plugin;

public class v1_16_R1 extends VersionSupport {

    public v1_16_R1(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    @Override
    public void setJoinSignBackgroundBlockData(BlockState blockState, byte b) {

    }

    @Override
    public void setJoinSignBackground(BlockState b, Material material) {
        if (b.getBlockData() instanceof WallSign) {
            b.getBlock().getRelative(((WallSign) b.getBlockData()).getFacing().getOppositeFace()).setType(material);
        }
    }
}
