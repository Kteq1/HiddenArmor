package me.kteq.hiddenarmor.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import com.google.common.collect.Multimap;
import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;

import me.kteq.hiddenarmor.util.ItemUtil;
import me.kteq.hiddenarmor.util.StrUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArmorSelfPacketListener {
    private final HiddenArmor plugin;
    private final HiddenArmorManager hiddenArmorManager;

    public ArmorSelfPacketListener(HiddenArmor plugin, ProtocolManager manager){
        this.plugin = plugin;
        this.hiddenArmorManager = plugin.getHiddenArmorManager();

        PacketAdapter.AdapterParameteters params = PacketAdapter.params().plugin(plugin)
                .listenerPriority(ListenerPriority.HIGH)
                .types(PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);

        manager.addPacketListener(new PacketAdapter(params) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                if(!hiddenArmorManager.isArmorHidden(player)) return;


                if(packet.getType().equals(PacketType.Play.Server.SET_SLOT) && packet.getIntegers().read(0).equals(0) && packet.getIntegers().read(ArmorSelfPacketListener.this.plugin.isOld() ? 1 : 2) > 4 && packet.getIntegers().read(ArmorSelfPacketListener.this.plugin.isOld() ? 1 : 2) < 9){
                    ItemStack itemStack = packet.getItemModifier().read(0);
                    if(itemStack!=null) packet.getItemModifier().write(0, getHiddenArmorPiece(itemStack));
                }

                if(packet.getType().equals(PacketType.Play.Server.WINDOW_ITEMS) && packet.getIntegers().read(0).equals(0)){
                    List<ItemStack> itemStacks = packet.getItemListModifier().read(0);
                    itemStacks.stream().skip(5).limit(4).forEach(e -> {
                        if(e!=null) e.setItemMeta(getHiddenArmorPiece(e).getItemMeta());
                    });
                }
            }
        });

    }

    public ItemStack getHiddenArmorPiece(ItemStack itemStack) {
        if(itemStack.getType().equals(Material.AIR)) return itemStack;

        ItemMeta itemMeta = itemStack.getItemMeta().clone();
        List<String> lore;
        if(itemMeta.hasLore())
            lore = itemMeta.getLore();
        else
            lore = new ArrayList<>();

        String durability = getPieceDurability(itemStack);
        if(durability!=null) lore.add(durability);

        if(itemStack.getType().equals(Material.ELYTRA)){
            itemMeta = getHiddenElytraMeta(itemStack);
        }

        Material button = getArmorButtonMaterial(itemStack);
        if(button!=null){
            String name = getPieceName(itemStack);
            if(name != null) itemMeta.setDisplayName(name);
            itemStack.setType(button);
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private ItemMeta getHiddenElytraMeta(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        Map<Enchantment, Integer> encs = itemMeta.getEnchants();
        Multimap<Attribute, AttributeModifier> attrs = itemMeta.getAttributeModifiers();
        int damage = ((org.bukkit.inventory.meta.Damageable) itemMeta).getDamage();

        itemStack = new ItemStack(Material.ELYTRA);

        itemMeta = itemStack.getItemMeta();

        for(Enchantment key : encs.keySet()){
            itemMeta.addEnchant(key, encs.get(key), true);
        }

        itemMeta.setAttributeModifiers(attrs);

        ((org.bukkit.inventory.meta.Damageable) itemMeta).setDamage(damage);

        return itemMeta;
    }

    private Material getArmorButtonMaterial(ItemStack armor) {
        if(!ItemUtil.isArmor(armor)) return null;
        FileConfiguration config = plugin.getConfig();

        String m = armor.getType().toString();
        if(m.startsWith("NETHERITE_"))
            return Material.POLISHED_BLACKSTONE_BUTTON;
        if(m.startsWith("DIAMOND_"))
            return Material.WARPED_BUTTON;
        if(m.startsWith("GOLDEN_"))
            return Material.BIRCH_BUTTON;
        if(m.startsWith("IRON_"))
            return Material.STONE_BUTTON;
        if(m.startsWith("LEATHER_") && !config.getBoolean("ignore.leather-armor"))
            return Material.ACACIA_BUTTON;
        if(m.startsWith("CHAINMAIL_"))
            return Material.JUNGLE_BUTTON;
        if(m.startsWith("TURTLE_") && !config.getBoolean("ignore.turtle-helmet"))
            return Material.CRIMSON_BUTTON;
        return null;
    }

    private String getPieceDurability(ItemStack itemStack){
        int percentage = ItemUtil.getDurabilityPercentage(itemStack);
        if(percentage != -1){
            String color = "&e";
            if(percentage>=70) color = "&a";
            if(percentage<30) color = "&c";
            return StrUtil.color("&fDurability: "+ color + percentage +"%");
        }
        return null;
    }

    private String getPieceName(ItemStack itemStack){
        String name = itemStack.getType().toString();
        name = name.replaceAll("_", " ");
        name = WordUtils.capitalizeFully(name);
        if(itemStack.getItemMeta().hasDisplayName())
            name = itemStack.getItemMeta().getDisplayName() + StrUtil.color(" &r&8(") + name + ")";
        else
            name = StrUtil.color("&r") + name;
        return name;
    }
}
