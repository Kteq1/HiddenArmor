package me.kteq.hiddenarmor.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.util.ProtocolUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ArmorPacketHandler {
    private static ArmorPacketHandler INSTANCE;

    private HiddenArmor plugin;
    private ProtocolManager protocolManager;

    public static ArmorPacketHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArmorPacketHandler();
        }
        return INSTANCE;
    }

    public void setup(HiddenArmor plugin, ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.protocolManager = protocolManager;
    }


    public void updatePlayer(Player player) {
        updateSelf(player);
        updateOthers(player);
    }

    public void updateSelf(Player player) {
        PlayerInventory inv = player.getInventory();
        for(int i = 5; i<=8;i++){
            PacketContainer packetSelf = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT);
            packetSelf.getIntegers().write(0, 0);
            if(!plugin.isOld())
                packetSelf.getIntegers().write(2, i);
            else
                packetSelf.getIntegers().write(1,i);
            ItemStack armor = ProtocolUtil.getArmor(ProtocolUtil.ArmorType.getType(i), inv);
            packetSelf.getItemModifier().write(0, armor);
            try {
                protocolManager.sendServerPacket(player, packetSelf);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateOthers(Player player) {
        PlayerInventory inv = player.getInventory();
        PacketContainer packetOthers = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packetOthers.getIntegers().write(0, player.getEntityId());
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = packetOthers.getSlotStackPairLists().read(0);
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.HELMET, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.CHEST, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.LEGGS, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.FEET, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.BOOTS, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, player.getInventory().getItemInMainHand().clone()));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, player.getInventory().getItemInOffHand().clone()));
        packetOthers.getSlotStackPairLists().write(0, pairList);
        ProtocolUtil.broadcastPlayerPacket(protocolManager, packetOthers, player);
    }
}
