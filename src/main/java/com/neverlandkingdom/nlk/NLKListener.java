package com.neverlandkingdom.nlk;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class NLKListener implements Listener {
    private final NLKPlugin plugin;
    private final NewItemService newItemService;

    public NLKListener(NLKPlugin plugin, NewItemService newItemService) {
        this.plugin = plugin;
        this.newItemService = newItemService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // ViaVersion needs a moment to expose the player's negotiated protocol.
        plugin.getServer().getScheduler().runTaskLater(
                plugin,
                () -> newItemService.applyTo(event.getPlayer()),
                20L
        );
    }
}
