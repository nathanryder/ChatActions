package me.bumblebeee_.chatactions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("chatactions")) {
            if (!(args.length > 0)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.InvalidArgs")));
                return false;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("chatactions.reload")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.NoPermissions")));
                    return false;
                }

                ChatActions.getInstance().reloadConfig();
                sender.sendMessage((ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.ConfigReloaded"))));
            } else if (args[0].equalsIgnoreCase("spy")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Console cannot use this command!");
                    return false;
                }
                Player p = (Player) sender;

                if (!p.hasPermission("chatactions.spy")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.NoPermissions")));
                    return false;
                }

                if (ChatActions.spyMode.containsKey(p.getUniqueId())) {
                    if (ChatActions.spyMode.get(p.getUniqueId())) {
                        ChatActions.spyMode.remove(p.getUniqueId());
                        ChatActions.spyMode.put(p.getUniqueId(), false);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.SpyDisabled")));
                    } else {
                        ChatActions.spyMode.put(p.getUniqueId(), true);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.SpyEnabled")));
                    }
                    return true;
                } else {
                    ChatActions.spyMode.put(p.getUniqueId(), true);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.SpyEnabled")));
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.InvalidArgs")));
                return false;
            }
        }
        return false;
    }
}
