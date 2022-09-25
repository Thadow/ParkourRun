package io.thadow.parkourrun.items;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Items {

    public static void giveLobbyItemsTo(Player player) {
        if (Main.getInstance().getConfiguration().getBoolean("Configuration.Items.Lobby.Arena Selector.Enabled")) {
            String material = Main.getInstance().getConfiguration().getString("Configuration.Items.Lobby.Arena Selector.Item");
            int data = Main.getInstance().getConfiguration().getInt("Configuration.Items.Lobby.Arena Selector.Data");
            String name = Main.getInstance().getConfiguration().getString("Configuration.Items.Lobby.Arena Selector.Name");
            List<String> lore = Main.getInstance().getConfiguration().getStringList("Configuration.Items.Lobby.Arena Selector.Lore");
            int slot = Main.getInstance().getConfiguration().getInt("Configuration.Items.Lobby.Arena Selector.Slot");
            ItemStack itemStack = Main.VERSION_HANDLER.createItemStack(material, 1, (short) data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Utils.colorize(name));
            itemMeta.setLore(Utils.colorize(lore));
            itemStack.setItemMeta(itemMeta);
            itemStack = Main.VERSION_HANDLER.addData(itemStack, "ArenaSelectorItem");

            player.getInventory().setItem((slot + 1), itemStack);
        }

        if (Main.getInstance().getConfiguration().getBoolean("Configuration.Items.Lobby.Leave Item.Enabled")) {
            String material = Main.getInstance().getConfiguration().getString("Configuration.Items.Lobby.Leave Item.Item");
            int data = Main.getInstance().getConfiguration().getInt("Configuration.Items.Lobby.Leave Item.Data");
            int slot = Main.getInstance().getConfiguration().getInt("Configuration.Items.Lobby.Leave Item.Slot");
            String name = Main.getInstance().getConfiguration().getString("Configuration.Items.Lobby.Leave Item.Name");
            List<String> lore = Main.getInstance().getConfiguration().getStringList("Configuration.Items.Lobby.Leave Item.Lore");
            ItemStack itemStack = Main.VERSION_HANDLER.createItemStack(material, 1, (short) data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Utils.colorize(name));
            itemMeta.setLore(Utils.colorize(lore));
            itemStack.setItemMeta(itemMeta);
            itemStack = Main.VERSION_HANDLER.addData(itemStack, "LeaveItemLobby");

            player.getInventory().setItem((slot + 1), itemStack);
        }

        player.updateInventory();
    }

    public static void giveArenaItemsTo(Player player, boolean onlyLeave) {
        if (Main.getInstance().getConfiguration().getBoolean("Configuration.Items.Arena Back Checkpoint Item.Enabled") && !onlyLeave) {
            String material = Main.getInstance().getConfiguration().getString("Configuration.Items.Arena.Back Checkpoint Item.Item");
            int data = Main.getInstance().getConfiguration().getInt("Configuration.Items.Arena.Back Checkpoint.Item.Data");
            int slot = Main.getInstance().getConfiguration().getInt("Configuration.Items.Arena.Back Checkpoint.Item.Slot");
            String name = Main.getInstance().getConfiguration().getString("Configuration.Items.Arena.Back Checkpoint Item.Name");
            List<String> lore = Main.getInstance().getConfiguration().getStringList("Configuration.Items.Arena.Back Checkpoint Item.Lore");
            ItemStack itemStack = Main.VERSION_HANDLER.createItemStack(material, 1, (short) data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Utils.colorize(name));
            itemMeta.setLore(Utils.colorize(lore));
            itemStack.setItemMeta(itemMeta);
            itemStack = Main.VERSION_HANDLER.addData(itemStack, "BackCheckpointItem");

            player.getInventory().setItem((slot + 1), itemStack);
        }

        if (Main.getInstance().getConfiguration().getBoolean("Configuration.Items.Arena.Leave Item.Enabled")) {
            String material = Main.getInstance().getConfiguration().getString("Configuration.Items.Arena.Leave Item.Item");
            int data = Main.getInstance().getConfiguration().getInt("Configuration.Items.Arena.Leave Item.Data");
            int slot = Main.getInstance().getConfiguration().getInt("Configuration.Items.Arena.Leave Item.Slot");
            String name = Main.getInstance().getConfiguration().getString("Configuration.Items.Arena.Leave Item.Name");
            List<String> lore = Main.getInstance().getConfiguration().getStringList("Configuration.Items.Leave Item.Lore");
            ItemStack itemStack = Main.VERSION_HANDLER.createItemStack(material, 1, (short) data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Utils.colorize(name));
            itemMeta.setLore(Utils.colorize(lore));
            itemStack.setItemMeta(itemMeta);
            itemStack = Main.VERSION_HANDLER.addData(itemStack, "LeaveItemArena");

            player.getInventory().setItem((slot + 1), itemStack);
        }

        player.updateInventory();
    }
}
