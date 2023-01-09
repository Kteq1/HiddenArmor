package me.kteq.hiddenarmor.listener;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.armormanager.ArmorManager;
import me.kteq.hiddenarmor.util.EventUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PotionEffectListener implements Listener {
    HiddenArmor plugin;
    ArmorManager armorManager;

    public PotionEffectListener(HiddenArmor pl, ArmorManager am){
        this.plugin = pl;
        this.armorManager = am;
        EventUtil.register(this, pl);
    }

    @EventHandler
    public void onPlayerInvisibleEffect(EntityPotionEffectEvent event){
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        new BukkitRunnable(){
            @Override
            public void run() {
                armorManager.updatePlayer(player);
            }
        }.runTaskLater(plugin, 2L);
    }
}
