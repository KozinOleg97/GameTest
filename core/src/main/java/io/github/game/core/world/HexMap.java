package io.github.game.core.world;

import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexCoordinates;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Чистый контейнер для хранения гексов. Только данные, без логики генерации.
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
     * Возвращает количество гексов на карте
     * @return количество гексов
     */
    public int size() {
        return hexes.size();
    }

    /**
     * Очищает карту, удаляя все гексы
     * Используется при перезагрузке мира или смене карты
     */
    public void clear() {
        hexes.clear();
    }
}
