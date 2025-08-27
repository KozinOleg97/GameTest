package io.github.game.monitoring;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.game.settings.GraphicsSettings;
import io.github.game.utils.MemoryUtils;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Сервис для мониторинга производительности игры. Отображает FPS, использование памяти и другие
 * метрики для отладки. Автоматически обновляет показатели с заданным интервалом.
 */
@Singleton
public class PerformanceMonitor implements Disposable {

    private final GraphicsSettings graphicsSettings;
    private final SpriteBatch uiSpriteBatch; // Отдельный SpriteBatch для UI
    private final Viewport uiViewport; // Отдельный Viewport для UI


    private final BitmapFont font;
    private final Array<Float> frameTimes;
    private final int updateInterval = 60; // Обновлять статистику каждые 60 кадров
    private final Map<String, String> customMetrics = new HashMap<>();
    private final Map<String, Long> events = new HashMap<>();
    private final Map<String, Float> eventDurations = new HashMap<>();
    // Статистика производительности
    private int fps;
    private long frameCounter;
    private long lastUpdateTime;
    // Расширенные метрики
    private boolean extendedStats = false;

    @Inject
    public PerformanceMonitor(GraphicsSettings graphicsSettings,
                              @Named("uiSpriteBatch") SpriteBatch uiSpriteBatch,
                              @Named("uiViewport") Viewport uiViewport,
                              BitmapFont font) {
        this.graphicsSettings = graphicsSettings;
        this.uiSpriteBatch = uiSpriteBatch;
        this.uiViewport = uiViewport;
        this.font = font;

        this.frameTimes = new Array<>();
        this.lastUpdateTime = System.currentTimeMillis();

        Gdx.app.log("PerformanceMonitor", "Performance monitor initialized");
    }

    /**
     * Обновляет статистику производительности. Должен вызываться каждый кадр в методе render.
     *
     * @param deltaTime время since последнего кадра
     */
    public void update(float deltaTime) {
        frameCounter++;

        // Добавляем время кадра для расчета среднего FPS
        frameTimes.add(deltaTime);

        // Обновляем статистику с заданным интервалом
        if (frameCounter % updateInterval == 0) {
            updatePerformanceStats();
        }
    }

    /**
     * Рендерит статистику производительности на экран. Должен вызываться после рендеринга основного
     * контента.
     */
    public void render() {
        if (!graphicsSettings.isDebugMode()) {
            return;
        }

        // Устанавливаем UI Viewport и обновляем его
        uiViewport.apply();
        uiSpriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);

        uiSpriteBatch.begin();

        // Отображаем базовую статистику
        int yPosition = (int) (uiViewport.getWorldHeight() - 20);
        int lineHeight = 20;

        font.draw(uiSpriteBatch, "FPS: " + fps, 20, yPosition);
        yPosition -= lineHeight;

        font.draw(uiSpriteBatch, "Memory: " + MemoryUtils.getUsedMemoryMB() + "MB / " +
                                 MemoryUtils.getTotalMemoryMB() + "MB (" +
                                 MemoryUtils.getMemoryUsagePercent() + "%)",
                  20, yPosition);
        yPosition -= lineHeight;

        font.draw(uiSpriteBatch, "Max Memory: " + MemoryUtils.getMaxMemoryMB() + "MB", 20,
                  yPosition);
        yPosition -= lineHeight;

        // Дополнительная статистика
        font.draw(uiSpriteBatch, "Frame Time: " + getAverageFrameTime() + "ms", 20, yPosition);
        yPosition -= lineHeight;

        // Расширенная статистика (если включена)
        if (extendedStats) {
            font.draw(uiSpriteBatch, "Entities: " + getEntityCount(), 20, yPosition);
            yPosition -= lineHeight;

            // Кастомные метрики
            for (Map.Entry<String, String> entry : customMetrics.entrySet()) {
                font.draw(uiSpriteBatch, entry.getKey() + ": " + entry.getValue(), 20, yPosition);
                yPosition -= lineHeight;
            }

            // Длительности событий
            for (Map.Entry<String, Float> entry : eventDurations.entrySet()) {
                font.draw(uiSpriteBatch,
                          entry.getKey() + ": " + String.format("%.2fms", entry.getValue()),
                          uiViewport.getWorldWidth() - 200, yPosition);
                yPosition -= lineHeight;
            }
        }

        uiSpriteBatch.end();
    }

    /**
     * Включает или выключает расширенную статистику.
     *
     * @param extended true для включения расширенной статистики
     */
    public void setExtendedStats(boolean extended) {
        this.extendedStats = extended;
        Gdx.app.log("PerformanceMonitor", "Extended stats: " + extended);
    }

    /**
     * Добавляет кастомную метрику для мониторинга.
     *
     * @param name  название метрики
     * @param value значение метрики
     */
    public void addCustomMetric(String name, String value) {
        customMetrics.put(name, value);
    }

    /**
     * Удаляет кастомную метрику.
     *
     * @param name название метрики
     */
    public void removeCustomMetric(String name) {
        customMetrics.remove(name);
    }

    /**
     * Регистрирует начало события для профилирования.
     *
     * @param eventName название события
     */
    public void startEvent(String eventName) {
        long startTime = System.nanoTime();
        events.put(eventName, startTime);
    }

    /**
     * Завершает событие и вычисляет его длительность.
     *
     * @param eventName название события
     * @return длительность события в миллисекундах
     */
    public float endEvent(String eventName) {
        Long startTime = events.get(eventName);
        if (startTime != null) {
            long endTime = System.nanoTime();
            float duration = (endTime - startTime) / 1_000_000f; // Convert to milliseconds
            events.remove(eventName);
            eventDurations.put(eventName, duration);
            return duration;
        }
        return 0;
    }

    /**
     * Возвращает текущее значение FPS.
     *
     * @return текущий FPS
     */
    public int getFps() {
        return fps;
    }

    /**
     * Возвращает используемую память в MB.
     *
     * @return используемая память
     */
    public long getUsedMemoryMB() {
        return MemoryUtils.getUsedMemoryMB();
    }

    /**
     * Возвращает общую доступную память в MB.
     *
     * @return общая память
     */
    public long getTotalMemoryMB() {
        return MemoryUtils.getTotalMemoryMB();
    }

    /**
     * Возвращает максимальную доступную память в MB.
     *
     * @return максимальная память
     */
    public long getMaxMemoryMB() {
        return MemoryUtils.getMaxMemoryMB();
    }


    /**
     * Возвращает количество активных сущностей (заглушка для будущей реализации).
     *
     * @return количество сущностей
     */
    public int getEntityCount() {
        // TODO: Реализовать получение количества сущностей из ECS движка
        // Временная заглушка
        return 0;
    }

    /**
     * Возвращает среднее время отрисовки кадра в миллисекундах.
     *
     * @return среднее время кадра
     */
    public float getAverageFrameTime() {
        if (frameTimes.size == 0) {
            return 0;
        }

        float total = 0;
        for (float time : frameTimes) {
            total += time;
        }
        return (total / frameTimes.size) * 1000; // Convert to milliseconds
    }

    /**
     * Обрабатывает изменение размера экрана для UI Viewport.
     */
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        // Очищаем ресурсы
        frameTimes.clear();
        customMetrics.clear();
        events.clear();
        eventDurations.clear();
        Gdx.app.log("PerformanceMonitor", "Performance monitor disposed");
    }

    /**
     * Обновляет статистику производительности.
     */
    private void updatePerformanceStats() {
        // Расчет FPS
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUpdateTime;
        fps = (int) (updateInterval * 1000f / elapsedTime);
        lastUpdateTime = currentTime;

        // Обновление информации о памяти (используем MemoryUtils)
        // Память обновляется при каждом обращении через MemoryUtils

        // Проверка критического использования памяти
        if (MemoryUtils.isMemoryCritical()) {
            Gdx.app.error("PerformanceMonitor", "CRITICAL: Memory usage is over 80%!");
        }
    }

}
