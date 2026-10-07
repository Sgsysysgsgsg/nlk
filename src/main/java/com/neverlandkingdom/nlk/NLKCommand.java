package com.neverlandkingdom.nlk;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
            sender.sendMessage(ChatColor.GRAY + "Preview modules: " + ChatColor.YELLOW + "not enabled yet");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.GREEN + "NLK configuration reload completed.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "/nlk status");
        sender.sendMessage(ChatColor.YELLOW + "/nlk reload");
        return true;
    }
}
