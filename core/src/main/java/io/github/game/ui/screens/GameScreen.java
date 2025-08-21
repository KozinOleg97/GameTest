package io.github.game.ui.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.game.ecs.EntityFactory;
import io.github.game.input.InputManager;
import io.github.game.input.InputMode;
import io.github.game.renderer.HexMapRenderer;
import io.github.game.services.WorldInitService;
import javax.inject.Inject;

public class GameScreen implements Screen {

    private final PooledEngine engine;
    private final EntityFactory entityFactory;
    private final WorldInitService worldInitService;
    private final HexMapRenderer hexMapRenderer;
    private final InputManager inputManager;
    private final Viewport viewport; // Добавляем Viewport

    @Inject
    public GameScreen(PooledEngine engine,
                      EntityFactory entityFactory,
                      WorldInitService worldInitService,
                      HexMapRenderer hexMapRenderer,
                      InputManager inputManager,
                      Viewport viewport) { // Добавляем Viewport в конструктор
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.worldInitService = worldInitService;
        this.hexMapRenderer = hexMapRenderer;
        this.inputManager = inputManager;
        this.viewport = viewport;
    }

    @Override
    public void show() {
        // Установка обработчика ввода
        Gdx.input.setInputProcessor(inputManager.getInputMultiplexer());

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

        // Обновление систем ECS (включая камеру)
        engine.update(delta);

        // Устанавливаем viewport и обновляем матрицы камеры
        viewport.apply();

        // Отрисовка гексовой карты (после обновления камеры)
        hexMapRenderer.render();
    }

    // Метод для смены режима ввода
    public void setInputMode(InputMode mode) {
        inputManager.setInputMode(mode);
    }

    // Остальные методы Screen
    @Override
    public void resize(int width, int height) {
        // Обработка изменения размера экрана
        viewport.update(width, height, true);
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
        hexMapRenderer.dispose();
    }
}
