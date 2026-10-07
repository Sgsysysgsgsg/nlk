package com.neverlandkingdom.nlk;

import org.bukkit.plugin.java.JavaPlugin;

public final class NLKPlugin extends JavaPlugin {
    private ClientVersionService clientVersionService;

    @Override
    public void onEnable() {
        clientVersionService = new ClientVersionService();

        if (getServer().getPluginManager().getPlugin("ViaVersion") == null) {
            getLogger().warning("ViaVersion is not installed. NLK client-version detection will be unavailable.");
        }

        getCommand("nlk").setExecutor(new NLKCommand(this));
        getLogger().info("NLK Compatibility enabled.");
        getLogger().info("Backend: " + getServer().getVersion());
    }

    public ClientVersionService getClientVersionService() {
        return clientVersionService;
    }
}
