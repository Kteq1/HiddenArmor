package me.kteq.hiddenarmor.util;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EventUtil {
    public static void register(Listener listener, JavaPlugin plugin){
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
