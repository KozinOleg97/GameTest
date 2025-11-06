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
import io.github.game.ecs.EntityFactory;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Фабрика для создания генераторов на основе типа и конфигурации.
 */
@Singleton
public class GeneratorFactory {

    private final GenerationContext generationContext;
    private final EntityFactory entityFactory;

    @Inject
    public GeneratorFactory(GenerationContext generationContext, EntityFactory entityFactory) {
        this.generationContext = generationContext;
        this.entityFactory = entityFactory;
    }

    public WorldGenerator createWorldGenerator(GeneratorType type, WorldGeneratorConfig config) {
        return switch (type) {
            case PROCEDURAL_WORLD -> new ProceduralWorldGenerator(
                config.getWidth(), config.getHeight(), config.getSeed(),
                generationContext);
            case RECTANGULAR_WORLD -> new RectangularWorldGenerator(
                config.getWidth(), config.getHeight(), config.getSeed(),
                generationContext);
            case SAVED_WORLD -> new SavedWorldGenerator(config.getSaveFilePath(), generationContext);
            default -> throw new IllegalArgumentException("Unknown world generator type: " + type);
        };
    }

    public LocationGenerator createLocationGenerator(GeneratorType type, LocationGeneratorConfig config) {
        return switch (type) {
            case RANDOM_LOCATION -> new RandomLocationGenerator(
                config.getNumberOfLocations(), config.getSeed(), generationContext,
                entityFactory);
            case SAVED_LOCATION -> new SavedLocationGenerator(config.getSaveFilePath(), generationContext);
            default -> throw new IllegalArgumentException("Unknown location generator type: " + type);
        };
    }
}
