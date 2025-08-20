package io.github.game.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.game.core.world.HexMap;
import io.github.game.ecs.EntityFactory;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Сервис для инициализации игрового мира. Создает сущности ECS для всех гексов карты.
 */
@Singleton
public class WorldInitService {

    private final PooledEngine engine;
    private final HexMap hexMap;
    private final EntityFactory entityFactory;

    @Inject
    public WorldInitService(PooledEngine engine, HexMap hexMap, EntityFactory entityFactory) {
        this.engine = engine;
        this.hexMap = hexMap;
        this.entityFactory = entityFactory;
    }

    /**
     * Инициализирует игровой мир, создавая сущности для всех гексов
     */
    public void initializeWorld() {
        hexMap.getHexes().values().forEach(hex -> {
            Entity hexEntity = entityFactory.createHexEntity(hex);
            engine.addEntity(hexEntity);
        });
    }
}
