package io.thadow.parkourrun.utils.storage.type.local;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.data.PlayerData;
import io.thadow.parkourrun.managers.PlayerDataManager;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

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

    public static void setup() {
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
        Bukkit.getConsoleSender().sendMessage(Utils.colorize("&aLocal Storage enabled."));
    }

    public static void save() {
        try {
            configuration.save(configurationFile);
        } catch (IOException | NullPointerException exception) {
            exception.printStackTrace();
        }
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
        return get().contains("Players." + uuid + ".Name");
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
        get().set("Players." + uuid + ".Wins", newWins);
        PlayerData playerData = PlayerDataManager.getPlayerDataManager().getPlayerData(player.getName());
        playerData.addWin();
        save();
    }

    public static void addLose(Player player) {
        String uuid = player.getUniqueId().toString();
        Integer currentLoses = getLoses(player);
        Integer newLoses = currentLoses + 1;
        get().set("Players." + uuid + ".Loses", newLoses);
        PlayerData playerData = PlayerDataManager.getPlayerDataManager().getPlayerData(player.getName());
        playerData.addLose();
        save();
    }

    public static ArrayList<PlayerData> getPlayers() {
        ArrayList<PlayerData> players = new ArrayList<>();
        if (get().getConfigurationSection("Players") == null) {
            return players;
        }
        for (String uuid : get().getConfigurationSection("Players").getKeys(false)) {
            String player_name = get().getString("Players." + uuid + ".Name");
            int wins = get().getInt("Players." + uuid + ".Wins");
            int loses = get().getInt("Players." + uuid + ".Loses");
            players.add(new PlayerData(player_name, uuid, wins, loses));
        }
        return players;
    }

    public static void savePlayers() {
        if (PlayerDataManager.getPlayerDataManager().getPlayers() == null)
            return;
        for (PlayerData playerData : PlayerDataManager.getPlayerDataManager().getPlayers()) {
            String uuid = playerData.getUUID();
            String player_name = playerData.getPlayer();
            int wins = playerData.getWins();
            int loses = playerData.getLoses();
            get().set("Players." + uuid + ".Name", player_name);
            get().set("Players." + uuid + ".Wins", wins);
            get().set("Players." + uuid + ".Loses", loses);
        }
        save();
    }
}
