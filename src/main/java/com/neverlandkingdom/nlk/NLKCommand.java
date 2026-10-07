package com.neverlandkingdom.nlk;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class NLKCommand implements CommandExecutor {
    private final NLKPlugin plugin;

    public NLKCommand(NLKPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("status")) {
            sender.sendMessage(ChatColor.AQUA + "NLK Compatibility");
            sender.sendMessage(ChatColor.GRAY + "Status: " + ChatColor.GREEN + "online");
            sender.sendMessage(ChatColor.GRAY + "Backend: " + ChatColor.WHITE + plugin.getServer().getVersion());

            if (sender instanceof Player player) {
                ClientProfile profile = plugin.getClientVersionService().profile(player);
                sender.sendMessage(ChatColor.GRAY + "Client protocol: " + ChatColor.WHITE + profile.protocol());
                sender.sendMessage(ChatColor.GRAY + "Newer than server: " +
                        (profile.newerThanServer() ? ChatColor.GREEN + "yes" : ChatColor.GRAY + "no"));
                sender.sendMessage(ChatColor.GRAY + "Feature definitions available: " +
                        ChatColor.WHITE + plugin.getFeatureRegistry().forClientProtocol(profile.protocol()).size());
            } else {
                sender.sendMessage(ChatColor.GRAY + "Client protocol: " + ChatColor.WHITE + "console");
            }

            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "NLK configuration reload completed.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "/nlk status");
        sender.sendMessage(ChatColor.YELLOW + "/nlk reload");
        return true;
    }
}
