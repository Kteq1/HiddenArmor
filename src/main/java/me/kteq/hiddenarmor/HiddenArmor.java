package me.kteq.hiddenarmor;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.kteq.hiddenarmor.util.Metrics;
import me.kteq.hiddenarmor.command.HiddenArmorCommand;
import me.kteq.hiddenarmor.command.ToggleArmorCommand;
import me.kteq.hiddenarmor.event.EntityToggleGlideListener;
import me.kteq.hiddenarmor.event.GameModeListener;
import me.kteq.hiddenarmor.event.PotionEffectListener;
import me.kteq.hiddenarmor.event.InventoryShiftClickListener;
import me.kteq.hiddenarmor.packet.ArmorOthersPacketListener;
import me.kteq.hiddenarmor.packet.ArmorSelfPacketListener;
import me.kteq.hiddenarmor.armormanager.ArmorManager;
import me.kteq.hiddenarmor.util.StrUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class HiddenArmor extends JavaPlugin {
    private File enabledPlayersFile = null;
    private FileConfiguration enabledPlayers;

    private String prefix;
    private boolean isOld;

    private boolean ignoreLeatherArmor;
    private boolean ignoreTurtleHelmet;
    private boolean ignoreElytra;
    private boolean hideInvisible;
    private boolean toggleDefault;
    private boolean toggleOtherDefault;
    private ToggleArmorCommand toggleCommand;

    private List<String> hiddenPlayers = new ArrayList<>();
    private List<UUID> ignoredPlayers;

    @Override
    public void onEnable() {
        // Default config file
        this.saveDefaultConfig();
        checkConfig();
        this.saveDefaultEnabledPlayers();
        isOld = Bukkit.getBukkitVersion().startsWith("1.16");

        // Define variables
        ignoredPlayers = new ArrayList<>();
        prefix = StrUtil.color("&c[&fHiddenArmor&c] &f");

        // Load initial config
        loadConfigVars();
        toggleDefault = this.getConfig().getBoolean("default-permissions.toggle");
        toggleOtherDefault = this.getConfig().getBoolean("default-permissions.toggle-other");

        // Get saved enabled players
        hiddenPlayers = enabledPlayers.getStringList("enabled-players");

        // Instantiate managers
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        ArmorManager armorManager = new ArmorManager(this, manager);

        // Enable commands
        toggleCommand = new ToggleArmorCommand(this, armorManager);
        new HiddenArmorCommand(this);
        // Register ProtocolLib packet listeners
        new ArmorSelfPacketListener(this, manager, armorManager);
        new ArmorOthersPacketListener(this, manager);
        // Register event listeners
        new InventoryShiftClickListener(this, armorManager);
        new GameModeListener(this, armorManager);
        new PotionEffectListener(this, armorManager);
        new EntityToggleGlideListener(this, armorManager);

        // Metrics
        Metrics metrics = new Metrics(this, 14419);
    }

    @Override
    public void onDisable() {
        enabledPlayers.set("enabled-players", hiddenPlayers);
        try {
            enabledPlayers.save(enabledPlayersFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save enabled players to " + enabledPlayersFile, e);
        }
    }


    public void loadConfigVars(){
        ignoreLeatherArmor = this.getConfig().getBoolean("ignore.leather-armor");
        ignoreTurtleHelmet = this.getConfig().getBoolean("ignore.turtle-helmet");
        ignoreElytra = this.getConfig().getBoolean("ignore.elytra");
        hideInvisible = this.getConfig().getBoolean("invisibility-potion.always-hide-gear");
    }

    public void reload(){
        reloadConfig();
        loadConfigVars();
    }

    private void saveDefaultEnabledPlayers() {
        enabledPlayersFile = new File(getDataFolder(), "enabled-players.yml");
        if (!enabledPlayersFile.exists()) {
            enabledPlayersFile.getParentFile().mkdirs();
            saveResource("enabled-players.yml", false);
        }

        enabledPlayers = new YamlConfiguration();
        try {
            enabledPlayers.load(enabledPlayersFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void checkConfig(){
        if(getConfig().getInt("config-version") >= getConfig().getDefaults().getInt("config-version")) return;
        getLogger().log(Level.WARNING, "Your HiddenArmor configuration file is outdated!");
        getLogger().log(Level.WARNING, "Please regenerate the 'config.yml' file when possible.");
    }



    public boolean shouldNotHide(Player player){
        return (!hasPlayer(player) && !player.isInvisible() && hideInvisible) ||
                (!hasPlayer(player) && !hideInvisible) ||
                (hasPlayer(player) && player.isInvisible() && !hideInvisible) ||
                (player.getGameMode().equals(GameMode.CREATIVE)) ||
                (isPlayerIgnored(player));
    }

    public String getPrefix() {
        return prefix;
    }

    public void addHiddenPlayer(Player player){
        hiddenPlayers.add(player.getUniqueId().toString());
    }

    public void removeHiddenPlayer(Player player){
        hiddenPlayers.remove(player.getUniqueId().toString());
    }

    public boolean hasPlayer(Player player){
        return hiddenPlayers.contains(player.getUniqueId().toString());
    }

    public void addIgnoredPlayer(Player player){
        ignoredPlayers.add(player.getUniqueId());
    }

    public void removeIgnoredPlayer(Player player){
        ignoredPlayers.remove(player.getUniqueId());
    }

    public boolean isPlayerIgnored(Player player){
        return player.getGameMode().equals(GameMode.CREATIVE) || ignoredPlayers.contains(player.getUniqueId());
    }

    public boolean isToggleDefault() {
        return toggleDefault;
    }

    public boolean isToggleOtherDefault() {
        return toggleOtherDefault;
    }

    public boolean isIgnoreLeatherArmor() {
        return ignoreLeatherArmor;
    }

    public boolean isIgnoreTurtleHelmet() {
        return ignoreTurtleHelmet;
    }

    public boolean isIgnoreElytra() {
        return ignoreElytra;
    }

    public boolean isOld() {
        return isOld;
    }
}
