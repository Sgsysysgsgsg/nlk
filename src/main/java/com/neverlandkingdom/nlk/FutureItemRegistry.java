package com.neverlandkingdom.nlk;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class FutureItemRegistry {
    private final List<FutureItem> items = new ArrayList<>();

    public FutureItemRegistry() {
        // Prototype entries. The registry is intentionally client-gated.
        // More items can be added without changing the translation service.
        register(new FutureItem(
                "sulfur",
                776,
                Material.SANDSTONE,
                "sulfur",
                "Sulfur"
        ));
        register(new FutureItem(
                "potent_sulfur",
                776,
                Material.END_STONE,
                "potent_sulfur",
                "Potent Sulfur"
        ));
    }

    public void register(FutureItem item) {
        items.add(item);
    }

    public List<FutureItem> forClientProtocol(int protocol) {
        return items.stream()
                .filter(item -> protocol >= item.minimumClientProtocol())
                .toList();
    }

    public FutureItem find(String id) {
        return items.stream()
                .filter(item -> item.id().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<FutureItem> all() {
        return List.copyOf(items);
    }
}
