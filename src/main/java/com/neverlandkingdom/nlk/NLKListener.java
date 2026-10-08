package com.neverlandkingdom.nlk;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.block.Action;

public final class NLKListener implements Listener {
    private final NLKPlugin plugin;
    private final NewItemService newItemService;

    public NLKListener(NLKPlugin plugin, NewItemService newItemService) {
        this.plugin = plugin;
        this.newItemService = newItemService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(
                plugin,
                () -> newItemService.applyTo(event.getPlayer()),
                20L
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) newItemService.normalize(event.getCurrentItem());
        if (event.getCursor() != null) newItemService.normalize(event.getCursor());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        FutureItem item = newItemService.identify(event.getItem());
        if (item == null) return;

        // A future block is represented by an old server-side carrier.
        // Translate placement back into the carrier block instead of letting
        // Paper/ViaVersion consume the virtual item as an incompatible action.
        if (item.placeable()
                && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block target = event.getClickedBlock().getRelative(event.getBlockFace());

                if (target.getType().isAir() || target.isReplaceable()) {
                    target.setType(item.carrier(), false);

                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        var hand = event.getHand();
                        if (hand != null) {
                            var stack = event.getPlayer().getInventory().getItem(hand);
                            if (stack != null && stack.getAmount() > 0) {
                                stack.setAmount(stack.getAmount() - 1);
                            }
                        }
                    }

                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (plugin.getConfig().getBoolean("virtual-items.prevent-vanilla-use", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        if (plugin.getConfig().getBoolean("virtual-items.prevent-vanilla-use", true)
                && newItemService.identify(event.getItem()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.getConfig().getBoolean("virtual-items.prevent-vanilla-use", true)
                && newItemService.identify(event.getItemInHand()) != null) {
            event.setCancelled(true);
        }
    }
}
