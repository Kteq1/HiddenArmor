package me.kteq.hiddenarmor.listener;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.armormanager.ArmorManager;
import me.kteq.hiddenarmor.util.EventUtil;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class GameModeListener implements Listener {
    HiddenArmor plugin;
    ArmorManager armorManager;

    public GameModeListener(HiddenArmor pl, ArmorManager am){
        this.plugin = pl;
        this.armorManager = am;
        EventUtil.register(this, pl);
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event){
        if(!plugin.hasPlayer(event.getPlayer())) return;
        if(event.getNewGameMode().equals(GameMode.CREATIVE)){
            plugin.addIgnoredPlayer(event.getPlayer());
            armorManager.updatePlayer(event.getPlayer());
            plugin.removeIgnoredPlayer(event.getPlayer());
        }else{
            new BukkitRunnable(){
                @Override
                public void run() {
                    armorManager.updatePlayer(event.getPlayer());
                }
            }.runTaskLater(plugin, 1L);
        }
    }


}
