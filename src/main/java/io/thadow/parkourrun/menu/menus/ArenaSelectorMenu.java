package io.thadow.parkourrun.menu.menus;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

public class ArenaSelectorMenu {

    public static void open(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(null, 54, "test");
        List<Arena> arenas = ArenaManager.getArenaManager().getArenas();

        // cada pagina tendra 28 arenas
        int totalPages = getTotalPages();

        int slot = 10;

        // Pagina 1 -> 0
        // Pagina 2 -> 28
        for (int i = 28 *(page-1); i < arenas.size(); i++) {
            Arena arena = arenas.get(i);
            if (!arena.isEnabled() || arena.getArenaStatus() == ArenaStatus.DISABLED) {
                return;
            }
            ItemStack arenaItem = null;
            String material;
            int data;
            String name;
            List<String> lore;
            String status;
            String waiting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Waiting");
            String starting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Starting");
            String playing = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Playing");
            String ending = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Ending");
            String disabled = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Disabled");
            if (arena.getArenaStatus() == ArenaStatus.WAITING) {
                material = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Waiting.Item");
                data = Main.getInstance().getConfiguration().getInt("Configuration.Arena Selector.Menu.Waiting.Data");
                status = waiting;
                arenaItem = Main.VERSION_HANDLER.createItemStack(material, 1, (short) data);
                name = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Item Name");
                name = name.replace("%arenaID%", arena.getArenaID());
                name = name.replace("%arenaName%", arena.getArenaDisplayName());
                lore = Main.getInstance().getConfiguration().getStringList("Configuration.Arena Selector.Menu.Item Lore");
                List<String> newLore = new ArrayList<>();
                for (String line : lore) {
                    line = line.replace("%arenaID%", arena.getArenaID());
                    line = line.replace("%arenaName%", arena.getArenaDisplayName());
                    line = line.replace("%status%", status);
                    line = line.replace("%current%", String.valueOf(arena.getPlayers().size()));
                    line = line.replace("%max%", String.valueOf(arena.getMaxPlayers()));
                    line = Utils.colorize(line);
                    newLore.add(line);
                }
                ItemMeta arenaItemMeta = arenaItem.getItemMeta();
                arenaItemMeta.setDisplayName(Utils.colorize(name));
                arenaItemMeta.setLore(newLore);
                arenaItem.setItemMeta(arenaItemMeta);
                arenaItem = Main.VERSION_HANDLER.addData(arenaItem, "arenaID=" + arena.getArenaID());
            } else if (arena.getArenaStatus() == ArenaStatus.STARTING) {
                material = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Starting.Item");
                data = Main.getInstance().getConfiguration().getInt("Configuration.Arena Selector.Menu.Starting.Data");
                status = starting;
                arenaItem = Main.VERSION_HANDLER.createItemStack(material, 1, (short) data);
                name = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Item Name");
                name = name.replace("%arenaID%", arena.getArenaID());
                name = name.replace("%arenaName%", arena.getArenaDisplayName());
                lore = Main.getInstance().getConfiguration().getStringList("Configuration.Arena Selector.Menu.Item Lore");
                List<String> newLore = new ArrayList<>();
                for (String line : lore) {
                    line = line.replace("%arenaID%", arena.getArenaID());
                    line = line.replace("%arenaName%", arena.getArenaDisplayName());
                    line = line.replace("%status%", status);
                    line = line.replace("%current%", String.valueOf(arena.getPlayers().size()));
                    line = line.replace("%max%", String.valueOf(arena.getMaxPlayers()));
                    line = Utils.colorize(line);
                    newLore.add(line);
                }
                ItemMeta arenaItemMeta = arenaItem.getItemMeta();
                arenaItemMeta.setDisplayName(Utils.colorize(name));
                arenaItemMeta.setLore(newLore);
                arenaItem.setItemMeta(arenaItemMeta);
                arenaItem = Main.VERSION_HANDLER.addData(arenaItem, "arenaID=" + arena.getArenaID());
            } else if (arena.getArenaStatus() == ArenaStatus.PLAYING) {
                material = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Playing.Item");
                data = Main.getInstance().getConfiguration().getInt("Configuration.Arena Selector.Menu.Playing.Data");
                status = playing;
                arenaItem = Main.VERSION_HANDLER.createItemStack(material, 1, (short) data);
                name = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Item Name");
                name = name.replace("%arenaID%", arena.getArenaID());
                name = name.replace("%arenaName%", arena.getArenaDisplayName());
                lore = Main.getInstance().getConfiguration().getStringList("Configuration.Arena Selector.Menu.Item Lore");
                List<String> newLore = new ArrayList<>();
                for (String line : lore) {
                    line = line.replace("%arenaID%", arena.getArenaID());
                    line = line.replace("%arenaName%", arena.getArenaDisplayName());
                    line = line.replace("%status%", status);
                    line = line.replace("%current%", String.valueOf(arena.getPlayers().size()));
                    line = line.replace("%max%", String.valueOf(arena.getMaxPlayers()));
                    line = Utils.colorize(line);
                    newLore.add(line);
                }
                ItemMeta arenaItemMeta = arenaItem.getItemMeta();
                arenaItemMeta.setDisplayName(Utils.colorize(name));
                arenaItemMeta.setLore(newLore);
                arenaItem.setItemMeta(arenaItemMeta);
                arenaItem = Main.VERSION_HANDLER.addData(arenaItem, "arenaID=" + arena.getArenaID());
            } else if (arena.getArenaStatus() == ArenaStatus.ENDING) {
                material = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Ending.Item");
                data = Main.getInstance().getConfiguration().getInt("Configuration.Arena Selector.Menu.Ending.Data");
                status = ending;
                arenaItem = Main.VERSION_HANDLER.createItemStack(material, 1, (short) data);
                name = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Item Name");
                name = name.replace("%arenaID%", arena.getArenaID());
                name = name.replace("%arenaName%", arena.getArenaDisplayName());
                lore = Main.getInstance().getConfiguration().getStringList("Configuration.Arena Selector.Menu.Item Lore");
                List<String> newLore = new ArrayList<>();
                for (String line : lore) {
                    line = line.replace("%arenaID%", arena.getArenaID());
                    line = line.replace("%arenaName%", arena.getArenaDisplayName());
                    line = line.replace("%status%", status);
                    line = line.replace("%current%", String.valueOf(arena.getPlayers().size()));
                    line = line.replace("%max%", String.valueOf(arena.getMaxPlayers()));
                    line = Utils.colorize(line);
                    newLore.add(line);
                }
                ItemMeta arenaItemMeta = arenaItem.getItemMeta();
                arenaItemMeta.setDisplayName(Utils.colorize(name));
                arenaItemMeta.setLore(newLore);
                arenaItem.setItemMeta(arenaItemMeta);
                arenaItem = Main.VERSION_HANDLER.addData(arenaItem, "arenaID=" + arena.getArenaID());
            }

            inventory.setItem(slot, arenaItem);

            slot++;

            if (slot > 27) {
                break;
            }
        }

        if (totalPages > page) {
            String material = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Next Page Item");
            String name = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Next Page Item Name");
            ItemStack changePageItem = Main.VERSION_HANDLER.createItemStack(material, 1, (short) 1);
            ItemMeta changePageItemMeta = changePageItem.getItemMeta();
            changePageItemMeta.setDisplayName(Utils.colorize(name));
            changePageItem.setItemMeta(changePageItemMeta);
            inventory.setItem(54, changePageItem);
        }

        if (page > 1) {
            String material = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Back Page Item");
            String name = Main.getInstance().getConfiguration().getString("Configuration.Arena Selector.Menu.Back Page Item Name");
            ItemStack changePageItem = Main.VERSION_HANDLER.createItemStack(material, 1, (short) 1);
            ItemMeta changePageItemMeta = changePageItem.getItemMeta();
            changePageItemMeta.setDisplayName(Utils.colorize(name));
            changePageItem.setItemMeta(changePageItemMeta);
            inventory.setItem(45, changePageItem);
        }

        player.openInventory(inventory);
        Utils.getArenaSelectorPlayers().put(player, page);
    }

    private static int getTotalPages() {
        if (ArenaManager.getArenaManager().getArenas().size() % 28 == 0) {
            return (ArenaManager.getArenaManager().getArenas().size() / 28);
        } else {
            return (ArenaManager.getArenaManager().getArenas().size() / 28) + 1;
        }
    }
}
