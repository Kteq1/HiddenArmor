package me.kteq.hiddenarmor.listener;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorUpdateHandler;
import me.kteq.hiddenarmor.manager.PlayerManager;
import me.kteq.hiddenarmor.util.EventUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PotionEffectListener implements Listener {
    HiddenArmor plugin;
    PlayerManager hiddenArmorManager;
    ArmorUpdateHandler armorUpdater;

    public PotionEffectListener(HiddenArmor plugin) {
        EventUtil.register(this, plugin);

        this.plugin = plugin;
        this.hiddenArmorManager = plugin.getPlayerManager();
        this.armorUpdater = plugin.getArmorUpdater();
    }

    @EventHandler
    public void onPlayerInvisibleEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        new BukkitRunnable(){
            @Override
            public void run() {
                armorUpdater.updatePlayer(player);
            }
        }.runTaskLater(plugin, 2L);
    }
}
