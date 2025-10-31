package io.github.game.core.world.generator.world;

import io.github.game.core.world.generator.GenerationContext;
import io.github.game.core.world.generator.GeneratorStep;

/**
 * Step for generating the world map.
 */
public class WorldGenerationStep implements GeneratorStep {

    private final WorldGenerator worldGenerator;

    public WorldGenerationStep(WorldGenerator worldGenerator) {
        this.worldGenerator = worldGenerator;
    }

    @Override
    public void execute(GenerationContext context) {
        context.setHexMap(worldGenerator.generateWorld());
    }
}
