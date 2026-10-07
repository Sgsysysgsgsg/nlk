package com.neverlandkingdom.nlk;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class VirtualBlockRegistry {
    private final List<VirtualBlock> blocks = new ArrayList<>();

    public VirtualBlockRegistry() {
        register(new VirtualBlock(
                "sulfur_block",
                776,
                Material.SANDSTONE,
                "sulfur_block",
                1001,
                "Sulfur Block"
        ));
        register(new VirtualBlock(
                "cinnabar_block",
                776,
                Material.RED_SANDSTONE,
                "cinnabar_block",
                1002,
                "Cinnabar Block"
        ));
    }

    public void register(VirtualBlock block) {
        if (blocks.stream().anyMatch(existing -> existing.id().equalsIgnoreCase(block.id()))) {
            throw new IllegalArgumentException("Duplicate virtual block id: " + block.id());
        }
        blocks.add(block);
    }

    public List<VirtualBlock> forClientProtocol(int protocol) {
        return blocks.stream()
                .filter(block -> protocol >= block.minimumClientProtocol())
                .toList();
    }

    public VirtualBlock find(String id) {
        return blocks.stream()
                .filter(block -> block.id().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<VirtualBlock> all() {
        return List.copyOf(blocks);
    }
}
