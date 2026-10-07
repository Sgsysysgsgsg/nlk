package com.neverlandkingdom.nlk;

import org.bukkit.Material;

public record FutureItem(
        String id,
        int minimumClientProtocol,
        Material carrier,
        String modelKey,
        String displayName
) {
}
