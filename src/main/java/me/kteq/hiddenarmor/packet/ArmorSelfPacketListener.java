package me.kteq.hiddenarmor.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.manager.ArmorManager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ArmorSelfPacketListener {

    public ArmorSelfPacketListener(HiddenArmor pl, ProtocolManager manager, ArmorManager armorManager){
        PacketAdapter.AdapterParameteters params = PacketAdapter.params().plugin(pl)
                .listenerPriority(ListenerPriority.HIGH)
                .types(PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);

        manager.addPacketListener(new PacketAdapter(params) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                if(pl.shouldNotHide(player)) return;


                //SET_SLOT
                if(packet.getType().equals(PacketType.Play.Server.SET_SLOT) && packet.getIntegers().read(0).equals(0) && packet.getIntegers().read(pl.isOld() ? 1 : 2) > 4 && packet.getIntegers().read(pl.isOld() ? 1 : 2) < 9){
                    ItemStack itemStack = packet.getItemModifier().read(0);
                    if(itemStack!=null) packet.getItemModifier().write(0, armorManager.hideArmor(itemStack));
                }

                //WINDOW_ITEMS
                if(packet.getType().equals(PacketType.Play.Server.WINDOW_ITEMS) && packet.getIntegers().read(0).equals(0)){
                    List<ItemStack> itemStacks = packet.getItemListModifier().read(0);
                    itemStacks.stream().skip(5).limit(4).forEach(e -> {
                        if(e!=null) e.setItemMeta(armorManager.hideArmor(e).getItemMeta());
                    });
                }
            }
        });

    }
}
