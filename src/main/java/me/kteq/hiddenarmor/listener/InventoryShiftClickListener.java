package me.kteq.hiddenarmor.listener;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorUpdateHandler;
import me.kteq.hiddenarmor.manager.PlayerManager;
import me.kteq.hiddenarmor.util.EventUtil;
import me.kteq.hiddenarmor.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryShiftClickListener implements Listener {
    private final HiddenArmor plugin;
    private final PlayerManager hiddenArmorManager;
    private final ArmorUpdateHandler armorUpdater;

    public InventoryShiftClickListener(HiddenArmor plugin){
        EventUtil.register(this, plugin);

        this.plugin = plugin;
        this.hiddenArmorManager = plugin.getPlayerManager();
        this.armorUpdater = plugin.getArmorUpdater();
    }

    @EventHandler
    public void onShiftClickArmor(InventoryClickEvent event){
        if(hiddenArmorManager.isArmorVisible((Player) event.getWhoClicked())) return;
        if(!(event.getClickedInventory() instanceof PlayerInventory)) return;
        if(!event.isShiftClick()) return;

        Player player = (Player) event.getWhoClicked();
        PlayerInventory inv = player.getInventory();
        ItemStack armor = event.getCurrentItem();

        if(armor == null) return;

        if((armor.getType().toString().endsWith("_HELMET") && ItemUtil.isEmpty(inv.getHelmet())) ||
                ((armor.getType().toString().endsWith("_CHESTPLATE") || armor.getType().equals(Material.ELYTRA)) && ItemUtil.isEmpty(inv.getChestplate())) ||
                (armor.getType().toString().endsWith("_LEGGINGS") && ItemUtil.isEmpty(inv.getLeggings())) ||
                (armor.getType().toString().endsWith("_BOOTS") && ItemUtil.isEmpty(inv.getBoots()))) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    armorUpdater.updateSelf(player);
                }
            }.runTaskLater(plugin, 1L);
        }
    }
}
