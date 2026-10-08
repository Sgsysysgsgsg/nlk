package com.neverlandkingdom.nlk;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FutureItemRegistry {
    private final List<FutureItem> items = new ArrayList<>();

    public FutureItemRegistry() {
        // 26.2 Chaos Cubed compatibility items.
        register(new FutureItem("sulfur", 776, Material.SANDSTONE, "sulfur", 1, "Sulfur", true));
        register(new FutureItem("potent_sulfur", 776, Material.END_STONE, "potent_sulfur", 2, "Potent Sulfur", true));
        register(new FutureItem("cinnabar", 776, Material.RED_SANDSTONE, "cinnabar", 3, "Cinnabar", true));
        register(new FutureItem("sulfur_spike", 776, Material.POINTED_DRIPSTONE, "sulfur_spike", 4, "Sulfur Spike", true));
        register(new FutureItem("chiseled_sulfur", 776, Material.CHISELED_SANDSTONE, "chiseled_sulfur", 5, "Chiseled Sulfur", true));
        register(new FutureItem("chiseled_cinnabar", 776, Material.CHISELED_RED_SANDSTONE, "chiseled_cinnabar", 6, "Chiseled Cinnabar", true));
        register(new FutureItem("sulfur_bricks", 776, Material.STONE_BRICKS, "sulfur_bricks", 7, "Sulfur Bricks", true));
        register(new FutureItem("cinnabar_bricks", 776, Material.BRICK, "cinnabar_bricks", 8, "Cinnabar Bricks", true));
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
