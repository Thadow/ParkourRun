package io.thadow.parkourrun;

import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.ArenaListener;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.commands.LeaveCommand;
import io.thadow.parkourrun.commands.ParkourRunCommand;
import io.thadow.parkourrun.utils.configurations.ArenasConfiguration;
import io.thadow.parkourrun.utils.configurations.MessagesConfiguration;
import io.thadow.parkourrun.utils.configurations.ScoreboardConfiguration;
import io.thadow.parkourrun.utils.lib.scoreboard.Scoreboard;
import io.thadow.parkourrun.utils.storage.Storage;
import io.thadow.parkourrun.utils.storage.StorageType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        saveDefaultConfig();
        ArenasConfiguration.registerConfiguration();
        MessagesConfiguration.registerConfiguration();
        ScoreboardConfiguration.registerConfiguration();
        String storageType = getConfig().getString("Configuration.StorageType");
        Storage.getStorage().setupStorage(StorageType.valueOf(storageType));
        ArenaManager.getArenaManager().loadArenas();
        getCommand("parkourrun").setExecutor(new ParkourRunCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
        registerListeners(new ArenaListener());
        Scoreboard.run();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Arena arena : ArenaManager.getArenaManager().getArenas()) {
            if (arena.getArenaStatus() == ArenaStatus.PLAYING) {
                arena.finalizeArena(true);
            } else if (arena.getArenaStatus() == ArenaStatus.ENDING) {
                for (Player players : arena.getPlayers()) {
                    players.teleport(players.getWorld().getSpawnLocation());
                }
            }
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getInstance().getServer().getPluginManager().registerEvents(listener, getInstance()));
    }

    public static FileConfiguration getConfiguration() {
        return Main.getInstance().getConfig();
    }
}
