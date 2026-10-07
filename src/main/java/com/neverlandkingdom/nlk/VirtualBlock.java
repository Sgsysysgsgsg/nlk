package com.neverlandkingdom.nlk;

import org.bukkit.Material;

public record VirtualBlock(
        String id,
        int minimumClientProtocol,
        Material carrier,
        String modelKey,
        int customModelData,
        String displayName
) {
}
