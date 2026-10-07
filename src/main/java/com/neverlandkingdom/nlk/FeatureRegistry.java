package com.neverlandkingdom.nlk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FeatureRegistry {
    private final List<Feature> features = new ArrayList<>();

    public void register(Feature feature) {
        features.add(feature);
    }

    public List<Feature> forClientProtocol(int clientProtocol) {
        return features.stream()
                .filter(feature -> clientProtocol >= feature.minimumClientProtocol())
                .sorted(Comparator.comparingInt(Feature::minimumClientProtocol))
                .toList();
    }

    public List<Feature> all() {
        return List.copyOf(features);
    }
}
