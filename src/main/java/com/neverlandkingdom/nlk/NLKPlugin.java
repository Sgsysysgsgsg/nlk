package com.neverlandkingdom.nlk;

import org.bukkit.plugin.java.JavaPlugin;

public final class NLKPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("nlk").setExecutor(new NLKCommand(this));
        getLogger().info("NLK Compatibility enabled.");
        getLogger().info("Server: " + getServer().getVersion());
        getLogger().info("Preview modules are intentionally disabled until implemented.");
    }
}
