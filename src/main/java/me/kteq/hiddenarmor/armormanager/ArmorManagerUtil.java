package me.kteq.hiddenarmor.armormanager;

import me.kteq.hiddenarmor.util.ItemUtil;
import me.kteq.hiddenarmor.util.StrUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ArmorManagerUtil {

    public static String getItemDurability(ItemStack itemStack){
        int percentage = ItemUtil.getDurabilityPercentage(itemStack);
        if(percentage != -1){
            String color = "&e";
            if(percentage>=70) color = "&a";
            if(percentage<30) color = "&c";
            return StrUtil.color("&fDurability: "+ color + percentage +"%");
        }
        return null;
    }

    public static String getArmorName(ItemStack itemStack){
        Material item = itemStack.getType();
        if(!item.toString().contains("_")) return null;
        String[] splitName = item.toString().split("_");
        String mat = splitName[0].substring(1).toLowerCase();
        String type = splitName[1].substring(1).toLowerCase();
        String name = splitName[0].charAt(0)+mat+" "+splitName[1].charAt(0)+type;
        if(itemStack.getItemMeta().hasDisplayName())
            name = itemStack.getItemMeta().getDisplayName()+ StrUtil.color(" &r&8(")+name+")";
        else
            name = StrUtil.color("&r")+name;
        return name;
    }
}
