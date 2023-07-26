package me.kteq.hiddenarmor.command;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.command.util.AbstractCommand;
import me.kteq.hiddenarmor.command.util.CommandStatus;
import me.kteq.hiddenarmor.handler.MessageHandler;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import me.kteq.hiddenarmor.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HiddenArmorCommand extends AbstractCommand {
    HiddenArmor plugin;
    HiddenArmorManager hiddenArmorManager;

    public HiddenArmorCommand(HiddenArmor plugin, String command) {
        super(plugin, command);
        this.plugin = plugin;
        this.hiddenArmorManager = plugin.getHiddenArmorManager();
    }

    @Override
    public CommandStatus execute(CommandSender sender, Command command, String[] arguments) throws Exception {
        if((arguments.length < 1) || (arguments[0].equalsIgnoreCase("help"))) {
            help(sender);
            return CommandStatus.SUCCESS;
        }

        MessageHandler messageHandler = MessageHandler.getInstance();

        String subcommand = arguments[0].toLowerCase();

        switch (subcommand) {
            case "reload":
                if (!hasSubPermission(sender, "reload")) break;
                plugin.saveDefaultConfig();
                plugin.reloadConfig();
                messageHandler.reloadLocales();
                messageHandler.message(sender, "%reload-success%", true);
                messageHandler.message(sender, "%reload-default-permission-note%", true);
                return CommandStatus.SUCCESS;
            case "toggle":
            case "hide":
            case "show":
                if (!toggleArmor(sender, arguments)) break;
                return CommandStatus.SUCCESS;
        }




        return CommandStatus.INVALID_USAGE;
    }

    private boolean toggleArmor(CommandSender sender, String[] arguments) {
        if (!hasSubPermission(sender, "toggle") && !plugin.getConfig().getBoolean("default-permissions.toggle")) return false;
        MessageHandler messageHandler = MessageHandler.getInstance();
        Player player;
        if (arguments.length == 2 && (hasSubPermission(sender, "toggle.other") || plugin.getConfig().getBoolean("default-permissions.toggle-other"))) {
            String playerName = arguments[1];
            player = Bukkit.getPlayer(playerName);

            if (player == null) {
                messageHandler.message(sender, "%player-not-found%");
                return true;
            }
        } else {
            if (sender instanceof ConsoleCommandSender) {
                messageHandler.message(sender, "%console-togglearmor-warning%");
                return true;
            } else {
                player = (Player) sender;
            }
        }

        String action = arguments[0].toLowerCase();

        switch (action) {
            case "toggle":
                hiddenArmorManager.togglePlayer(player, true);
                break;
            case "hide":
                hiddenArmorManager.enablePlayer(player, true);
                break;
            case "show":
                hiddenArmorManager.disablePlayer(player, true);
                break;
        }

        if (!player.equals(sender)) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("visibility", hiddenArmorManager.isEnabled(player) ? "%visibility-hidden%" : "%visibility-shown%");
            placeholderMap.put("player", player.getName());
            messageHandler.message(sender, "%armor-visibility-other%", false, placeholderMap);
        }
        return true;
    }

    private void help(CommandSender sender){
        MessageHandler messageHandler = MessageHandler.getInstance();
        messageHandler.message(sender,"&6----------[ &fHiddenArmor &6]-----------------");

        boolean togglePermission = plugin.getConfig().getBoolean("default-permissions.toggle");
        boolean toggleOtherPermission = plugin.getConfig().getBoolean("default-permissions.toggle-other");

        //hiddenarmor <toggle/hide/show>
        if(PermissionUtil.canUse(sender ,"hiddenarmor.toggle") || togglePermission)
            messageHandler.message(sender, "&e/hiddenarmor <toggle/hide/show> &6- %help-togglearmor%");

        //hiddenarmor <toggle/hide/show> <player>
        if(PermissionUtil.canUse(sender ,"hiddenarmor.toggle.other") || (togglePermission && toggleOtherPermission))
            messageHandler.message(sender, "&e/hiddenarmor <toggle/hide/show> [%player%] &6- %help-togglearmor-other%");

        // hiddenarmor reload
        if(PermissionUtil.canUse(sender, "hiddenarmor.reload"))
            messageHandler.message(sender, "&e/hiddenarmor reload &6- %help-reload%");

        // help
        messageHandler.message(sender, "&e/hiddenarmor help &6- %help-help%");

        messageHandler.message(sender,"&6----------------------------------------");
    }

}
