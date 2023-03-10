package me.kteq.hiddenarmor.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigUtil {
    public static FileConfiguration getYamlConfiguration(Plugin plugin, String path) {
        File customConfigFile = new File(plugin.getDataFolder() + path);
        if (!customConfigFile.exists()) {
            if (plugin.getResource(path) == null) {
                return null;
            }

            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource(path, false);
        }

        return YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public static FileConfiguration getYamlConfiguration(File customConfigFile) {
        if (!customConfigFile.exists()) {
                return null;
        }

        return YamlConfiguration.loadConfiguration(customConfigFile);
    }
}
