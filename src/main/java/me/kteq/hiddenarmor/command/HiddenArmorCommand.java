package me.kteq.hiddenarmor.command;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.MessageHandler;
import me.kteq.hiddenarmor.util.CommandUtil;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class HiddenArmorCommand {
    HiddenArmor plugin;

    public HiddenArmorCommand(HiddenArmor plugin){
        this.plugin = plugin;

        new CommandUtil(plugin, "hiddenarmor", 0, 1, false, true){
            @Override
            public boolean onCommand(CommandSender sender, String[] arguments) throws IOException {
                if((arguments.length < 1) || (arguments[0].equals("help"))){
                    help(sender);
                    return true;
                }

                MessageHandler messageHandler = MessageHandler.getInstance();
                if(arguments[0].equalsIgnoreCase("reload") && canUseArg(sender, "reload")){
                    plugin.reloadConfig();
                    messageHandler.reloadLocales();
                    messageHandler.message(sender, "%reload-success%", true);
                    messageHandler.message(sender, "%reload-default-permission-note%", true);
                    return true;
                }

                return false;
            }

            @Override
            public void sendUsage(CommandSender sender) {
                MessageHandler messageHandler = MessageHandler.getInstance();
                messageHandler.message(sender, "%command-unknown%");
            }

        }.setCPermission("");
    }

    public void help(CommandSender sender){
        MessageHandler messageHandler = MessageHandler.getInstance();
        messageHandler.message(sender,"&6----------[ &fHiddenArmor &6]-----------------");

        // togglearmor
        if(canUse(sender, "hiddenarmor.toggle") || plugin.getConfig().getBoolean("default-permissions.toggle"))
            messageHandler.message(sender, "&e/togglearmor &6- %help-togglearmor%");

        // togglearmor <player>
        if(canUse(sender ,"hiddenarmor.toggle.other"))
            messageHandler.message(sender, "&e/togglearmor <%player%> &6- %help-togglearmor-other%");

        // hiddenarmor reload
        if(canUse(sender, "hiddenarmor.reload"))
            messageHandler.message(sender, "&e/hiddenarmor reload &6- %help-reload%");

        // help
        messageHandler.message(sender, "&e/hiddenarmor help &6- %help-help%");

        messageHandler.message(sender,"&6----------------------------------------");
    }

    private boolean canUse(CommandSender sender, String perm){
        return sender.hasPermission(perm) || sender.isOp();
    }
}

