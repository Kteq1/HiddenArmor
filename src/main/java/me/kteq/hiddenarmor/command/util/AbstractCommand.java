package me.kteq.hiddenarmor.command.util;

import me.kteq.hiddenarmor.handler.MessageHandler;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;


public abstract class AbstractCommand implements CommandExecutor {
    private PluginCommand pluginCommand;

    private String permission = null;
    private boolean permissionRequired = true;
    private boolean playerOnly = false;

    public AbstractCommand(JavaPlugin plugin, String command) {
        PluginCommand pluginCommand = plugin.getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
        } else {
            plugin.getLogger().severe("Could not register '/" + command + "' command. Is it present on plugin.yml?");
            return;
        }

        this.permission = pluginCommand.getPermission();
        this.pluginCommand = pluginCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageHandler messageHandler = MessageHandler.getInstance();
        if (sender instanceof ConsoleCommandSender && isPlayerOnly()) {
            messageHandler.message(sender, "%command-player-only%");
            return true;
        }
        if (!canUse(sender)) {
            messageHandler.message(sender, "%command-no-permission%");
            return true;
        }
        CommandStatus commandStatus = null;
        try {
            commandStatus = execute(sender, command, args);
        } catch (Exception e) {
            commandStatus = CommandStatus.ERROR;
            messageHandler.message(sender, "%command-error%");
            throw new RuntimeException(e);
        }
        if (commandStatus.equals(CommandStatus.INVALID_USAGE)) {
            messageHandler.message(sender, "%command-invalid%");
        }
        return true;
    }

    public boolean canUse(CommandSender sender) {
        if (!isPermissionRequired() || getPermission() == null) {
            return true;
        }
        return sender.hasPermission(getPermission()) || sender.isOp();
    }

    protected boolean hasSubPermission(CommandSender sender, String subPermission){
        return sender.isOp() || sender.hasPermission(permission + "." + subPermission);
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public boolean isPermissionRequired() {
        return this.permissionRequired;
    }

    public String getPermission() {
        return this.permission;
    }

    public AbstractCommand setPlayerOnly(boolean playerOnly) {
        this.playerOnly = playerOnly;
        return this;
    }

    public AbstractCommand setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
        return this;
    }

    public AbstractCommand setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public AbstractCommand setTabCompleter(TabCompleter tabCompleter) {
        pluginCommand.setTabCompleter(tabCompleter);
        return this;
    }

    public abstract CommandStatus execute(CommandSender sender, Command command, String[] arguments) throws Exception;

}
