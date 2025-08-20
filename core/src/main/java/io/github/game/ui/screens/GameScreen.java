package io.github.game.ui.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import io.github.game.ecs.EntityFactory;
import io.github.game.services.WorldInitService;
import javax.inject.Inject;

public class GameScreen implements Screen {

    private final PooledEngine engine;
    private final EntityFactory entityFactory;
    private final WorldInitService worldInitService;

    @Inject
    public GameScreen(PooledEngine engine,
                      EntityFactory entityFactory,
                      WorldInitService worldInitService) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.worldInitService = worldInitService;
    }

    @Override
    public void show() {
        // Инициализация мира
        worldInitService.initializeWorld();

        // Создание игрока и NPC
        entityFactory.createPlayer(100, 100);
        entityFactory.createNPC(300, 100);
        entityFactory.createNPC(300, 100);
    }

    @Override
    public void render(float delta) {
        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновление систем ECS
        engine.update(delta);
    }

    // Остальные методы Screen
    @Override
    public void resize(int width, int height) {
        // Обработка изменения размера экрана
    }

    @Override
    public void pause() {
        // Пауза игры
    }

    @Override
    public void resume() {
        // Возобновление игры
    }

    @Override
    public void hide() {
        // Скрытие экрана
    }

    @Override
    public void dispose() {
        // Очистка ресурсов
        // Освобождение ресурсов должно быть делегировано соответствующим системам/сервисам
    }
}
