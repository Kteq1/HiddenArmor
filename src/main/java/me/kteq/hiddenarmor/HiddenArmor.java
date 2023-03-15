package me.kteq.hiddenarmor;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.kteq.hiddenarmor.handler.ArmorPacketHandler;
import me.kteq.hiddenarmor.handler.MessageHandler;
import me.kteq.hiddenarmor.util.Metrics;
import me.kteq.hiddenarmor.command.HiddenArmorCommand;
import me.kteq.hiddenarmor.command.ToggleArmorCommand;
import me.kteq.hiddenarmor.listener.EntityToggleGlideListener;
import me.kteq.hiddenarmor.listener.GameModeListener;
import me.kteq.hiddenarmor.listener.PotionEffectListener;
import me.kteq.hiddenarmor.listener.InventoryShiftClickListener;
import me.kteq.hiddenarmor.listener.packet.ArmorOthersPacketListener;
import me.kteq.hiddenarmor.listener.packet.ArmorSelfPacketListener;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class HiddenArmor extends JavaPlugin {
    private HiddenArmorManager hiddenArmorManager;

    private boolean isOld;

    @Override
    public void onEnable() {
        // Default config file
        this.saveDefaultConfig();
        checkConfig();

        isOld = Bukkit.getBukkitVersion().startsWith("1.16");

        MessageHandler.getInstance().setup(this, getConfig().getString("default-locale", "en_us"), "&c[&fHiddenArmor&c] &f");

        // Instantiate managers
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        this.hiddenArmorManager = new HiddenArmorManager(this);

        ArmorPacketHandler.getInstance().setup(this, manager);

        // Enable commands
        new ToggleArmorCommand(this);
        new HiddenArmorCommand(this);
        // Register ProtocolLib packet listeners
        new ArmorSelfPacketListener(this, manager);
        new ArmorOthersPacketListener(this, manager);
        // Register event listeners
        new InventoryShiftClickListener(this);
        new GameModeListener(this);
        new PotionEffectListener(this);
        new EntityToggleGlideListener(this);

        // Metrics
        Metrics metrics = new Metrics(this, 14419);
    }

    @Override
    public void onDisable() {
        hiddenArmorManager.saveCurrentEnabledPlayers();
    }

    private void checkConfig() {
        if(getConfig().getInt("config-version") >= getConfig().getDefaults().getInt("config-version")) return;
        getLogger().log(Level.WARNING, "Your HiddenArmor configuration file is outdated!");
        getLogger().log(Level.WARNING, "Please regenerate the 'config.yml' file when possible.");
    }

    public boolean isOld() {
        return isOld;
    }

    public HiddenArmorManager getHiddenArmorManager() {
        return hiddenArmorManager;
    }
}
