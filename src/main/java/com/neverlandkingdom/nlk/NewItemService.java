package com.neverlandkingdom.nlk;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public final class NewItemService {
    private final NLKPlugin plugin;
    private final FutureItemRegistry registry;
    private final NamespacedKey futureItemKey;

    public NewItemService(NLKPlugin plugin, FutureItemRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
        this.futureItemKey = new NamespacedKey(plugin, "future_item");
    }

    public void applyTo(Player player) {
        ClientProfile profile = plugin.getClientVersionService().profile(player);
        sendResourcePack(player);

        if (!profile.newerThanServer()) return;

        if (plugin.getConfig().getBoolean("preview.give-on-join", false)) {
            for (FutureItem definition : registry.forClientProtocol(profile.protocol())) {
                give(player, createItem(definition));
            }
        }
    }

    private ItemStack createItem(FutureItem definition) {
        ItemStack item = new ItemStack(definition.carrier());
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(definition.displayName()));

        var customModelData = meta.getCustomModelDataComponent();
        customModelData.setFloats(List.of((float) definition.customModelData()));
        customModelData.setStrings(List.of(definition.modelKey()));
        meta.setCustomModelDataComponent(customModelData);

        meta.getPersistentDataContainer().set(
                futureItemKey,
                PersistentDataType.STRING,
                definition.id()
        );

        item.setItemMeta(meta);
        return item;
    }

    private void give(Player player, ItemStack item) {
        var leftovers = player.getInventory().addItem(item);
        leftovers.values().forEach(leftover ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }

    private void sendResourcePack(Player player) {
        if (!plugin.getConfig().getBoolean("resource-pack.enabled", true)) return;

        String url = plugin.getConfig().getString("resource-pack.url", "");
        if (url.isBlank()) return;

        try {
            player.addResourcePack(
                    UUID.nameUUIDFromBytes("NLK-Future-Items".getBytes(StandardCharsets.UTF_8)),
                    url,
                    null,
                    "NLK: Future Minecraft item visuals",
                    false
            );
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Could not request NLK resource pack: " + ex.getMessage());
        }
    }
}
