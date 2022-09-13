package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class ScoreboardConfiguration {
    private static FileConfiguration configuration = null;
    private static File configurationFile = null;

    public static FileConfiguration getConfiguration() {
        if (configuration == null)
            reloadConfiguration();
        return configuration;
    }

    public static void reloadConfiguration() {
        if (configuration == null)
            configurationFile = new File(Main.getInstance().getDataFolder(), "scoreboards.yml");
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
        Reader reader = new InputStreamReader(Main.getInstance().getResource("scoreboards.yml"));
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(reader);
        configuration.setDefaults(yamlConfiguration);
    }

    public static void registerConfiguration() {
        File configuration = new File(Main.getInstance().getDataFolder(), "scoreboards.yml");
        if (!configuration.exists()) {
            Main.getInstance().saveResource("scoreboards.yml", false);
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try {
                yamlConfiguration.load(configuration);
            } catch (InvalidConfigurationException | IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void saveConfiguration() {
        try {
            configuration.save(configurationFile);
        } catch (IOException | NullPointerException exception) {
            exception.printStackTrace();
        }
        reloadConfiguration();
    }

    public static String getPath(String path) {
        return getConfiguration().getString(path);
    }

    public static List<String> getListPath(String path) {
        return getConfiguration().getStringList(path);
    }

    public static boolean getBoolean(String path) {
        return getConfiguration().getBoolean(path);
    }

    public static Integer getInt(String path) {
        return getConfiguration().getInt(path);
    }
}
