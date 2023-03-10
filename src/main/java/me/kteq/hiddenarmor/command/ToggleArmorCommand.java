package me.kteq.hiddenarmor.command;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.manager.ArmorManager;
import me.kteq.hiddenarmor.message.MessageHandler;
import me.kteq.hiddenarmor.util.CommandUtil;
import me.kteq.hiddenarmor.util.StrUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ToggleArmorCommand {
    HiddenArmor plugin;
    ArmorManager armorManager;

    public ToggleArmorCommand(HiddenArmor plugin, ArmorManager armorManager){
        this.armorManager = armorManager;
        this.plugin = plugin;
        new CommandUtil(ToggleArmorCommand.this.plugin,"togglearmor", 0,1, false, ToggleArmorCommand.this.plugin.isToggleDefault()){

            @Override
            public void sendUsage(CommandSender sender) {
                MessageHandler messageHandler = MessageHandler.getInstance();
                Map<String, String> placeholderMap = new HashMap<>();
                if(sender instanceof Player) {
                    String usage = "/togglearmor" + (canUseArg(sender, "other") ? " [%player%]" : "");
                    placeholderMap.put("usage", usage);
                } else {
                    placeholderMap.put("usage", "/togglearmor <%player%>");
                }
                messageHandler.message(sender, "%correct-usage%", false, placeholderMap);
            }

            @Override
            public boolean onCommand(CommandSender sender, String[] arguments){
                Player player;
                MessageHandler messageHandler = MessageHandler.getInstance();
                if(arguments.length == 1) {
                    if(!canUseArg(sender, "other") && !ToggleArmorCommand.this.plugin.isToggleOtherDefault()) return false;
                    String playerName = arguments[0];
                    player = Bukkit.getPlayer(playerName);

                    if(player == null){
                        messageHandler.message(sender, "%player-not-found%");
                        return true;
                    }
                }else {
                    if (sender instanceof ConsoleCommandSender) {
                        messageHandler.message(sender, "%console-togglearmor-warning%");
                        sendUsage(sender);
                        return true;
                    } else {
                        player = (Player) sender;
                    }
                }

                String visibility;

                if(ToggleArmorCommand.this.plugin.hasPlayer(player)){
                    ToggleArmorCommand.this.plugin.removeHiddenPlayer(player);
                    visibility = "%visibility-shown%";
                }else {
                    ToggleArmorCommand.this.plugin.addHiddenPlayer(player);
                    visibility = "%visibility-hidden%";
                }

                Map<String, String> placeholderMap = new HashMap<>();
                placeholderMap.put("visibility", visibility);
                if(!player.equals(sender)) {
                    placeholderMap.put("player", player.getName());
                    messageHandler.message(sender, "%armor-visibility-other%", false, placeholderMap);
                }
                messageHandler.message(ChatMessageType.ACTION_BAR, player, "%armor-visibility%", false, placeholderMap);

                ToggleArmorCommand.this.armorManager.updatePlayer(player);

                return true;
            }
        }.setCPermission("toggle").setUsage("/togglearmor").setDescription("Toggle armor invisibility");
    }
}
