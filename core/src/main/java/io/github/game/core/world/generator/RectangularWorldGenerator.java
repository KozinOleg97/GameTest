package io.github.game.core.world.generator;

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
     * @param startQ   начальная координата Q (столбец)
     * @param startR   начальная координата R (строка)
     * @param width    ширина области в гексах
     * @param height   высота области в гексах
     * @param baseType базовый тип гексов для заполнения области
     */
    public RectangularWorldGenerator(int startQ, int startR, int width,
                                     int height, HexType baseType) {
        this.startQ = startQ;
        this.startR = startR;
        this.width = width;
        this.height = height;
        this.baseType = baseType;
    }

    /**
     * Генерирует прямоугольную область гексов
     *
     * @return карта с заполненной прямоугольной областью
     */
    @Override
    public HexMap generateWorld() {
        HexMap map = new HexMap();
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
