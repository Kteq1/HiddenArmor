package me.kteq.hiddenarmor.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {

    /// @param itemStack The ItemStack to be checked.
    /// @return **true** if the itemStack is a player armor type item, **false** otherwise.
    public static boolean isArmor(ItemStack itemStack) {
        if (itemStack == null) return false;
        String type = itemStack.getType().name();
        return type.endsWith("_HELMET")
                || type.endsWith("_CHESTPLATE")
                || type.endsWith("_LEGGINGS")
                || type.endsWith("_BOOTS");
    }

    /// @param itemStack The ItemStack to be processed.
    /// @return The **int** percentage of the durability of the damageable item. Returns **-1** if the item is not damageable.
    public static int getDurabilityPercentage(ItemStack itemStack){
        if(itemStack.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable){
            org.bukkit.inventory.meta.Damageable meta = (org.bukkit.inventory.meta.Damageable) itemStack.getItemMeta();
            int maxDurability = itemStack.getType().getMaxDurability();
            if(maxDurability==0) return -1;
            return 100-((meta.getDamage()*100)/maxDurability);
        }
        return -1;
    }

    /// @param itemStack The ItemStack to be checked.
    /// @return **true** if the itemStack is either null or type AIR, **false** otherwise.
    public static boolean isEmpty(ItemStack itemStack){
        return itemStack == null || itemStack.getType().equals(Material.AIR);
    }

}
