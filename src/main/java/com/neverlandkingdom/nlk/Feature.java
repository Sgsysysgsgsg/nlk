package com.neverlandkingdom.nlk;

public record Feature(
        String id,
        int minimumClientProtocol,
        String category,
        String description
) {
}
