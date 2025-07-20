package me.kteq.hiddenarmor.handler;

import com.google.common.collect.Multimap;
import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.util.ConfigHolder;
import me.kteq.hiddenarmor.util.ItemUtil;
import me.kteq.hiddenarmor.util.StrUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArmorPlaceholderHandler implements ConfigHolder {
    private final HiddenArmor plugin;

    private boolean ignoreLeatherArmor;
    private boolean ignoreTurtleHelmet;

    public ArmorPlaceholderHandler(HiddenArmor plugin) {
        plugin.addConfigHolder(this);
        this.plugin = plugin;
    }

    public ItemStack buildItemPlaceholder(ItemStack itemStack) {
        if (itemStack.getType().equals(Material.AIR)) return itemStack;

        Material placeholderMaterial = getPlaceholderMaterial(itemStack);
        if (placeholderMaterial == null) return itemStack;
        ItemMeta newItemMeta = buildNewItemMeta(itemStack, placeholderMaterial);
        if (newItemMeta == null) return itemStack;

        List<String> lore = newItemMeta.getLore();
        if (lore == null)
            lore = new ArrayList<>();
        String durability = buildDurabilityText(itemStack);
        if (durability != null) lore.add(durability);
        newItemMeta.setLore(lore);

        String displayName = buildName(itemStack);
        newItemMeta.setDisplayName(displayName);

        itemStack.setType(placeholderMaterial);
        itemStack.setItemMeta(newItemMeta);

        return itemStack;
    }

    private ItemMeta buildNewItemMeta(ItemStack itemStack, Material material) {
        ItemMeta oldItemMeta = itemStack.getItemMeta();
        if (oldItemMeta == null) return null;

        Map<Enchantment, Integer> enchantments = oldItemMeta.getEnchants();
        Multimap<Attribute, AttributeModifier> attributes = oldItemMeta.getAttributeModifiers();
        int damage = ((org.bukkit.inventory.meta.Damageable) oldItemMeta).getDamage();

        ItemMeta newItemMeta = plugin.getServer().getItemFactory().getItemMeta(material);
        if (newItemMeta == null) return null;

        for(Enchantment key : enchantments.keySet()) {
            newItemMeta.addEnchant(key, enchantments.get(key), true);
        }

        newItemMeta.setAttributeModifiers(attributes);

        ((Damageable) newItemMeta).setDamage(damage);

        return newItemMeta;
    }

    private Material getPlaceholderMaterial(ItemStack armor) {
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
        if(m.startsWith("LEATHER_") && !ignoreLeatherArmor)
            return Material.ACACIA_BUTTON;
        if(m.startsWith("CHAINMAIL_"))
            return Material.JUNGLE_BUTTON;
        if(m.startsWith("TURTLE_") && !ignoreTurtleHelmet)
            return Material.CRIMSON_BUTTON;
        return null;
    }

    private String buildDurabilityText(ItemStack itemStack){
        int percentage = ItemUtil.getDurabilityPercentage(itemStack);
        if(percentage != -1){
            String color = "&e";
            if(percentage>=70) color = "&a";
            if(percentage<30) color = "&c";
            return StrUtil.color("&fDurability: "+ color + percentage +"%");
        }
        return null;
    }

    private String buildName(ItemStack itemStack){
        String name = itemStack.getType().toString();
        name = name.replaceAll("_", " ");
        name = WordUtils.capitalizeFully(name);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null && itemMeta.hasDisplayName())
            name = itemStack.getItemMeta().getDisplayName() + StrUtil.color(" &r&8(") + name + ")";
        else
            name = StrUtil.color("&r") + name;
        return name;
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.ignoreLeatherArmor = config.getBoolean("ignore.leather-armor");
        this.ignoreTurtleHelmet = config.getBoolean("ignore.turtle-helmet");
    }
}
