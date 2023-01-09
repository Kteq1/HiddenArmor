package me.kteq.hiddenarmor.command;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.util.CommandUtil;
import me.kteq.hiddenarmor.util.StrUtil;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class HiddenArmorCommand {
    HiddenArmor plugin;

    public HiddenArmorCommand(HiddenArmor pl){
        this.plugin = pl;

        new CommandUtil(plugin, "hiddenarmor", 0, 1, false, true){
            @Override
            public boolean onCommand(CommandSender sender, String[] arguments) throws IOException {
                if((arguments.length < 1) || (arguments[0].equals("help"))){
                    help(sender);
                    return true;
                }

                switch (arguments[0]) {

                }
                if(arguments[0].equalsIgnoreCase("reload") && canUseArg(sender, "reload")){
                    plugin.reload();
                    sender.sendMessage(plugin.getPrefix() + "Configuration reloaded!");
                    sender.sendMessage(plugin.getPrefix() + "NOTE: Default permissions changes need a server restart to be applied.");
                    return true;
                }

                return false;
            }

            @Override
            public void sendUsage(CommandSender sender) {
                sender.sendMessage(StrUtil.color("&cCommand not found, use '/hiddenarmor help' to list all commands available."));
            }

        }.setCPermission("");
    }

    public void help(CommandSender sender){
        sender.sendMessage(StrUtil.color("&6----------[ &fHiddenArmor &6]-----------------"));

        // togglearmor
        if(canUse(sender, "hiddenarmor.toggle") || plugin.isToggleDefault())
            sender.sendMessage(StrUtil.color("&e/togglearmor &6- &fToggle your armor visibility"));

        // togglearmor <player>
        if(canUse(sender ,"hiddenarmor.toggle.other"))
            sender.sendMessage(StrUtil.color("&e/togglearmor <player> &6- &fToggle other player's armor visibility"));

        // hiddenarmor reload
        if(canUse(sender, "hiddenarmor.reload"))
            sender.sendMessage(StrUtil.color("&e/hiddenarmor reload &6- &fReloads configuration"));

        // help
        sender.sendMessage(StrUtil.color("&e/hiddenarmor help &6- &fShows this help message"));

        sender.sendMessage(StrUtil.color("&6----------------------------------------"));
    }

    private boolean canUse(CommandSender sender, String perm){
        return sender.hasPermission(perm) || sender.isOp();
    }
}

