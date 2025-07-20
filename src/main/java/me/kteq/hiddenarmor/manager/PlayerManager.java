package me.kteq.hiddenarmor.manager;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorUpdateHandler;
import me.kteq.hiddenarmor.handler.MessageHandler;
import me.kteq.hiddenarmor.util.ConfigHolder;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class PlayerManager implements ConfigHolder {
    private final HiddenArmor plugin;
    private final ArmorUpdateHandler armorUpdater;
    private final MessageHandler messageHandler;


    private File enabledPlayersFile = null;
    private FileConfiguration enabledPlayersConfig;

    private boolean invisibleAlwaysHideGear;

    private Set<UUID> enabledPlayersUUID = new HashSet<>();
    private final Set<Predicate<Player>> forceDisablePredicates = new HashSet<>();
    private final Set<Predicate<Player>> forceEnablePredicates = new HashSet<>();


    public PlayerManager(HiddenArmor plugin) {
        this.plugin = plugin;
        plugin.addConfigHolder(this);
        this.armorUpdater = plugin.getArmorUpdater();
        this.messageHandler = plugin.getMessageHandler();
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
            messageHandler.message(ChatMessageType.ACTION_BAR, player, "%armor-visibility%", false, placeholderMap);
        }

        this.enabledPlayersUUID.add(player.getUniqueId());
        armorUpdater.updatePlayer(player);
    }

    public void disablePlayer(Player player, boolean inform) {
        if (!isEnabled(player)) return;
        if (inform) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("visibility", "%visibility-shown%");
            messageHandler.message(ChatMessageType.ACTION_BAR, player, "%armor-visibility%", false, placeholderMap);
        }

        enabledPlayersUUID.remove(player.getUniqueId());
        armorUpdater.updatePlayer(player);
    }

    public boolean isEnabled(Player player) {
        return this.enabledPlayersUUID.contains(player.getUniqueId());
    }

    public boolean isArmorVisible(Player player) {
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
        return !hidden;
    }

    private void registerDefaultPredicates() {
        forceDisablePredicates.add(player -> player.getGameMode().equals(GameMode.CREATIVE));
        forceDisablePredicates.add(player -> player.isInvisible() && !invisibleAlwaysHideGear);

        forceEnablePredicates.add(player -> player.isInvisible() && invisibleAlwaysHideGear);
    }

    public void saveCurrentEnabledPlayers() {
        List<String> enabledUUIDs = this.enabledPlayersUUID.stream().map(UUID::toString).collect(Collectors.toList());

        enabledPlayersConfig.set("enabled-players", enabledUUIDs);
        try {
            enabledPlayersConfig.save(enabledPlayersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not save enabled players to " + enabledPlayersFile, e);
        }
    }

    private void loadEnabledPlayers() {
        loadEnabledPlayersConfig();
        this.enabledPlayersUUID = enabledPlayersConfig.getStringList("enabled-players").stream().map(UUID::fromString).collect(Collectors.toSet());
    }

    private void loadEnabledPlayersConfig() {
        enabledPlayersFile = new File(plugin.getDataFolder(), "enabled-players.yml");
        if (!enabledPlayersFile.exists()) {
            enabledPlayersFile.getParentFile().mkdirs();
            plugin.saveResource("enabled-players.yml", false);
        }

        enabledPlayersConfig = YamlConfiguration.loadConfiguration(enabledPlayersFile);
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.invisibleAlwaysHideGear = config.getBoolean("invisibility-potion.always-hide-gear");
    }
}
