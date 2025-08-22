package io.github.game.utils;

import com.badlogic.gdx.Gdx;

public class MemoryUtils {

    public static void logMemoryUsage(String context) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory() / (1024 * 1024);

        Gdx.app.log("Memory", context + " - Used: " + usedMemory + "MB, Free: " + freeMemory +
            "MB, Total: " + totalMemory + "MB, Max: " + maxMemory + "MB");
    }

    public static boolean isMemoryCritical() {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();

        // Считаем память критической, если свободно менее 10%
        return (freeMemory < totalMemory * 0.1);
    }
}
