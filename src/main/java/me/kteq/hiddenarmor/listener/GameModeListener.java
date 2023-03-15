package me.kteq.hiddenarmor.listener;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorPacketHandler;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import me.kteq.hiddenarmor.util.EventUtil;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class GameModeListener implements Listener {
    HiddenArmor plugin;
    HiddenArmorManager hiddenArmorManager;

    public GameModeListener(HiddenArmor plugin){
        this.plugin = plugin;
        this.hiddenArmorManager = plugin.getHiddenArmorManager();
        EventUtil.register(this, plugin);
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event){
        if(!hiddenArmorManager.isEnabled(event.getPlayer())) return;
        if(event.getNewGameMode().equals(GameMode.CREATIVE)) {
            hiddenArmorManager.disablePlayer(event.getPlayer(), false);
            ArmorPacketHandler.getInstance().updatePlayer(event.getPlayer());
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                if (event.getNewGameMode().equals(GameMode.CREATIVE)) {
                    hiddenArmorManager.enablePlayer(event.getPlayer(), false);
                } else {
                    ArmorPacketHandler.getInstance().updatePlayer(event.getPlayer());
                }
            }
        }.runTaskLater(plugin, 1L);
    }


}
