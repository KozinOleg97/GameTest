package io.github.game.core.world.hex;

/**
 * Утилитарный класс для математических операций с гексами
 */
public final class HexUtils {

    private HexUtils() {
    } // Утилитарный класс

    /**
     * Вычисляет расстояние между двумя гексами
     */
    public static int distance(Hex a, Hex b) {
        return distance(a.getCoordinates(), b.getCoordinates());
    }

    /**
     * Вычисляет расстояние между двумя наборами координат
     */
    public static int distance(HexCoordinates a, HexCoordinates b) {
        int x1 = a.getQ();
        int z1 = a.getR();
        int x2 = b.getQ();
        int z2 = b.getR();

        int y1 = -(x1 + z1);
        int y2 = -(x2 + z2);

        return (Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2)) / 2;
    }

    /**
     * Проверяет, являются ли два гекса соседями
     */
    public static boolean areNeighbors(Hex a, Hex b) {
        return distance(a, b) == 1;
    }
}
