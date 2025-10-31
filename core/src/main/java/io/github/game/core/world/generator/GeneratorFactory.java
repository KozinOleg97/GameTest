package io.github.game.core.world.generator;

import io.github.game.core.world.generator.location.LocationGenerator;
import io.github.game.core.world.generator.location.LocationGeneratorConfig;
import io.github.game.core.world.generator.location.RandomLocationGenerator;
import io.github.game.core.world.generator.location.SavedLocationGenerator;
import io.github.game.core.world.generator.world.ProceduralWorldGenerator;
import io.github.game.core.world.generator.world.RectangularWorldGenerator;
import io.github.game.core.world.generator.world.SavedWorldGenerator;
import io.github.game.core.world.generator.world.WorldGenerator;
import io.github.game.core.world.generator.world.WorldGeneratorConfig;

/**
 * Фабрика для создания генераторов на основе типа и конфигурации.
 */
public class GeneratorFactory {

    public static WorldGenerator createWorldGenerator(GeneratorType type, WorldGeneratorConfig config) {
        return switch (type) {
            case PROCEDURAL_WORLD -> new ProceduralWorldGenerator(config.getWidth(), config.getHeight(), config.getSeed());
            case RECTANGULAR_WORLD -> new RectangularWorldGenerator(config.getWidth(), config.getHeight(), config.getSeed());
            case SAVED_WORLD -> new SavedWorldGenerator(config.getSaveFilePath()); // TODO: реализовать
            default -> throw new IllegalArgumentException("Unknown world generator type: " + type);
        };
    }

    public static LocationGenerator createLocationGenerator(GeneratorType type, LocationGeneratorConfig config) {
        return switch (type) {
            case RANDOM_LOCATION -> new RandomLocationGenerator(config.getHexMap(),  config.getNumberOfLocations(), config.getSeed());
            case SAVED_LOCATION -> new SavedLocationGenerator(config.getSaveFilePath()); // TODO: реализовать
            default -> throw new IllegalArgumentException("Unknown location generator type: " + type);
        };
    }

    // TODO: createItemGenerator, createNpcGenerator...
}
