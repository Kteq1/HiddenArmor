package me.kteq.hiddenarmor.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorPlaceholderHandler;
import me.kteq.hiddenarmor.manager.PlayerManager;

import me.kteq.hiddenarmor.util.protocol.PacketFields;
import me.kteq.hiddenarmor.util.protocol.PacketIndexMapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetSlotPacketListener extends PacketAdapter {
    private final PlayerManager playerManager;
    private final ArmorPlaceholderHandler placeholderHandler;

    private final int WINDOW_ID_INDEX;
    private final int SLOT_NUMBER_INDEX;
    private final int ITEM_INDEX;


    public SetSlotPacketListener(HiddenArmor plugin, PacketIndexMapper indexMapper) {
        super(plugin, PacketType.Play.Server.SET_SLOT);
        this.playerManager = plugin.getPlayerManager();
        this.placeholderHandler = plugin.getArmorPlaceholderHandler();

        this.WINDOW_ID_INDEX = indexMapper.get(PacketFields.SET_SLOT_$WINDOW_ID);
        this.SLOT_NUMBER_INDEX = indexMapper.get(PacketFields.SET_SLOT_$SLOT_NUMBER);
        this.ITEM_INDEX = indexMapper.get(PacketFields.SET_SLOT_$ITEM);
    }


    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (playerManager.isArmorVisible(player)) return;

        PacketContainer packet = event.getPacket();
        if (!packet.getIntegers().read(WINDOW_ID_INDEX).equals(0)) return;

        int slotNumber = packet.getIntegers().read(SLOT_NUMBER_INDEX);
        if (slotNumber < 5 || slotNumber > 8) return;


        ItemStack itemStack = packet.getItemModifier().read(ITEM_INDEX);
        if (itemStack != null) {
            ItemStack placeholder = placeholderHandler.buildItemPlaceholder(itemStack);
            packet.getItemModifier().write(0, placeholder);
        }
    }

}
