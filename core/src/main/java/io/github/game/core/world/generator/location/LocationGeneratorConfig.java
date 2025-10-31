package io.github.game.core.world.generator.location;

import io.github.game.core.world.HexMap;
import io.github.game.core.world.generator.GeneratorConfig;

/**
 * Configuration for location generators.
 */
public class LocationGeneratorConfig extends GeneratorConfig {
    private HexMap hexMap;
    private int numberOfLocations;
    private String saveFilePath; // For saved generators

    public LocationGeneratorConfig(long seed, HexMap hexMap, int numberOfLocations) {
        super(seed);
        this.hexMap = hexMap;
        this.numberOfLocations = numberOfLocations;
    }

    public LocationGeneratorConfig(String saveFilePath) {
        super(0);
        this.saveFilePath = saveFilePath;
    }

    public HexMap getHexMap() {
        return hexMap;
    }

    public int getNumberOfLocations() {
        return numberOfLocations;
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }
}
