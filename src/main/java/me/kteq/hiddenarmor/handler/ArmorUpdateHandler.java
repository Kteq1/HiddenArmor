package me.kteq.hiddenarmor.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.util.protocol.PacketFields;
import me.kteq.hiddenarmor.util.protocol.PacketIndexMapper;
import me.kteq.hiddenarmor.util.protocol.ProtocolUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class ArmorUpdateHandler {
    private final ProtocolManager protocolManager;

    private final int SET_SLOT_$WINDOW_ID_INDEX;
    private final int SET_SLOT_$SLOT_NUMBER_INDEX;
    private final int SET_SLOT_$ITEM_INDEX;

    private final int ENTITY_EQUIPMENT_$ENTITY_ID_INDEX;
    private final int ENTITY_EQUIPMENT_$SLOT_ITEM_PAIR_LIST_INDEX;

    public ArmorUpdateHandler(HiddenArmor plugin, PacketIndexMapper indexMapper) {
        this.protocolManager = plugin.getProtocolManager();

        this.SET_SLOT_$WINDOW_ID_INDEX = indexMapper.get(PacketFields.SET_SLOT_$WINDOW_ID);
        this.SET_SLOT_$SLOT_NUMBER_INDEX = indexMapper.get(PacketFields.SET_SLOT_$SLOT_NUMBER);
        this.SET_SLOT_$ITEM_INDEX = indexMapper.get(PacketFields.SET_SLOT_$ITEM);
        this.ENTITY_EQUIPMENT_$ENTITY_ID_INDEX = indexMapper.get(PacketFields.ENTITY_EQUIPMENT_$ENTITY_ID);
        this.ENTITY_EQUIPMENT_$SLOT_ITEM_PAIR_LIST_INDEX = indexMapper.get(PacketFields.ENTITY_EQUIPMENT_$SLOT_ITEM_PAIR_LIST);
    }

    public void updatePlayer(Player player) {
        updateSelf(player);
        updateToOthers(player);
    }

    public void updateSelf(Player player) {
        PlayerInventory inv = player.getInventory();
        for(int i = 5; i<=8;i++) {
            PacketContainer packetSelf = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT);

            packetSelf.getIntegers().write(SET_SLOT_$WINDOW_ID_INDEX, 0);
            packetSelf.getIntegers().write(SET_SLOT_$SLOT_NUMBER_INDEX, i);

            ItemStack armor = ProtocolUtil.getArmor(ProtocolUtil.ArmorType.getType(i), inv);
            packetSelf.getItemModifier().write(SET_SLOT_$ITEM_INDEX, armor);

            protocolManager.sendServerPacket(player, packetSelf);
        }
    }

    public void updateToOthers(Player player) {
        PacketContainer packetOthers = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packetOthers.getIntegers().write(ENTITY_EQUIPMENT_$ENTITY_ID_INDEX, player.getEntityId());

        PlayerInventory inv = player.getInventory();
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = packetOthers.getSlotStackPairLists().read(0);
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.HELMET, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.CHEST, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.LEGGS, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.FEET, ProtocolUtil.getArmor(ProtocolUtil.ArmorType.BOOTS, inv)));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, player.getInventory().getItemInMainHand().clone()));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, player.getInventory().getItemInOffHand().clone()));

        packetOthers.getSlotStackPairLists().write(ENTITY_EQUIPMENT_$SLOT_ITEM_PAIR_LIST_INDEX, pairList);

        ProtocolUtil.broadcastPlayerPacket(protocolManager, packetOthers, player);
    }
}
