package io.github.game.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import io.github.game.core.world.HexMap;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.components.world.HexComponent;
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

    /**
     * Создает ECS-сущности для всех гексов на карте. Гарантирует однократное создание сущностей
     * (идемпотентность).
     */
    public void initializeHexEntities() {
        if (hexEntitiesCreated) {
            Gdx.app.log("WorldInit", "Hex entities already created, skipping");
            return;
        }

        MemoryUtils.logMemoryUsage("Before hex entities creation");
        Gdx.app.log("WorldInit", "Creating entities for " + hexMap.size() + " hexes");

        // Создаем сущности для всех гексов на карте
        hexMap.getHexes().values().forEach(hex -> {
            Entity hexEntity = entityFactory.createHexEntity(hex);
            engine.addEntity(hexEntity);

            // Регистрируем сущность в HexMapService
            hexMapService.registerHexEntity(hex.getCoordinates(), hexEntity);
        });
        hexEntitiesCreated = true;

        Gdx.app.log("WorldInit", "Hex entities creation completed");
        MemoryUtils.logMemoryUsage("After hex entities creation");
    }

    /**
     * Очищает все сущности гексов из движка
     */
    public void clearHexEntities() {
        Family family = Family.all(HexComponent.class).get();
        for (Entity entity : engine.getEntitiesFor(family)) {
            engine.removeEntity(entity);
        }
        hexEntitiesCreated = false;
        Gdx.app.log("WorldInit", "Hex entities cleared");
    }

    /**
     * Сбрасывает состояние инициализации
     */
    public void reset() {
        hexEntitiesCreated = false;
    }

}
