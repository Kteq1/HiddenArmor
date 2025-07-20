package me.kteq.hiddenarmor.util.protocol;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PacketIndexMapper {
    private final Map<PacketFields, Integer> indexMapping;

    public PacketIndexMapper(JavaPlugin plugin) {
        indexMapping = new HashMap<>();
        buildMapping(plugin.getServer().getBukkitVersion().split("-")[0]);
    }

    public int get(PacketFields packetFields) {
        return indexMapping.get(packetFields);
    }

    private void buildMapping(String version) {
        indexMapping.put(PacketFields.SET_SLOT_$WINDOW_ID, 0);
        indexMapping.put(PacketFields.SET_SLOT_$SLOT_NUMBER, isVersionGreaterOrEqual(version, "1.16.5") ? 2 : 1);
        indexMapping.put(PacketFields.SET_SLOT_$ITEM, 0);

        indexMapping.put(PacketFields.WINDOW_ITEMS_$WINDOW_ID, 0);
        indexMapping.put(PacketFields.WINDOW_ITEMS_$ITEM_LIST, 0);

        indexMapping.put(PacketFields.ENTITY_EQUIPMENT_$ENTITY_ID, 0);
        indexMapping.put(PacketFields.ENTITY_EQUIPMENT_$SLOT_ITEM_PAIR_LIST, 0);
    }

    public static boolean isVersionGreaterOrEqual(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (num1 > num2) return true;
            if (num1 < num2) return false;
        }
        return true;
    }
}
