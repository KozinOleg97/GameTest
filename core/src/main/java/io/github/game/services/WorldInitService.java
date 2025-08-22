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

/**
 * Сервис для инициализации игрового мира. Создает сущности ECS для всех гексов карты.
 */
@Singleton
public class WorldInitService {

    private final PooledEngine engine;
    private final HexMap hexMap;
    private final EntityFactory entityFactory;
    private boolean initialized = false;
    /**
     * Инициализирует игровой мир, создавая сущности для всех гексов
     */
    private boolean playerCreated = false;
    private boolean npcsCreated = false;
    @Inject
    public WorldInitService(PooledEngine engine, HexMap hexMap, EntityFactory entityFactory) {
        this.engine = engine;
        this.hexMap = hexMap;
        this.entityFactory = entityFactory;
    }

    public void initializeWorld() {
        if (initialized) {
            return;
        }

        MemoryUtils.logMemoryUsage("Before world initialization");
        Gdx.app.log("Init", "Initializing world with " + hexMap.getHexes().size() + " hexes");

        hexMap.getHexes().values().forEach(entityFactory::createHexEntity);

        if (!playerCreated) {
            entityFactory.createPlayer(100, 100);
            playerCreated = true;
        }

        if (!npcsCreated) {
            entityFactory.createNPC(200, 200);
            npcsCreated = true;
        }

        initialized = true;
        Gdx.app.log("Init", "World initialization completed");
        MemoryUtils.logMemoryUsage("After world initialization");
    }

    /**
     * Очищает все сущности гексов из движка
     */
    public void clearHexEntities() {
        Family family = Family.all(HexComponent.class).get();
        for (Entity entity : engine.getEntitiesFor(family)) {
            engine.removeEntity(entity);
        }
        initialized = false;
    }

    /**
     * Сбрасывает состояние инициализации
     */
    public void reset() {
        initialized = false;
    }

    /**
     * Проверяет, был ли мир уже инициализирован
     */
    public boolean isInitialized() {
        return initialized;
    }
}
