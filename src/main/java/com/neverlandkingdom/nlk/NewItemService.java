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

        // ViaVersion/ViaBackwards can expose newer client items as an older carrier
        // item with custom model data. Normalize the inventory after the protocol
        // negotiation is ready so NLK can identify those items reliably.
        normalizeInventory(player);

        if (!profile.newerThanServer()) return;

        if (plugin.getConfig().getBoolean("preview.give-on-join", false)) {
            for (FutureItem definition : registry.forClientProtocol(profile.protocol())) {
                give(player, createItem(definition));
            }
        }
    }

    /**
     * Returns the NLK definition represented by this ItemStack.
     *
     * Items created by NLK carry a PDC marker. Items translated by
     * ViaBackwards/Geyser may not, so we also recognize the carrier +
     * custom-model-data representation used on the wire.
     */
    public FutureItem identify(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        String storedId = meta.getPersistentDataContainer().get(
                futureItemKey,
                PersistentDataType.STRING
        );

        if (storedId != null) {
            FutureItem stored = registry.find(storedId);
            if (stored != null) return stored;
        }

        var customModelData = meta.getCustomModelDataComponent();
        List<Float> floats = customModelData.getFloats();
        List<String> strings = customModelData.getStrings();

        for (FutureItem definition : registry.all()) {
            if (item.getType() != definition.carrier()) continue;

            boolean numberMatch = floats.stream()
                    .anyMatch(value -> Float.compare(value, definition.customModelData()) == 0);
            boolean modelMatch = strings.stream()
                    .anyMatch(value -> value.equalsIgnoreCase(definition.modelKey()));

            if (numberMatch || modelMatch) {
                mark(item, definition);
                return definition;
            }
        }

        return null;
    }

    /**
     * Adds the stable NLK identity to an item translated by ViaBackwards/Geyser.
     * This is deliberately server-side metadata; it does not change the visible
     * carrier or the resource-pack model.
     */
    public boolean normalize(ItemStack item) {
        return identify(item) != null;
    }

    public void normalizeInventory(Player player) {
        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null || item.getType().isAir()) continue;

            FutureItem definition = identify(item);
            if (definition != null) {
                player.getInventory().setItem(slot, item);
            }
        }
    }

    private void mark(ItemStack item, FutureItem definition) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(
                futureItemKey,
                PersistentDataType.STRING,
                definition.id()
        );
        item.setItemMeta(meta);
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
