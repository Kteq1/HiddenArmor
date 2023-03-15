package me.kteq.hiddenarmor.manager;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorPacketHandler;
import me.kteq.hiddenarmor.handler.MessageHandler;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class HiddenArmorManager {
    private final HiddenArmor plugin;

    private File enabledPlayersFile = null;
    private FileConfiguration enabledPlayersConfig;

    private Set<OfflinePlayer> enabledPlayers = new HashSet<>();
    private final Set<Predicate<Player>> forceDisablePredicates = new HashSet<>();
    private final Set<Predicate<Player>> forceEnablePredicates = new HashSet<>();


    public HiddenArmorManager(HiddenArmor plugin) {
        this.plugin = plugin;
        registerDefaultPredicates();
        loadEnabledPlayers();
    }

    public void togglePlayer(Player player, boolean inform) {
        if (isEnabled(player)) {
            disablePlayer(player, inform);
        } else {
            enablePlayer(player, inform);
        }
    }

    public void enablePlayer(Player player, boolean inform) {
        if (isEnabled(player)) return;
        if (inform) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("visibility", "%visibility-hidden%");
            MessageHandler.getInstance().message(ChatMessageType.ACTION_BAR, player, "%armor-visibility%", false, placeholderMap);
        }

        this.enabledPlayers.add(player);
        ArmorPacketHandler.getInstance().updatePlayer(player);
    }

    public void disablePlayer(Player player, boolean inform) {
        if (!isEnabled(player)) return;
        if (inform) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("visibility", "%visibility-shown%");
            MessageHandler.getInstance().message(ChatMessageType.ACTION_BAR, player, "%armor-visibility%", false, placeholderMap);
        }

        enabledPlayers.remove(player);
        ArmorPacketHandler.getInstance().updatePlayer(player);
    }

    public boolean isEnabled(Player player) {
        return this.enabledPlayers.contains(player);
    }

    public boolean isArmorHidden(Player player) {
        boolean hidden = isEnabled(player);
        for (Predicate<Player> predicate : forceDisablePredicates) {
            if (predicate.test(player)) {
                hidden = false;
                break;
            }
        }
        for (Predicate<Player> predicate : forceEnablePredicates) {
            if (predicate.test(player)) {
                hidden = true;
                break;
            }
        }
        return hidden;
    }

    private void registerDefaultPredicates() {
        boolean hideWhenInvisible = plugin.getConfig().getBoolean("invisibility-potion.always-hide-gear");
        forceDisablePredicates.add(player -> player.getGameMode().equals(GameMode.CREATIVE));
        forceDisablePredicates.add(player -> player.isInvisible() && !hideWhenInvisible);

        forceEnablePredicates.add(player -> player.isInvisible() && hideWhenInvisible);
    }

    public void saveCurrentEnabledPlayers() {
        List<String> enabledUUIDs = this.enabledPlayers.stream().map(player -> player.getUniqueId().toString()).toList();

        enabledPlayersConfig.set("enabled-players", enabledUUIDs);
        try {
            enabledPlayersConfig.save(enabledPlayersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not save enabled players to " + enabledPlayersFile, e);
        }
    }

    private void loadEnabledPlayers() {
        loadEnabledPlayersConfig();
        this.enabledPlayers = enabledPlayersConfig.getStringList("enabled-players").stream().map(uuidPlayer -> this.plugin.getServer().getOfflinePlayer(UUID.fromString(uuidPlayer))).collect(Collectors.toSet());
    }

    private void loadEnabledPlayersConfig() {
        enabledPlayersFile = new File(plugin.getDataFolder(), "enabled-players.yml");
        if (!enabledPlayersFile.exists()) {
            enabledPlayersFile.getParentFile().mkdirs();
            plugin.saveResource("enabled-players.yml", false);
        }

        enabledPlayersConfig = YamlConfiguration.loadConfiguration(enabledPlayersFile);
    }

}
