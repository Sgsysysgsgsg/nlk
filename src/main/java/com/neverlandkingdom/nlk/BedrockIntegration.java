package com.neverlandkingdom.nlk;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class BedrockIntegration {
    private final NLKPlugin plugin;

    public BedrockIntegration(NLKPlugin plugin) {
        this.plugin = plugin;
    }

    public void installMapping() {
        Plugin geyser = plugin.getServer().getPluginManager().getPlugin("Geyser-Spigot");
        if (geyser == null) {
            plugin.getLogger().info("Geyser-Spigot not found; Bedrock integration will activate when Geyser is installed.");
            return;
        }

        Path mappings = geyser.getDataFolder().toPath().resolve("custom_mappings");
        try {
            Files.createDirectories(mappings);
            Path target = mappings.resolve("nlk-mappings.json");
            try (InputStream in = plugin.getResource("geyser/nlk-mappings.json")) {
                if (in == null) {
                    throw new IOException("Bundled NLK Geyser mapping is missing.");
                }
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            plugin.getLogger().info("Installed NLK Geyser mapping: " + target);
            plugin.getLogger().warning("Restart Geyser/server once after installing or changing NLK mappings.");
        } catch (IOException ex) {
            plugin.getLogger().warning("Could not install NLK Geyser mapping: " + ex.getMessage());
        }
    }
}
