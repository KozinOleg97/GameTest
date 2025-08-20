package io.github.game.ui.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Screen;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.systems.MovementSystem;
import io.github.game.ecs.systems.NPCLogicSystem;
import io.github.game.ecs.systems.PlayerInputSystem;
import io.github.game.ecs.systems.RenderingSystem;
import javax.inject.Inject;

public class GameScreen implements Screen {

    private final PooledEngine engine;
    private final PlayerInputSystem playerInputSystem;
    private final MovementSystem movementSystem;
    private final RenderingSystem renderingSystem;
    private final EntityFactory entityFactory;
    private final NPCLogicSystem npcLogicSystem;

    @Inject
    public GameScreen(PooledEngine engine, PlayerInputSystem playerInputSystem,
                      MovementSystem movementSystem, RenderingSystem renderingSystem,
                      EntityFactory entityFactory, NPCLogicSystem npcLogicSystem) {
        this.engine = engine;
        this.playerInputSystem = playerInputSystem;
        this.movementSystem = movementSystem;
        this.renderingSystem = renderingSystem;
        this.entityFactory = entityFactory;
        this.npcLogicSystem = npcLogicSystem;

        // Добавление систем в движок ECS
        engine.addSystem(playerInputSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(npcLogicSystem);
        engine.addSystem(renderingSystem);

        // Добавление сущностей в движок ECS
        entityFactory.createPlayer(100, 100);
        entityFactory.createNPC(300, 100);
        entityFactory.createNPC(300, 100);

    }

    @Override
    public void render(float delta) {
        engine.update(delta); // Обновление систем ECS
    }

    // Остальные методы Screen (не используются)

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (renderingSystem != null) {
            renderingSystem.dispose();
        }
    }
}
