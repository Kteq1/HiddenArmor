package me.kteq.hiddenarmor.handler;

import me.kteq.hiddenarmor.util.ConfigUtil;
import me.kteq.hiddenarmor.util.StrUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MessageHandler {
    private static MessageHandler instance;

    private Plugin plugin;
    private String defaultLang;
    private String prefix = "";
    private Map<String, FileConfiguration> localeMap;

    public void setup(Plugin plugin, String defaultLang, String prefix) {
        this.plugin = plugin;
        reloadLocales();
        setDefaultLang(defaultLang);
        setPrefix(prefix);
    }

    public static MessageHandler getInstance() {
        if (instance == null) {
            instance = new MessageHandler();
        }
        return instance;
    }

    public void reloadLocales() {
        plugin.saveResource("locale/en_us.yml", false);
        plugin.saveResource("locale/pt_br.yml", false);

        localeMap = new HashMap<>();
        File localeFolder = new File(plugin.getDataFolder().getAbsolutePath() + "/locale");
        for (File file : localeFolder.listFiles()) {
            FileConfiguration localeYaml = ConfigUtil.getYamlConfiguration(file);
            localeMap.put(file.getName().replaceAll(".yml", ""), localeYaml);
        }
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void message(CommandSender sender, String message) {
        message(ChatMessageType.CHAT, sender, message, false);
    }

    public void message(CommandSender sender, String message, boolean prefix) {
        message(ChatMessageType.CHAT, sender, message, prefix);
    }

    public void message(CommandSender sender, String message, boolean prefix, Map<String, String> placeholderMap) {
        message(ChatMessageType.CHAT, sender, message, prefix, placeholderMap);
    }

    public void message(ChatMessageType messageType, CommandSender sender, String message, boolean prefix) {
        message(messageType, sender, message, prefix, new HashMap<>());
    }

    public void message(ChatMessageType messageType, CommandSender sender, String message, boolean prefix, Map<String, String> placeholderMap) {
        message = replaceHoldersFromConfig(sender, message);

        for (String placeholder : placeholderMap.keySet()) {
            String value = placeholderMap.get(placeholder);
            value = replaceHoldersFromConfig(sender, value);

            message = message.replaceAll("%" + placeholder + "%", value);
        }

        if (prefix) {
            message = this.prefix + message;
        }

        if (sender instanceof Player player) {
            player.spigot().sendMessage(messageType, new TextComponent(StrUtil.color(message)));
        } else if (sender instanceof ConsoleCommandSender && messageType.equals(ChatMessageType.CHAT)) {
            sender.sendMessage(StrUtil.color(message));
        }
    }

    private String replaceHoldersFromConfig(CommandSender sender, String message) {
        for (String string : message.split("%")) {
            if (string.contains(" ")) continue;
            String localizedMessage = getLocalizedMessage(sender, string);

            if (localizedMessage != null) {
                message = message.replaceAll("%" + string + "%", localizedMessage);
            }
        }

        return message;
    }

    private String getLocalizedMessage(CommandSender sender, String messageKey) {
        String locale;
        if (sender instanceof Player player) {
            locale = player.getLocale();
        } else {
            locale = "en_us";
        }
        FileConfiguration localeYaml = localeMap.get(locale);
        if (localeYaml == null) {
            localeYaml = localeMap.get(defaultLang);
            if (localeYaml == null) {
                localeYaml = getDefaultResourceLocale();
            }
        }

        return localeYaml.getString(messageKey, getDefaultResourceLocale().getString(messageKey));
    }

    private FileConfiguration getDefaultResourceLocale() {
        InputStream inputStream = plugin.getResource("locale/en_us.yml");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return YamlConfiguration.loadConfiguration(inputStreamReader);
    }
}
