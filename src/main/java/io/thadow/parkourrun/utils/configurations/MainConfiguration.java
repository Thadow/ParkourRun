package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.managers.ConfigurationManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.Collections;

public class MainConfiguration extends ConfigurationManager {
    public static MainConfiguration mainConfiguration;

    public MainConfiguration() {
        super("configuration", Main.getInstance().getDataFolder().getPath());
    }

    public static void init() {
        mainConfiguration = new MainConfiguration();
        YamlConfiguration configuration = mainConfiguration.getConfiguration();
        configuration.addDefault("Configuration.Debug", false);
        configuration.addDefault("Configuration.StorageType", "LOCAL");
        configuration.addDefault("Configuration.Transform.From", "LOCAL");
        configuration.addDefault("Configuration.Transform.To", "MySQL");
        configuration.addDefault("Configuration.MySQL.Database", "database");
        configuration.addDefault("Configuration.MySQL.Host", "localhost");
        configuration.addDefault("Configuration.MySQL.Port", "3306");
        configuration.addDefault("Configuration.MySQL.Username", "root");
        configuration.addDefault("Configuration.MySQL.Password", "root");
        configuration.addDefault("Configuration.MySQL.SSL", false);
        configuration.addDefault("Configuration.Items.Lobby.Arena Selector.Enabled", true);
        configuration.addDefault("Configuration.Items.Lobby.Arena Selector.Slot", 1);
        configuration.addDefault("Configuration.Items.Lobby.Arena Selector.Item", "EMERALD");
        configuration.addDefault("Configuration.Items.Lobby.Arena Selector.Title", "&2Arena Selector");
        configuration.addDefault("Configuration.Items.Lobby.Arena Selector.Lore", Arrays.asList("&eClick derecho", "", "&aMuestra todas las arenas disponibles"));
        configuration.addDefault("Configuration.Items.Arena.Leave Item.Enabled", true);
        configuration.addDefault("Configuration.Items.Arena.Leave Item.Slot", 9);
        configuration.addDefault("Configuration.Items.Arena.Leave Item.Item", "BED");
        configuration.addDefault("Configuration.Items.Arena.Leave Item.Title", "&cSalir");
        configuration.addDefault("Configuration.Items.Arena.Leave Item.Lore", Arrays.asList("&cClick derecho", "&cpara salir"));
        configuration.addDefault("Configuration.Arena Selector.Menu.Title", "&2Arena Selector");
        configuration.addDefault("Configuration.Arena Selector.Menu.Waiting.Item", "nose");
        configuration.addDefault("Configuration.Arena Selector.Menu.Waiting.Data", "nose");
        configuration.addDefault("Configuration.Arena Selector.Menu.Starting.Item", "nose");
        configuration.addDefault("Configuration.Arena Selector.Menu.Starting.Data", "nose");
        configuration.addDefault("Configuration.Arena Selector.Menu.Playing.Item", "nose");
        configuration.addDefault("Configuration.Arena Selector.Menu.Playing.Data", "nose");
        configuration.addDefault("Configuration.Arena Selector.Menu.Ending.Item", "nose");
        configuration.addDefault("Configuration.Arena Selector.Menu.Ending.Data", "nose");
        configuration.addDefault("Configuration.Arena Selector.Menu.Item Name", "%arenaID%");
        configuration.addDefault("Configuration.Arena Selector.Menu.Item Lore", Arrays.asList("&7> %arenaName%", "", "&7Estado: %status%", "&7Players: &f%current%&7/&f%max%", "", "&eClick para unirte!"));
        configuration.addDefault("Configuration.Arena Selector.Menu.Next Page Item", "ARROW");
        configuration.addDefault("Configuration.Arena Selector.Menu.Next Page Item Name", "&aSiguiente pagina!");
        configuration.addDefault("Configuration.Arena Selector.Menu.Back Page Item", "ARROW");
        configuration.addDefault("Configuration.Arena Selector.Menu.Back Page Item Name", "&aPagina anterior");
        configuration.addDefault("Configuration.Arenas.Per Chat Arena", true);
        configuration.addDefault("Configuration.Arenas.Status.Waiting", "&aESPERANDO");
        configuration.addDefault("Configuration.Arenas.Status.Starting", "&6INICIANDO");
        configuration.addDefault("Configuration.Arenas.Status.Playing", "&cEN JUEGO");
        configuration.addDefault("Configuration.Arenas.Status.Ending", "&cTERMINANDO");
        configuration.addDefault("Configuration.Arenas.Status.Disabled", "&4DESACTIVADA");
        configuration.addDefault("Configuration.Arenas.Checkpoints.Firework.Enabled", true);
        configuration.addDefault("Configuration.Arenas.Fireworks For Winner", true);
        configuration.addDefault("Configuration.Arenas.Configurations.GameMode", "SURVIVAL");
        configuration.addDefault("Configuration.Arenas.Configurations.Disable Flight", true);
        configuration.addDefault("Configuration.Arenas.Configurations.Set Flying", false);
        configuration.addDefault("Configuration.Arenas.Configurations.Clear Inventory", true);
        configuration.addDefault("Configuration.Arenas.Checkpoints.Under Block", "EMERALD_BLOCK");
        configuration.addDefault("Configuration.Arenas.Checkpoints.Plate", "GOLD_PLATE");

        configuration.options().copyDefaults(true);
        mainConfiguration.save();
    }
}
