package io.github.game.services;

import com.badlogic.ashley.core.PooledEngine;
import io.github.game.core.world.HexMap;
import io.github.game.ecs.EntityFactory;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Сервис для инициализации игрового мира. Создает сущности ECS для всех гексов карты.
 */
@Singleton
public class WorldEntityService {

    private final PooledEngine engine;
    private final HexMap hexMap;
    private final EntityFactory entityFactory;


    @Inject
    public WorldEntityService(PooledEngine engine,
                              HexMap hexMap,
                              EntityFactory entityFactory
    ) {
        this.engine = engine;
        this.hexMap = hexMap;
        this.entityFactory = entityFactory;

    }

}
