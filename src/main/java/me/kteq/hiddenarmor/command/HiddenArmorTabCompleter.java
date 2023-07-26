package me.kteq.hiddenarmor.command;

import me.kteq.hiddenarmor.util.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class HiddenArmorTabCompleter implements TabCompleter {
    private Plugin plugin;

    public HiddenArmorTabCompleter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equals("hiddenarmor")) return null;
        if(args.length > 1) return null;
        List<String> options = new ArrayList<>();

        boolean togglePermission = plugin.getConfig().getBoolean("default-permissions.toggle");

        if(PermissionUtil.canUse(sender ,"hiddenarmor.toggle") || togglePermission) {
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
}
