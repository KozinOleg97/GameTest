package io.github.game.core.world.generator.world;

import io.github.game.core.world.generator.GeneratorConfig;

/**
 * Configuration for world generators.
 */
public class WorldGeneratorConfig extends GeneratorConfig {
    private int width;
    private int height;
    private String saveFilePath; // For saved generators

    public WorldGeneratorConfig(long seed, int width, int height) {
        super(seed);
        this.width = width;
        this.height = height;
    }

    public WorldGeneratorConfig(String saveFilePath) {
        super(0); // Seed not used for saves
        this.saveFilePath = saveFilePath;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }
}
