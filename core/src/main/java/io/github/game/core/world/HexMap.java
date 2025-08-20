package io.github.game.core.world;

import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexCoordinates;
import io.github.game.core.world.hex.HexType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Представляет карту гексов игрового мира. Использует Map для хранения гексов, что позволяет
 * работать с разреженными картами.
 */
public class HexMap {

    private final Map<HexCoordinates, Hex> hexes = new HashMap<>();

    /**
     * Добавляет гекс на карту
     *
     * @param hex гекс для добавления
     */
    public void addHex(Hex hex) {
        hexes.put(hex.getCoordinates(), hex);
    }

    /**
     * Возвращает гекс по координатам
     *
     * @param coordinates координаты гекса
     * @return Optional с гексом, если найден
     */
    public Optional<Hex> getHex(HexCoordinates coordinates) {
        return Optional.ofNullable(hexes.get(coordinates));
    }

    /**
     * Возвращает гекс по координатам (q, r)
     */
    public Optional<Hex> getHex(int q, int r) {
        return getHex(new HexCoordinates(q, r));
    }

    /**
     * Проверяет существование гекса с заданными координатами
     */
    public boolean hasHex(HexCoordinates coordinates) {
        return hexes.containsKey(coordinates);
    }

    /**
     * Удаляет гекс с карты
     */
    public void removeHex(HexCoordinates coordinates) {
        hexes.remove(coordinates);
    }

    /**
     * Возвращает неизменяемое представление карты гексов
     */
    public Map<HexCoordinates, Hex> getHexes() {
        return Collections.unmodifiableMap(hexes);
    }

    /**
     * Создает и добавляет прямоугольную область гексов
     *
     * @param startQ начальная координата Q
     * @param startR начальная координата R
     * @param width  ширина области
     * @param height высота области
     * @param type   тип гексов
     */
    public void generateRectangularArea(int startQ, int startR, int width, int height,
                                        HexType type) {
        for (int q = startQ; q < startQ + width; q++) {
            for (int r = startR; r < startR + height; r++) {
                Hex hex = new Hex(q, r, type);
                addHex(hex);
            }
        }
    }
}
