package me.kteq.hiddenarmor.listener;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorPacketHandler;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import me.kteq.hiddenarmor.util.EventUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityToggleGlideListener implements Listener {
    HiddenArmor plugin;
    HiddenArmorManager hiddenArmorManager;

    public EntityToggleGlideListener(HiddenArmor plugin){
        this.plugin = plugin;
        this.hiddenArmorManager = plugin.getHiddenArmorManager();
        EventUtil.register(this, plugin);
    }

    @EventHandler
    public void onPlayerToggleGlide(EntityToggleGlideEvent e){
        if(!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        if(!hiddenArmorManager.isArmorHidden(player)) return;

        new BukkitRunnable(){
            @Override
            public void run() {
                ArmorPacketHandler.getInstance().updatePlayer(player);
            }
        }.runTaskLater(plugin, 1L);
    }
}
