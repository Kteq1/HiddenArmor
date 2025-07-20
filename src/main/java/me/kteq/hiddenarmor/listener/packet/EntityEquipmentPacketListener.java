package me.kteq.hiddenarmor.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.util.ConfigHolder;
import me.kteq.hiddenarmor.util.protocol.PacketFields;
import me.kteq.hiddenarmor.util.protocol.PacketIndexMapper;
import me.kteq.hiddenarmor.manager.PlayerManager;
import me.kteq.hiddenarmor.util.ItemUtil;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EntityEquipmentPacketListener extends PacketAdapter implements ConfigHolder {
    private final PlayerManager hiddenArmorManager;
    private final ProtocolManager protocolManager;

    private boolean ignoreLeatherArmor;
    private boolean ignoreTurtleHelmet;
    private boolean ignoreElytra;

    private final int ENTITY_ID_INDEX;
    private final int SLOT_ITEM_PAIR_LIST_INDEX;

    public EntityEquipmentPacketListener(HiddenArmor plugin, PacketIndexMapper indexMapper) {
        super(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT);
        plugin.addConfigHolder(this);

        this.hiddenArmorManager = plugin.getPlayerManager();
        this.protocolManager = plugin.getProtocolManager();

        this.ENTITY_ID_INDEX = indexMapper.get(PacketFields.ENTITY_EQUIPMENT_$SLOT_ITEM_PAIR_LIST);
        this.SLOT_ITEM_PAIR_LIST_INDEX = indexMapper.get(PacketFields.ENTITY_EQUIPMENT_$ENTITY_ID);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        LivingEntity livingEntity = (LivingEntity) protocolManager.getEntityFromID(player.getWorld(), packet.getIntegers().read(ENTITY_ID_INDEX));
        if(!(livingEntity instanceof Player)) return;
        Player packetPlayer = (Player) livingEntity;

        if(hiddenArmorManager.isArmorVisible(packetPlayer)) return;

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = packet.getSlotStackPairLists().read(SLOT_ITEM_PAIR_LIST_INDEX);

        for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : pairList) {
            ItemStack item = pair.getSecond();
            if (item.getType().equals(Material.ELYTRA)
                    && ((packetPlayer.isGliding() || ignoreElytra)
                    && !packetPlayer.isInvisible()))
            {
                pair.setSecond(new ItemStack(Material.ELYTRA));
            }
            else if (!shouldIgnore(pair.getSecond()))
                pair.setSecond(new ItemStack(Material.AIR));
        }
        packet.getSlotStackPairLists().write(0, pairList);
    }

    private boolean shouldIgnore(ItemStack itemStack) {
        Material material = itemStack.getType();

        return (ignoreLeatherArmor && material.toString().startsWith("LEATHER")) ||
                (ignoreTurtleHelmet && material.equals(Material.TURTLE_HELMET)) ||
                (!ItemUtil.isArmor(itemStack) && !itemStack.getType().equals(Material.ELYTRA)) ||
                (ignoreElytra && itemStack.getType().equals(Material.ELYTRA));
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.ignoreLeatherArmor = config.getBoolean("ignore.leather-armor");
        this.ignoreTurtleHelmet = config.getBoolean("ignore.turtle-helmet");
        this.ignoreElytra = config.getBoolean("ignore.elytra");
    }
}
