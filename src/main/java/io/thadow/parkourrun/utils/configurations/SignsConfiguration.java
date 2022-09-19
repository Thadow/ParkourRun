package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.managers.ConfigurationManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;

public class SignsConfiguration extends ConfigurationManager {
    public static SignsConfiguration signsConfiguration;

    public SignsConfiguration() {
        super("signs", Main.getInstance().getDataFolder().getPath());
    }

    public static void init() {
        signsConfiguration = new SignsConfiguration();
        signsConfiguration.getConfiguration().options().copyDefaults(true);
        signsConfiguration.getConfiguration().addDefault("Format", Arrays.asList("&a[arena]", "", "&2[current]&9/&2[max]", "[status]"));
        signsConfiguration.getConfiguration().addDefault("Status.Waiting.Material", "STAINED_CLAY");
        signsConfiguration.getConfiguration().addDefault("Status.Waiting.Data", 5);
        signsConfiguration.getConfiguration().addDefault("Status.Starting.Material", "STAINED_CLAY");
        signsConfiguration.getConfiguration().addDefault("Status.Starting.Data", 14);
        signsConfiguration.getConfiguration().addDefault("Status.Playing.Material", "STAINED_CLAY");
        signsConfiguration.getConfiguration().addDefault("Status.Playing.Data", 4);
        signsConfiguration.getConfiguration().addDefault("Status.Ending.Material", "STAINED_CLAY");
        signsConfiguration.getConfiguration().addDefault("Status.Ending.Data", 2);
        signsConfiguration.save();
    }
}
