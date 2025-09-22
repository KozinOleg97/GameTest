package io.github.game.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import io.github.game.ecs.EntityFactory;
import io.github.game.utils.MemoryUtils;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;

/**
 * Сервис для инициализации игрового мира. Создает сущности ECS для всех гексов карты.
 */
@Singleton
public class WorldEntityService {

    private final PooledEngine engine;
    private final HexMap hexMap;
    private final EntityFactory entityFactory;
    private final HexMapService hexMapService;

    /**
     * Инициализирует игровой мир, создавая сущности для всех гексов
     */
    private final boolean playerCreated = false;
    @Getter
    private boolean hexEntitiesCreated = false;

    @Inject
    public WorldEntityService(PooledEngine engine, HexMap hexMap, EntityFactory entityFactory,
                              HexMapService hexMapService) {
        this.engine = engine;
        this.hexMap = hexMap;
        this.entityFactory = entityFactory;
        this.hexMapService = hexMapService;
    }

}
