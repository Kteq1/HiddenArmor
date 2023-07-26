package me.kteq.hiddenarmor.command;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.command.util.AbstractCommand;
import me.kteq.hiddenarmor.command.util.CommandStatus;
import me.kteq.hiddenarmor.handler.MessageHandler;
import me.kteq.hiddenarmor.manager.HiddenArmorManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ToggleArmorCommand extends AbstractCommand {
    HiddenArmor plugin;

    public ToggleArmorCommand(HiddenArmor plugin, String command) {
        super(plugin, command);
        this.plugin = plugin;
    }

    @Override
    public CommandStatus execute(CommandSender sender, Command command, String[] arguments) {
        FileConfiguration config = plugin.getConfig();
        HiddenArmorManager hiddenArmorManager = plugin.getHiddenArmorManager();

        Player player;
        MessageHandler messageHandler = MessageHandler.getInstance();
        if(arguments.length == 1) {
            if(!hasSubPermission(sender, "other") && !config.getBoolean("default-permissions.toggle-other")) return CommandStatus.SUCCESS;
            String playerName = arguments[0];
            player = Bukkit.getPlayer(playerName);

            if(player == null){
                messageHandler.message(sender, "%player-not-found%");
                return CommandStatus.SUCCESS;
            }
        }else {
            if (sender instanceof ConsoleCommandSender) {
                messageHandler.message(sender, "%console-togglearmor-warning%");
                sendUsage(sender);
                return CommandStatus.SUCCESS;
            } else {
                player = (Player) sender;
            }
        }

        hiddenArmorManager.togglePlayer(player, true);

        if(!player.equals(sender)) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("visibility", hiddenArmorManager.isEnabled(player) ? "%visibility-hidden%" : "%visibility-shown%");
            placeholderMap.put("player", player.getName());
            messageHandler.message(sender, "%armor-visibility-other%", false, placeholderMap);
        }
        return CommandStatus.SUCCESS;
    }

    public void sendUsage(CommandSender sender) {
        MessageHandler messageHandler = MessageHandler.getInstance();
        Map<String, String> placeholderMap = new HashMap<>();
        if(sender instanceof Player) {
            String usage = "/togglearmor" + (hasSubPermission(sender, "other") ? " [%player%]" : "");
            placeholderMap.put("usage", usage);
        } else {
            placeholderMap.put("usage", "/togglearmor <%player%>");
        }
        messageHandler.message(sender, "%correct-usage%", false, placeholderMap);
    }
}
