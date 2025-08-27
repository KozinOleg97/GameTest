package io.github.game.utils;

import com.badlogic.gdx.Gdx;

/**
 * Утилита для работы с памятью и мониторинга использования памяти. Предоставляет методы для
 * получения информации о памяти и логирования.
 */
public final class MemoryUtils {

    private MemoryUtils() {
        // Запрещаем создание экземпляров утилитного класса
    }

    /**
     * Логирует текущее использование памяти с указанным контекстом.
     *
     * @param context контекст для логирования (например, "After world initialization")
     */
    public static void logMemoryUsage(String context) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);

        Gdx.app.log("Memory",
                    context + " - Used: " + usedMemory + "MB, Total: " + totalMemory + "MB, Max: " +
                    maxMemory + "MB");
    }

    /**
     * Проверяет, является ли использование памяти критическим.
     *
     * @return true если используется более 80% доступной памяти
     */
    public static boolean isMemoryCritical() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        return (usedMemory > maxMemory * 0.8); // 80% использования
    }

    /**
     * Возвращает используемую память в мегабайтах.
     *
     * @return используемая память в MB
     */
    public static long getUsedMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    /**
     * Возвращает общую выделенную память в мегабайтах.
     *
     * @return общая память в MB
     */
    public static long getTotalMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() / (1024 * 1024);
    }

    /**
     * Возвращает максимальную доступную память в мегабайтах.
     *
     * @return максимальная память в MB
     */
    public static long getMaxMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.maxMemory() / (1024 * 1024);
    }

    /**
     * Возвращает процент использования памяти.
     *
     * @return процент использования памяти (0-100)
     */
    public static int getMemoryUsagePercent() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        return (int) ((usedMemory * 100) / maxMemory);
    }

    /**
     * Вызывает сборку мусора и логирует результат.
     */
    public static void runGarbageCollection() {
        long before = getUsedMemoryMB();
        System.gc();
        long after = getUsedMemoryMB();
        Gdx.app.log("Memory", "Garbage collection: " + before + "MB -> " + after + "MB");
    }
}
