package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigurationManager {

    private YamlConfiguration configuration;
    private File file;
    private String configName;
    private boolean firstTime = false;

    public ConfigurationManager(String configName, String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Debugger.debug(DebugType.ALERT, "&cCould not create: " + file.getPath());
                return;
            }
        }

        this.file = new File(dir, configName + ".yml");
        if (!this.file.exists()) {
            firstTime = true;
            Debugger.debug(DebugType.INFO, "&aCreating: " + this.file.getPath());
            try {
                if (!this.file.createNewFile())  {
                    Debugger.debug(DebugType.ALERT, "&cCould not create: " + this.file.getPath());
                    return;
                }
            } catch (IOException exception) {
                Debugger.debug(DebugType.ALERT, "&cError while creating: " + this.file.getPath());
                Debugger.debug(DebugType.ALERT, "&cError cause: " + exception.getCause());
            }
        }

        Debugger.debug(DebugType.INFO, "&aLoading: " + this.file.getPath());
        configuration = YamlConfiguration.loadConfiguration(this.file);
        this.configName = configName;
    }

    public void reload() {
        Debugger.debug(DebugType.INFO, "&aReloading: " + this.file.getPath());
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void set(String path, Object value) {
        configuration.set(path, value);
        save();
    }

    public void save() {
        try {
            Debugger.debug(DebugType.INFO, "&aSaving: " + this.file.getPath());
            configuration.save(file);
        } catch (IOException exception) {
            Debugger.debug(DebugType.ALERT, "&cError while saving: " + file.getPath());
            Debugger.debug(DebugType.ALERT, "&cError cause: " + exception.getCause());
        }
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public List<String> getStringList(String path) {
        return configuration.getStringList(path);
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    public Integer getInt(String path) {
        return configuration.getInt(path);
    }

    public String getString(String path) {
        return configuration.getString(path);
    }


    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public boolean isFirstTime() {
        return firstTime;
    }
}
