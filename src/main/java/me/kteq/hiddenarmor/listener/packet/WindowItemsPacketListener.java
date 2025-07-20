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

import java.util.List;

public class WindowItemsPacketListener extends PacketAdapter {
    private final PlayerManager playerManager;
    private final ArmorPlaceholderHandler placeholderHandler;

    private final int WINDOW_ID_INDEX;
    private final int ITEM_LIST_INDEX;

    public WindowItemsPacketListener(HiddenArmor plugin, PacketIndexMapper indexMapper) {
        super(plugin, PacketType.Play.Server.WINDOW_ITEMS);
        this.playerManager = plugin.getPlayerManager();
        this.placeholderHandler = plugin.getArmorPlaceholderHandler();

        this.WINDOW_ID_INDEX = indexMapper.get(PacketFields.WINDOW_ITEMS_$WINDOW_ID);
        this.ITEM_LIST_INDEX = indexMapper.get(PacketFields.WINDOW_ITEMS_$ITEM_LIST);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (playerManager.isArmorVisible(player)) return;

        PacketContainer packet = event.getPacket();
        if (!packet.getIntegers().read(WINDOW_ID_INDEX).equals(0)) return;


        List<ItemStack> items = packet.getItemListModifier().read(ITEM_LIST_INDEX);
        for (int i = 5; i < 9; i++) {
            ItemStack itemStack = items.get(i);
            if (itemStack != null) {
                ItemStack placeholder = placeholderHandler.buildItemPlaceholder(itemStack);
                items.set(i, placeholder);
            }
        }
    }
}
