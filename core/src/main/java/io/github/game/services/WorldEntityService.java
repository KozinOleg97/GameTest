package io.github.game.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.core.world.generator.GeneratorFactory;
import io.github.game.core.world.generator.GeneratorPipeline;
import io.github.game.core.world.generator.GeneratorType;
import io.github.game.core.world.generator.PipelineBuilder;
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


    private final GenerationContext generationContext;
    private final GeneratorFactory generatorFactory;

    @Inject
    public WorldEntityService(PooledEngine engine, EntityFactory entityFactory,
                              GenerationContext generationContext, GeneratorFactory generatorFactory) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.generationContext = generationContext;
        this.generatorFactory = generatorFactory;
    }


    public void generateAll() {
        GenerationContext context = this.generationContext;

        // Используем инжектированную фабрику
        GeneratorPipeline pipeline = new PipelineBuilder(generatorFactory)
            .withWorld(GeneratorType.PROCEDURAL_WORLD,
                       new WorldGeneratorConfig(5050,
                                                context.getSettings().getHexSize(),
                                                context.getSettings().getHexSize()))
            .withLocations(GeneratorType.RANDOM_LOCATION,
                           new LocationGeneratorConfig(5050,
                                                       context.getHexMap(),
                                                       50))
            .build();

        GenerationContext resultContext = pipeline.execute(generationContext);

        pipeline.clear();

        for (Entity entity : resultContext.getLocations()) {
            engine.addEntity(entity);
        }
    }

}
