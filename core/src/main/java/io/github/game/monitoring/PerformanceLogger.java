package io.github.game.monitoring;

import com.badlogic.gdx.Gdx;
import io.github.game.utils.MemoryUtils;

/**
 * Утилита для логирования показателей производительности. Автоматически логирует метрики с заданным
 * интервалом.
 */
public class PerformanceLogger {

    private static final int LOG_INTERVAL = 300; // Логировать каждые 300 кадров (5 секунд при 60 FPS)
    private static long frameCounter = 0;

    /**
     * Логирует показатели производительности. Должен вызываться каждый кадр.
     *
     * @param monitor монитор производительности
     */
    public static void logPerformance(PerformanceMonitor monitor) {
        frameCounter++;

        if (frameCounter % LOG_INTERVAL == 0) {
            // Основные метрики
            Gdx.app.log("PERFORMANCE",
                        String.format("FPS: %d, Memory: %dMB/%dMB (%d%%), Frame: %.2fms",
                                      monitor.getFps(),
                                      monitor.getUsedMemoryMB(),
                                      monitor.getTotalMemoryMB(),
                                      MemoryUtils.getMemoryUsagePercent(),
                                      monitor.getAverageFrameTime()));

            // Дополнительные метрики, если они доступны
            if (monitor.getEntityCount() > 0) {
                Gdx.app.log("PERFORMANCE", "Entities: " + monitor.getEntityCount());
            }

            frameCounter = 0;

            // Периодически запускаем сборку мусора
            if (MemoryUtils.isMemoryCritical()) {
                Gdx.app.log("PERFORMANCE", "Memory critical, running garbage collection");
                MemoryUtils.runGarbageCollection();
            }
        }
    }

    /**
     * Логирует кастомное событие производительности.
     *
     * @param eventName название события
     * @param duration  длительность события
     */
    public static void logEvent(String eventName, float duration) {
        Gdx.app.log("PERFORMANCE_EVENT",
                    String.format("%s: %.2fms", eventName, duration));
    }

    /**
     * Логирует использование памяти с указанным контекстом.
     *
     * @param context контекст для логирования
     */
    public static void logMemoryUsage(String context) {
        MemoryUtils.logMemoryUsage(context);
    }

    /**
     * Принудительно логирует текущие показатели производительности.
     *
     * @param monitor монитор производительности
     */
    public static void logImmediate(PerformanceMonitor monitor) {
        Gdx.app.log("PERFORMANCE_IMMEDIATE",
                    String.format("FPS: %d, Memory: %dMB/%dMB (%d%%)",
                                  monitor.getFps(),
                                  monitor.getUsedMemoryMB(),
                                  monitor.getTotalMemoryMB(),
                                  MemoryUtils.getMemoryUsagePercent()));
    }

    /**
     * Сбрасывает счетчик кадров для немедленного логирования на следующем кадре.
     */
    public static void resetCounter() {
        frameCounter = LOG_INTERVAL - 1; // Следующий кадр вызовет логирование
    }
}
