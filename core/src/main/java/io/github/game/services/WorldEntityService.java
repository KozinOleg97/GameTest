package io.github.game.services;

import com.badlogic.ashley.core.PooledEngine;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.core.world.generator.GenerationManager;
import io.github.game.core.world.generator.GeneratorPipeline;
import io.github.game.core.world.generator.GeneratorType;
import io.github.game.core.world.generator.location.LocationGeneratorConfig;
import io.github.game.core.world.generator.world.WorldGeneratorConfig;
import io.github.game.ecs.EntityFactory;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Сервис для инициализации игрового мира. Создает сущности ECS для всех гексов карты.
 */
@Singleton
public class WorldEntityService {

    private final PooledEngine engine;

    private final EntityFactory entityFactory;

    private final GenerationManager generationManager;
    private final GenerationContext generationContext;


    @Inject
    public WorldEntityService(PooledEngine engine, EntityFactory entityFactory,
                              GenerationManager generationManager,
                              GenerationContext generationContext) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.generationManager = generationManager;
        this.generationContext = generationContext;
    }


    public GenerationContext generateAll() {
        // Inject dependencies
        GenerationManager manager = this.generationManager;
        GenerationContext context = this.generationContext;

        // Create pipeline: world -> locations
        GeneratorPipeline pipeline = manager.
            createStandardPipeline
                (GeneratorType.PROCEDURAL_WORLD,
                 new WorldGeneratorConfig(5050,
                                          context.getSettings().getHexSize(),
                                          context.getSettings().getHexSize()),

                 GeneratorType.RANDOM_LOCATION,
                 new LocationGeneratorConfig(5050, context.getHexMap(), 20)
                );
        // Execute

//        HexMap map = result.getHexMap();
//        List<Entity> locations = result.getLocations();
        return pipeline.execute(context);
    }

}
