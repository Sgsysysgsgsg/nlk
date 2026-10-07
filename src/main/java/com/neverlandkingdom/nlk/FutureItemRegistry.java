package com.neverlandkingdom.nlk;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FutureItemRegistry {
    private final List<FutureItem> items = new ArrayList<>();

    public FutureItemRegistry() {
        // Protocol gates are evaluated automatically per connecting client.
        register(new FutureItem("sulfur", 776, Material.SANDSTONE, "sulfur", 1, "Sulfur"));
        register(new FutureItem("potent_sulfur", 776, Material.END_STONE, "potent_sulfur", 2, "Potent Sulfur"));
    }

    public void register(FutureItem item) {
        if (items.stream().anyMatch(existing -> existing.id().equalsIgnoreCase(item.id()))) {
            throw new IllegalArgumentException("Duplicate future item id: " + item.id());
        }
        items.add(item);
    }

    public List<FutureItem> forClientProtocol(int protocol) {
        return items.stream()
                .filter(item -> protocol >= item.minimumClientProtocol())
                .sorted(Comparator.comparingInt(FutureItem::minimumClientProtocol)
                        .thenComparing(FutureItem::id))
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

    public int countForClientProtocol(int protocol) {
        return (int) items.stream()
                .filter(item -> protocol >= item.minimumClientProtocol())
                .count();
    }
}
