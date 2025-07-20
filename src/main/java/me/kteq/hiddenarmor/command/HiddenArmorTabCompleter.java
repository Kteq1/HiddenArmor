package me.kteq.hiddenarmor.command;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.util.ConfigHolder;
import me.kteq.hiddenarmor.util.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class HiddenArmorTabCompleter implements TabCompleter, ConfigHolder {
    private boolean defaultPermissionToggle;

    public HiddenArmorTabCompleter(HiddenArmor plugin) {
        plugin.addConfigHolder(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equals("hiddenarmor")) return null;
        if(args.length > 1) return null;
        List<String> options = new ArrayList<>();


        if(PermissionUtil.canUse(sender ,"hiddenarmor.toggle") || defaultPermissionToggle) {
            options.add("toggle");
            options.add("hide");
            options.add("show");
        }
        if(PermissionUtil.canUse(sender, "hiddenarmor.reload")) {
            options.add("reload");
        }
        options.add("help");

        return options;
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.defaultPermissionToggle = config.getBoolean("default-permissions.toggle");
    }
}
