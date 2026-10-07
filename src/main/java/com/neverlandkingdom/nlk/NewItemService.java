package com.neverlandkingdom.nlk;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
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
    private final NamespacedKey futureItemKey;

    public NewItemService(NLKPlugin plugin) {
        this.plugin = plugin;
        this.futureItemKey = new NamespacedKey(plugin, "future_item");
    }

    public void applyTo(Player player) {
        ClientProfile profile = plugin.getClientVersionService().profile(player);

        if (!profile.newerThanServer()) {
            return;
        }

        sendResourcePack(player);

        // The carrier item is a real 1.21.11 item. The resource pack uses
        // its component data to render the newer item's model/icon.
        // Nothing is injected into the old server's vanilla registry.
        if (plugin.getConfig().getBoolean("preview.give-on-join", false)) {
            givePreviewItems(player);
        }
    }

    private void givePreviewItems(Player player) {
        give(player, futureItem(Material.SANDSTONE, "sulfur", "Sulfur"));
        give(player, futureItem(Material.END_STONE, "potent_sulfur", "Potent Sulfur"));
    }

    private ItemStack futureItem(Material carrier, String modelKey, String displayName) {
        ItemStack item = new ItemStack(carrier);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(displayName));

        var customModelData = meta.getCustomModelDataComponent();
        customModelData.setStrings(List.of(modelKey));
        meta.setCustomModelDataComponent(customModelData);

        meta.getPersistentDataContainer().set(
                futureItemKey,
                PersistentDataType.STRING,
                modelKey
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
        if (!plugin.getConfig().getBoolean("resource-pack.enabled", true)) {
            return;
        }

        String url = plugin.getConfig().getString("resource-pack.url", "");
        if (url.isBlank()) {
            return;
        }

        try {
            player.addResourcePack(
                    UUID.nameUUIDFromBytes(
                            "NLK-ViaBackwards-Plus".getBytes(StandardCharsets.UTF_8)
                    ),
                    url,
                    null,
                    Component.text("NLK: Future Minecraft item visuals"),
                    false
            );
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Could not request NLK resource pack: " + ex.getMessage());
        }
    }
}
