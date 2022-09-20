package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.managers.ConfigurationManager;
import org.bukkit.configuration.file.YamlConfiguration;

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
        configuration.addDefault("Configuration.Arenas.Per Chat Arena", true);
        configuration.addDefault("Configuration.Arenas.Status.Waiting", "&aESPERANDO");
        configuration.addDefault("Configuration.Arenas.Status.Starting", "&6INICIANDO");
        configuration.addDefault("Configuration.Arenas.Status.Ending", "&cTERMINANDO");
        configuration.addDefault("Configuration.Arenas.Status.Disabled", "&4DESACTIVADA");
        configuration.addDefault("Configuration.Arenas.Checkpoints.Firework.Enabled", true);
        configuration.addDefault("Configuration.Arenas.Checkpoints.Under Block", "EMERALD_BLOCK");
        configuration.addDefault("Configuration.Arenas.Checkpoints.Plate", "GOLD_PLATE");

        configuration.options().copyDefaults(true);
        mainConfiguration.save();
    }
}
