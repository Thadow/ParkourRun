package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.managers.CheckpointManager;
import io.thadow.parkourrun.utils.Utils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class MessagesConfiguration {
    private static FileConfiguration configuration = null;
    private static File configurationFile = null;

    public static FileConfiguration getConfiguration() {
        if (configuration == null)
            reloadConfiguration();
        return configuration;
    }

    public static void reloadConfiguration() {
        if (configuration == null)
            configurationFile = new File(Main.getInstance().getDataFolder(), "messages.yml");
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
        Reader reader = new InputStreamReader(Main.getInstance().getResource("messages.yml"));
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(reader);
        configuration.setDefaults(yamlConfiguration);
    }

    public static void registerConfiguration() {
        File configuration = new File(Main.getInstance().getDataFolder(), "messages.yml");
        if (!configuration.exists()) {
            Main.getInstance().saveResource("messages.yml", false);
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

    public static String getPrefix() {
        return getConfiguration().getString("Prefix");
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

    public static void sendInfoMessage(Player player, Arena arena, String arenaID) {
        List<String> messageList = MessagesConfiguration.getListPath("Messages.Arena.Parameter Changed.Show Info.Message");
        String arenaName = arena.getArenaDisplayName();
        boolean enabled = arena.isEnabled();
        int waitTime = arena.getTime();
        int waitToReEnableTime = arena.getReEnableCount();
        int minPlayers = arena.getMinPlayers();
        int maxPlayers = arena.getMaxPlayers();
        int maxTime = arena.getMaxTime();
        int totalCheckpoints = CheckpointManager.getCheckpointManager().getTotalCheckpoints(arena);
        for (String line : messageList) {
            line = Utils.replace(line, "%enabled%", String.valueOf(enabled));
            line = Utils.replace(line, "%arenaID%", arenaID);
            line = Utils.replace(line, "%arenaName%", arenaName);
            line = Utils.replace(line, "%waitTime%", String.valueOf(waitTime));
            line = Utils.replace(line, "%reEnableTime%", String.valueOf(waitToReEnableTime));
            line = Utils.replace(line, "%minPlayers%", String.valueOf(minPlayers));
            line = Utils.replace(line, "%maxPlayers%", String.valueOf(maxPlayers));
            line = Utils.replace(line, "%maxTime%", String.valueOf(maxTime));
            line = Utils.replace(line, "%totalCheckpoints%", String.valueOf(totalCheckpoints));
            line = Utils.colorize(line);
            player.sendMessage(line);
        }
    }
}
