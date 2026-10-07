package com.neverlandkingdom.nlk;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class NLKPlugin extends JavaPlugin {
    private ClientVersionService clientVersionService;
    private FeatureRegistry featureRegistry;
    private NewItemService newItemService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        FileConfiguration config = getConfig();
        int serverProtocol = config.getInt("server-protocol", 774);

        clientVersionService = new ClientVersionService(serverProtocol);
        featureRegistry = new FeatureRegistry();
        newItemService = new NewItemService(this);

        if (getServer().getPluginManager().getPlugin("ViaVersion") == null) {
            getLogger().warning("ViaVersion is not installed. NLK client-version detection will be unavailable.");
        } else {
            getLogger().info("Automatic client-version detection enabled.");
        }

        getCommand("nlk").setExecutor(new NLKCommand(this));
        getServer().getPluginManager().registerEvents(
                new NLKListener(this, newItemService),
                this
        );

        getLogger().info("NLK Compatibility enabled.");
        getLogger().info("Backend: " + getServer().getVersion());
        getLogger().info("Automatic future-item preview: enabled.");
    }

    public ClientVersionService getClientVersionService() {
        return clientVersionService;
    }

    public FeatureRegistry getFeatureRegistry() {
        return featureRegistry;
    }
}
