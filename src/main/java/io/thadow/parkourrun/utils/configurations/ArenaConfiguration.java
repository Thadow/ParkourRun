package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.managers.ConfigurationManager;
import org.bukkit.configuration.file.YamlConfiguration;

public class ArenaConfiguration extends ConfigurationManager {

    public ArenaConfiguration(String configName, String dir) {
        super(configName, dir);
        if (isFirstTime()) {
            YamlConfiguration configuration = getConfiguration();
            configuration.addDefault("Enabled", false);
            configuration.addDefault("Extensions.Checkpoints.Need All To Win", true);
            configuration.addDefault("Extensions.Damage.Disable Fall Damage", true);
            configuration.addDefault("Extensions.Damage.Disable Player Damage", true);
            configuration.addDefault("Extensions.Damage.Disable Monster Damage", true);
            configuration.addDefault("Extensions.Lose.Add Lose On Disconnect/Leave", true);
            configuration.addDefault("Extensions.Lose.Add Lose On Tie", false);
            configuration.addDefault("Arena Name", configName);
            configuration.addDefault("Wait Time To Start", 15);
            configuration.addDefault("Wait Time To Re-Enable", 5);
            configuration.addDefault("Min Players", 2);
            configuration.addDefault("Max Players", 8);
            configuration.addDefault("Max Time", 120);
            configuration.addDefault("Spawn Location", "world;1;1;1;1.0;1.0");
            configuration.addDefault("Wait Location", "world;1;1;1;1.0;1.0");
            configuration.addDefault("Win Zone Corner 1", "1;1;1");
            configuration.addDefault("Win Zone Corner 2", "1;1;1");
            configuration.addDefault("Arena Zone Corner 1", "1;1;1");
            configuration.addDefault("Arena Zone Corner 2", "1;1;1");
            configuration.addDefault("Total Checkpoints", 0);
            configuration.options().copyDefaults(true);
            save();
        }
    }
}
