package io.github.game.core.world.generator.world;

import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexType;

/**
 * Реализация генератора, создающего прямоугольную область гексов. Для тестирования.
 */
public class RectangularWorldGenerator implements WorldGenerator {

    private final int startQ;
    private final int startR;
    private final int width;
    private final int height;
    private final HexType baseType;

    /**
     * Создает генератор прямоугольной области
     *
     * @param width  ширина области в гексах
     * @param height высота области в гексах
     * @param seed
     */
    public RectangularWorldGenerator(int width, int height, long seed) {
        this.startQ = 0;
        this.startR = 0;
        this.width = width;
        this.height = height;
        this.baseType = HexType.FOREST;
    }

    /**
     * Генерирует прямоугольную область гексов
     *
     * @return карта с заполненной прямоугольной областью
     */
    @Override
    public HexMap generateWorld() {
        HexMap map = new HexMap(width, height);
        for (int q = startQ; q < startQ + width; q++) {
            for (int r = startR; r < startR + height; r++) {
                // Создаем гекс и добавляем его на карту
                Hex hex = new Hex(q, r, baseType);
                map.addHex(hex);
            }
        }
        return map;
    }
}
