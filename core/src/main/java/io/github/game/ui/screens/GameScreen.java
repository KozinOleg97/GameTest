package io.github.game.ui.screens;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.game.ecs.EntityFactory;
import io.github.game.input.InputManager;
import io.github.game.input.InputMode;
import io.github.game.monitoring.PerformanceLogger;
import io.github.game.monitoring.PerformanceMonitor;
import io.github.game.renderer.HexMapRenderer;
import io.github.game.services.CharacterEntityService;
import io.github.game.services.WorldEntityService;
import io.github.game.utils.MemoryUtils;
import javax.inject.Inject;

public class GameScreen implements Screen {

    private final PooledEngine engine;
    private final EntityFactory entityFactory;
    private final WorldEntityService worldEntityService;
    private final HexMapRenderer hexMapRenderer;
    private final InputManager inputManager;
    private final Viewport viewport;
    private final CharacterEntityService characterEntityService;
    private final PerformanceMonitor performanceMonitor;

    @Inject
    public GameScreen(PooledEngine engine,
                      EntityFactory entityFactory,
                      WorldEntityService worldEntityService,
                      HexMapRenderer hexMapRenderer,
                      InputManager inputManager,
                      Viewport viewport,
                      CharacterEntityService characterEntityService,
                      PerformanceMonitor performanceMonitor                      ) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.worldEntityService = worldEntityService;
        this.hexMapRenderer = hexMapRenderer;
        this.inputManager = inputManager;
        this.viewport = viewport;
        this.characterEntityService = characterEntityService;
        this.performanceMonitor = performanceMonitor;
    }

    @Override
    public void show() {
        // Установка обработчика ввода
        Gdx.input.setInputProcessor(inputManager.getInputMultiplexer());

        // Инициализация гексов (создание ECS-сущностей)
        worldEntityService.initializeHexEntities();

        // Создание игрока и NPC через отдельный сервис
        characterEntityService.createPlayer(100, 100);

        for (int i = 0; i < 1000; i++) {
            characterEntityService.createNPC(random.nextInt(1000), random.nextInt(1000));
        }

        MemoryUtils.logMemoryUsage("GameScreen shown");
    }

    @Override
    public void render(float delta) {
        // Обновляем мониторинг производительности
        performanceMonitor.update(delta);

        // Начинаем измерение времени рендеринга
        performanceMonitor.startEvent("frame_render");

        //----------------Рендер-----------------------------------

        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Устанавливаем viewport и обновляем матрицы камеры
        viewport.apply();

        performanceMonitor.startEvent("hex_render");
        hexMapRenderer.render(); // Отрисовка гексовой карты (сначала фон)
        performanceMonitor.endEvent("hex_render");


        performanceMonitor.startEvent("ecs_update");
        engine.update(delta);// Обновление систем ECS (игрок и NPC поверх гексов)
        performanceMonitor.endEvent("ecs_update");

        //----------------------------------------------------------

        // Завершаем измерение времени рендеринга кадра
        float frameTime = performanceMonitor.endEvent("frame_render");
        performanceMonitor.addCustomMetric("Frame Time", String.format("%.2fms", frameTime));

        // Рендерим статистику производительности поверх всего
        performanceMonitor.render();

        if (Gdx.graphics.getFrameId() % 60 == 0) { // Каждую секунду при 60 FPS
            PerformanceLogger.logMemoryUsage("During rendering");

            if (MemoryUtils.isMemoryCritical()) {
                Gdx.app.error("Memory", "CRITICAL: Memory is running low!");
            }
        }
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

    // Метод для смены режима ввода
    public void setInputMode(InputMode mode) {
        inputManager.setInputMode(mode);
    }
}
