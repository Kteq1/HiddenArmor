package me.kteq.hiddenarmor.util;

import me.kteq.hiddenarmor.handler.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class CommandUtil extends BukkitCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final String pluginName;
    private final boolean defaultPermission;
    private String permission;
    private final int minArguments;
    private final int maxArguments;
    private final boolean playerOnly;
    private int cooldown;
    private List<UUID> cooldownPlayers = null;


    public CommandUtil(JavaPlugin plugin, String command, boolean playerOnly, boolean defaultPermission){
        this(plugin, command, 0, playerOnly, defaultPermission);
    }

    public CommandUtil(JavaPlugin plugin, String command, int requiredArguments, boolean playerOnly, boolean defaultPermission){
        this(plugin ,command, requiredArguments, requiredArguments, playerOnly, defaultPermission);
    }

    public CommandUtil(JavaPlugin plugin, String command, int minArguments, int maxArguments, boolean playerOnly, boolean defaultPermission){
        super(command);

        this.plugin = plugin;
        this.pluginName = plugin.getName().toLowerCase().replaceAll(" ","");

        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.playerOnly = playerOnly;
        this.cooldown = 0;
        this.defaultPermission = defaultPermission;
        this.setCPermission(command);

        CommandMap commandMap = getCommandMap();
        if(commandMap!=null){
            commandMap.register(this.pluginName, this);
        }
    }

    public CommandMap getCommandMap(){
        try {
            if(Bukkit.getPluginManager() instanceof SimplePluginManager){
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);

                return (CommandMap) field.get(Bukkit.getPluginManager());
            }
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    public CommandUtil setCPermission(String perm) {
        super.setPermission(defaultPermission ? null : this.pluginName+"." + perm);
        this.permission = this.pluginName+"." + perm;
        return this;
    }

    public CommandUtil setCooldown(int cooldown){
        this.cooldown = cooldown;
        this.cooldownPlayers = new ArrayList<>();
        return this;
    }

    public void removeCooldown(Player player){
        this.cooldownPlayers.remove(player.getUniqueId());
    }

    protected boolean canUseArg(CommandSender sender, String arg){
        return sender.isOp() || sender.hasPermission(permission + "." + arg);
    }

    public boolean execute(CommandSender sender, String alias, String [] arguments){
        MessageHandler messageHandler = MessageHandler.getInstance();
        String permission = getPermission();
        if(!defaultPermission && permission != null && !sender.hasPermission(permission) && !sender.isOp()){
            messageHandler.message(sender, "%command-no-permission%");
            return true;
        }

        if(arguments.length < minArguments || arguments.length > maxArguments){
            sendUsage(sender);
            return true;
        }

        if(playerOnly && !(sender instanceof Player)){
            messageHandler.message(sender, "%command-player-only%");
            return true;
        }

        if (cooldownPlayers != null && sender instanceof Player){
            Player player = (Player) sender;
            if(cooldownPlayers.contains(player.getUniqueId())){
                messageHandler.message(sender, "%command-delay%");
                return true;
            }

            cooldownPlayers.add(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> removeCooldown(player), 20L*cooldown);
        }

        try {
            if(!onCommand(sender, arguments)){
                sendUsage(sender);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String [] arguments){
        try {
            return this.onCommand(sender, arguments);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public abstract boolean onCommand(CommandSender sender, String [] arguments) throws IOException;

    public abstract void sendUsage(CommandSender sender);
}
