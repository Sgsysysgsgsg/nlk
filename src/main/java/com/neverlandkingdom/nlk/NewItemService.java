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
    private final NamespacedKey seededKey;

    public NewItemService(NLKPlugin plugin) {
        this.plugin = plugin;
        this.seededKey = new NamespacedKey(plugin, "future_items_seeded");
    }

    public void applyTo(Player player) {
        ClientProfile profile = plugin.getClientVersionService().profile(player);

        if (!profile.newerThanServer()) {
            return;
        }

        sendResourcePack(player);

        if (player.getPersistentDataContainer().has(seededKey, PersistentDataType.BYTE)) {
            return;
        }

        addIfPossible(player, futureItem(Material.SANDSTONE, "sulfur", "Sulfur"));
        addIfPossible(player, futureItem(Material.END_STONE, "potent_sulfur", "Potent Sulfur"));

        player.getPersistentDataContainer().set(seededKey, PersistentDataType.BYTE, (byte) 1);
    }

    private ItemStack futureItem(Material carrier, String modelKey, String displayName) {
        ItemStack item = new ItemStack(carrier);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(displayName));

        var customModelData = meta.getCustomModelDataComponent();
        customModelData.setStrings(List.of(modelKey));
        meta.setCustomModelDataComponent(customModelData);

        item.setItemMeta(meta);
        return item;
    }

    private void addIfPossible(Player player, ItemStack item) {
        var leftovers = player.getInventory().addItem(item);
        leftovers.values().forEach(leftover ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }

    private void sendResourcePack(Player player) {
        if (!plugin.getConfig().getBoolean("resource-pack.enabled", true)) {
            return;
        }

        String url = plugin.getConfig().getString(
                "resource-pack.url",
                "https://www.curseforge.com/minecraft/texture-packs/vbp/download/8954075"
        );

        if (url == null || url.isBlank()) {
            return;
        }

        try {
            player.addResourcePack(
                    UUID.nameUUIDFromBytes(
                            "NLK-ViaBackwards-Plus".getBytes(StandardCharsets.UTF_8)
                    ),
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
