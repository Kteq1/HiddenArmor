package me.kteq.hiddenarmor.armormanager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.google.common.collect.Multimap;
import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.util.ItemUtil;
import me.kteq.hiddenarmor.util.ProtocolUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ArmorManager {
    private final HiddenArmor plugin;
    private final ProtocolManager manager;

    public ArmorManager(HiddenArmor pl , ProtocolManager pm){
        this.plugin = pl;
        this.manager = pm;
    }

    public void updatePlayer(Player player) {
        updateSelf(player);
        updateOthers(player);
    }

    public void updateSelf(Player player){
        PlayerInventory inv = player.getInventory();
        for(int i = 5; i<=8;i++){
            PacketContainer packetSelf = manager.createPacket(PacketType.Play.Server.SET_SLOT);
            packetSelf.getIntegers().write(0, 0);
            if(!plugin.isOld())
                packetSelf.getIntegers().write(2, i);
            else
                packetSelf.getIntegers().write(1,i);
            ItemStack armor = ProtocolUtil.getArmor(ProtocolUtil.ArmorType.getType(i), inv);
            packetSelf.getItemModifier().write(0, armor);
            try {
                manager.sendServerPacket(player, packetSelf);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateOthers(Player player){
        PlayerInventory inv = player.getInventory();
        PacketContainer packetOthers = manager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packetOthers.getIntegers().write(0, player.getEntityId());
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = packetOthers.getSlotStackPairLists().read(0);
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.HELMET, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.CHEST, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.LEGGS, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.FEET, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.BOOTS, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, player.getInventory().getItemInMainHand().clone()));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, player.getInventory().getItemInOffHand().clone()));
        packetOthers.getSlotStackPairLists().write(0, pairList);
        ProtocolUtil.broadcastPlayerPacket(manager, packetOthers, player);
    }

    public ItemStack hideArmor(ItemStack itemStack){
        if(itemStack.getType().equals(Material.AIR)) return itemStack;

        // Getting item meta and lore
        ItemMeta itemMeta = itemStack.getItemMeta().clone();
        List<String> lore;
        if(itemMeta.hasLore())
            lore = itemMeta.getLore();
        else
            lore = new ArrayList<>();

        // Adding item durability percentage to lore, if it has it
        String durability = ArmorManagerUtil.getItemDurability(itemStack);
        if(durability!=null) lore.add(durability);

        // ArmoredElytra mod compatibility
        if(itemStack.getType().equals(Material.ELYTRA)){
            itemMeta = hideElytra(itemStack);
        }

        // Changing armor material and name to its placeholder's, if it has one
        Material button = getArmorButtonMaterial(itemStack);
        if(button!=null){
            String name = ArmorManagerUtil.getArmorName(itemStack);
            if(name!=null) itemMeta.setDisplayName(ArmorManagerUtil.getArmorName(itemStack));
            itemStack.setType(button);
        }

        // Applying item meta and lore
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemMeta hideElytra(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Storing the elytra current enchantments, attributes and damage
        Map<Enchantment, Integer> encs = itemMeta.getEnchants();
        Multimap<org.bukkit.attribute.Attribute, AttributeModifier> attrs = itemMeta.getAttributeModifiers();
        int damage = ((org.bukkit.inventory.meta.Damageable) itemMeta).getDamage();

        itemStack = new ItemStack(Material.ELYTRA);

        // Getting item meta from the new elytra
        itemMeta = itemStack.getItemMeta();

        // Applying stored enchantments to the new elytra
        for(Enchantment key : encs.keySet()){
            itemMeta.addEnchant(key, encs.get(key), true);
        }

        // Applying stored attributes to the new elytra
        itemMeta.setAttributeModifiers(attrs);

        // Applying stored damage to the new elytra
        ((org.bukkit.inventory.meta.Damageable) itemMeta).setDamage(damage);

        return itemMeta;
    }

    public Material getArmorButtonMaterial(ItemStack armor){
        if(!ItemUtil.isArmor(armor)) return null;
        String m = armor.getType().toString();
        if(m.startsWith("NETHERITE_"))
            return Material.POLISHED_BLACKSTONE_BUTTON;
        if(m.startsWith("DIAMOND_"))
            return Material.WARPED_BUTTON;
        if(m.startsWith("GOLDEN_"))
            return Material.BIRCH_BUTTON;
        if(m.startsWith("IRON_"))
            return Material.STONE_BUTTON;
        if(m.startsWith("LEATHER_") && !plugin.isIgnoreLeatherArmor())
            return Material.ACACIA_BUTTON;
        if(m.startsWith("CHAINMAIL_"))
            return Material.JUNGLE_BUTTON;
        if(m.startsWith("TURTLE_") && !plugin.isIgnoreTurtleHelmet())
            return Material.CRIMSON_BUTTON;
        return null;
    }
}
