package io.github.game.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import io.github.game.ecs.EntityFactory;
import io.github.game.utils.MemoryUtils;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Сервис для управления игровыми сущностями (игрок, NPC и т.д.).
 */
@Singleton
public class CharacterEntityService {

    private final PooledEngine engine;
    private final EntityFactory entityFactory;
    private boolean playerCreated = false;

    /**
     * Конструктор с внедрением зависимостей
     *
     * @param engine        движок ECS для добавления сущностей
     * @param entityFactory фабрика для создания сущностей
     */
    @Inject
    public CharacterEntityService(PooledEngine engine, EntityFactory entityFactory) {
        this.engine = engine;
        this.entityFactory = entityFactory;
    }

    /**
     * Создает сущность игрока в указанных координатах. Гарантирует однократное создание игрока.
     *
     * @param x координата X для размещения игрока
     * @param y координата Y для размещения игрока
     */
    public void createPlayer(int x, int y) {
        if (!playerCreated) {
            Entity player = entityFactory.createPlayer(x, y);
            engine.addEntity(player);
            MemoryUtils.EntityAdded();

            playerCreated = true;
            Gdx.app.log("EntityManagement", "Player entity created at (" + x + ", " + y + ")");
        } else {
            Gdx.app.log("EntityManagement", "Player already exists, skipping creation");
        }
    }

    /**
     * Создает сущность NPC в указанных координатах.
     *
     * @param x координата X для размещения NPC
     * @param y координата Y для размещения NPC
     */
    public void createNPC(int x, int y) {
        Entity npc = entityFactory.createNPC(x, y);
        engine.addEntity(npc);
        MemoryUtils.EntityAdded();
        Gdx.app.log("EntityManagement", "NPC entity created at (" + x + ", " + y + ")");
    }

    /**
     * Сбрасывает состояние сервиса, позволяя заново создать сущности. Используется при перезагрузке
     * игры или смене уровня.
     */
    public void resetEntities() {
        playerCreated = false;
        Gdx.app.log("EntityManagement", "Entity management state reset");
    }
}
