package me.kteq.hiddenarmor.util;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;

public class ProtocolUtil {

    public static void broadcastPlayerPacket(ProtocolManager manager, PacketContainer packet, Player player){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!(p.getWorld().equals(player.getWorld()) && p.getLocation().distance(player.getLocation()) < Bukkit.getViewDistance()*16 && !p.equals(player))) continue;
            try {
                manager.sendServerPacket(p, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isArmorSlot(Pair<EnumWrappers.ItemSlot, ItemStack> pair){
        return pair.getFirst().equals(EnumWrappers.ItemSlot.FEET) ||
                pair.getFirst().equals(EnumWrappers.ItemSlot.LEGS) ||
                pair.getFirst().equals(EnumWrappers.ItemSlot.CHEST) ||
                pair.getFirst().equals(EnumWrappers.ItemSlot.HEAD);
    }

    public enum ArmorType{
        HELMET(5), CHEST(6), LEGGS(7), BOOTS(8);

        private final int value;

        public static ArmorType getType(int value){
            for(int i = 0; i < values().length; i++){
                if(values()[i].getValue() == value) return values()[i];
            }
            return null;
        }

        public int getValue(){
            return value;
        }

        ArmorType(int i){
            this.value = i;
        }
    }

    public static ItemStack getArmor(ArmorType type, PlayerInventory inv){
        switch (type){
            case HELMET: if(inv.getHelmet()!=null) return inv.getHelmet().clone();
                break;
            case CHEST: if(inv.getChestplate()!=null) return inv.getChestplate().clone();
                break;
            case LEGGS: if(inv.getLeggings()!=null) return inv.getLeggings().clone();
                break;
            case BOOTS: if(inv.getBoots()!=null) return inv.getBoots().clone();
                break;
        }
        return new ItemStack(Material.AIR);
    }
}
