package com.neverlandkingdom.nlk;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            newItemService.normalize(event.getCurrentItem());
        }
        if (event.getCursor() != null) {
            newItemService.normalize(event.getCursor());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("virtual-items.prevent-vanilla-use", true)) {
            return;
        }

        FutureItem item = newItemService.identify(event.getItem());
        if (item == null) return;

        /*
         * The 1.21.11 backend only knows the carrier item (for example
         * SANDSTONE). Letting vanilla process that carrier can place/consume it
         * and make the newer item appear to disappear.
         *
         * Cancel the carrier action until a dedicated NLK behavior handler exists.
         * The item itself remains in the player's inventory.
         */
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        if (!plugin.getConfig().getBoolean("virtual-items.prevent-vanilla-use", true)) {
            return;
        }

        if (newItemService.identify(event.getItem()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!plugin.getConfig().getBoolean("virtual-items.prevent-vanilla-use", true)) {
            return;
        }

        if (newItemService.identify(event.getItemInHand()) != null) {
            event.setCancelled(true);
        }
    }
}
