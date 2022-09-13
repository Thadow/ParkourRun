package io.thadow.parkourrun.utils.storage.type;

import io.thadow.parkourrun.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class LocalStorage {
    private static FileConfiguration configuration = null;
    private static File configurationFile = null;

    public static FileConfiguration get() {
        if (configuration == null)
            reload();
        return configuration;
    }

    public static void reload() {
        if (configuration == null)
            configurationFile = new File(Main.getInstance().getDataFolder(), "playersdata.yml");
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
        Reader reader = new InputStreamReader(Main.getInstance().getResource("playersdata.yml"));
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(reader);
        configuration.setDefaults(yamlConfiguration);
    }

    public static void register() {
        File configuration = new File(Main.getInstance().getDataFolder(), "playersdata.yml");
        if (!configuration.exists()) {
            Main.getInstance().saveResource("playersdata.yml", false);
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try {
                yamlConfiguration.load(configuration);
            } catch (InvalidConfigurationException | IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void save() {
        try {
            configuration.save(configurationFile);
        } catch (IOException | NullPointerException exception) {
            exception.printStackTrace();
        }
        reload();
    }

    public static void createPlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        get().set("Players." + uuid + ".Name", player.getName());
        get().set("Players." + uuid + ".Wins", String.valueOf(0));
        get().set("Players." + uuid + ".Loses", String.valueOf(0));
        save();
    }

    public static void removePlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        get().set("Players." + uuid + ".Name", null);
        get().set("Players." + uuid + ".Wins", null);
        get().set("Players." + uuid + ".Loses", null);
        get().set("Players." + uuid, null);
        save();
    }

    public static boolean containsPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        return get().contains("Players" + uuid);
    }

    public static Integer getWins(Player player) {
        if (containsPlayer(player)) {
            String uuid = player.getUniqueId().toString();
            return get().getInt("Players." + uuid + ".Wins");
        }
        return 0;
    }

    public static Integer getLoses(Player player) {
        if (containsPlayer(player)) {
            String uuid = player.getUniqueId().toString();
            return get().getInt("Players." + uuid + ".Loses");
        }
        return 0;
    }

    public static void addWin(Player player) {
        String uuid = player.getUniqueId().toString();
        Integer currentWins = getWins(player);
        Integer newWins = currentWins + 1;
        get().set("Players." + uuid + ".Wins", String.valueOf(newWins));
        save();
    }

    public static void addLose(Player player) {
        String uuid = player.getUniqueId().toString();
        Integer currentLoses = getLoses(player);
        Integer newLoses = currentLoses + 1;
        get().set("Players." + uuid + ".Loses", String.valueOf(newLoses));
        save();
    }
}
