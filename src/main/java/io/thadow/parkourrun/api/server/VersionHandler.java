package io.thadow.parkourrun.api.server;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class VersionHandler {

    private static String name2;

    private Plugin plugin;

    public VersionHandler(Plugin plugin, String versionName) {
        name2 = versionName;
        this.plugin = plugin;
    }

    public abstract void setBlockData(BlockState b, byte data);

    public abstract void setBackground(BlockState b, Material material);

    public abstract ItemStack addData(ItemStack itemStack, String data);

    public abstract ItemStack setTag(ItemStack itemStack, String key, String value);

    public abstract String getTag(ItemStack itemStack, String key);

    public abstract boolean isCustomItem(ItemStack itemStack);

    public abstract String getData(ItemStack itemStack);

    public abstract ItemStack createItemStack(String material, int amount, short data);
}
