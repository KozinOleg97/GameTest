package io.github.game.ui.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Screen;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.systems.NPCMovementSystem;
import io.github.game.ecs.systems.PlayerInputSystem;
import io.github.game.ecs.systems.PlayerMovementSystem;
import io.github.game.ecs.systems.RenderingSystem;
import javax.inject.Inject;

public class GameScreen implements Screen {

    private final PooledEngine engine;
    private final PlayerInputSystem playerInputSystem;
    private final PlayerMovementSystem playerMovementSystem;
    private final RenderingSystem renderingSystem;
    private final EntityFactory entityFactory;
    private final NPCMovementSystem npcMovementSystem;

    @Inject
    public GameScreen(PooledEngine engine, PlayerInputSystem playerInputSystem,
                      PlayerMovementSystem playerMovementSystem, RenderingSystem renderingSystem,
                      EntityFactory entityFactory, NPCMovementSystem npcMovementSystem) {
        this.engine = engine;
        this.playerInputSystem = playerInputSystem;
        this.playerMovementSystem = playerMovementSystem;
        this.renderingSystem = renderingSystem;
        this.entityFactory = entityFactory;
        this.npcMovementSystem = npcMovementSystem;


        // Добавление систем в движок ECS
        engine.addSystem(playerInputSystem);
        engine.addSystem(playerMovementSystem);
        engine.addSystem(renderingSystem);
        engine.addSystem(npcMovementSystem);


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
