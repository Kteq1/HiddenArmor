package me.kteq.hiddenarmor.listener;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.armormanager.ArmorManager;
import me.kteq.hiddenarmor.util.EventUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryShiftClickListener implements Listener {
    HiddenArmor plugin;
    ArmorManager armorManager;

    public InventoryShiftClickListener(HiddenArmor pl, ArmorManager am){
        this.plugin = pl;
        this.armorManager = am;
        EventUtil.register(this, pl);
    }

    @EventHandler
    public void onShiftClickArmor(InventoryClickEvent event){
        if(!plugin.hasPlayer((Player) event.getWhoClicked())) return;
        if(!(event.getClickedInventory() instanceof PlayerInventory)) return;
        if(!event.isShiftClick()) return;

        Player player = (Player) event.getWhoClicked();
        PlayerInventory inv = player.getInventory();
        ItemStack armor = event.getCurrentItem();

        if(player == null) return;
        if(inv == null) return;
        if(armor == null) return;

        if((armor.getType().toString().endsWith("_HELMET") && inv.getHelmet()==null) ||
                ((armor.getType().toString().endsWith("_CHESTPLATE") || armor.getType().equals(Material.ELYTRA)) && inv.getChestplate()==null) ||
                (armor.getType().toString().endsWith("_LEGGINGS") && inv.getLeggings()==null) ||
                (armor.getType().toString().endsWith("_BOOTS") && inv.getBoots()==null)){
            new BukkitRunnable(){
                @Override
                public void run() {
                    armorManager.updateSelf(player);
                }
            }.runTaskLater(plugin, 1L);
        }
    }
}
