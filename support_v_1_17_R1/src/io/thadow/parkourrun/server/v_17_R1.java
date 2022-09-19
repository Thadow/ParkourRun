package io.thadow.parkourrun.server;

import io.thadow.parkourrun.api.server.VersionSupport;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.plugin.Plugin;

public class v_17_R1 extends VersionSupport {

    public v_17_R1(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    @Override
    public void setJoinSignBackgroundBlockData(BlockState b, byte data) {

    }

    @Override
    public void setJoinSignBackground(BlockState b, org.bukkit.Material material) {
        if (b.getBlockData() instanceof WallSign) {
            b.getBlock().getRelative(((WallSign) b.getBlockData()).getFacing().getOppositeFace()).setType(material);
        }
    }
}
