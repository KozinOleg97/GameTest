package io.github.game.core.world;

import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexCoordinates;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Чистый контейнер для хранения гексов. Только данные, без логики генерации. Использует двумерный
 * массив для оптимального доступа к гексам. Координаты начинаются с (0, 0) и идут до (width-1,
 * height-1).
 */
public class HexMap {

    private final Hex[][] hexGrid;
    private final int width;
    private final int height;
    private int count = 0;

    /**
     * Создает карту гексов заданного размера
     *
     * @param width  количество гексов по оси Q (координата от 0 до width-1)
     * @param height количество гексов по оси R (координата от 0 до height-1)
     */
    public HexMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        this.width = width;
        this.height = height;
        this.hexGrid = new Hex[height][width];
    }

    /**
     * Добавляет гекс на карту
     *
     * @param hex гекс для добавления
     * @throws IllegalArgumentException если координаты гекса выходят за границы карты
     */
    public void addHex(Hex hex) {
        HexCoordinates coordinates = hex.getCoordinates();
        int q = coordinates.getQ();
        int r = coordinates.getR();

        if (!isWithinBounds(q, r)) {
            throw new IllegalArgumentException(
                "Hex coordinates out of bounds: (" + q + ", " + r + ")");
        }

        if (hexGrid[r][q] == null) {
            count++;
        }
        hexGrid[r][q] = hex;
    }

    /**
     * Возвращает гекс по координатам
     *
     * @param coordinates координаты гекса
     * @return Optional с гексом, если найден
     */
    public Hex getHex(HexCoordinates coordinates) {
        int q = coordinates.getQ();
        int r = coordinates.getR();

        return hexGrid[r][q];
    }

    /**
     * Возвращает гекс по координатам (q, r)
     */
    public Hex getHex(int q, int r) {
        return hexGrid[r][q];
    }

    /**
     * Проверяет существование гекса с заданными координатами
     */
    public boolean hasHex(HexCoordinates coordinates) {
        int q = coordinates.getQ();
        int r = coordinates.getR();

        if (!isWithinBounds(q, r)) {
            return false;
        }

        return hexGrid[r][q] != null;
    }

    /**
     * Удаляет гекс с карты
     */
    public void removeHex(HexCoordinates coordinates) {
        int q = coordinates.getQ();
        int r = coordinates.getR();

        if (isWithinBounds(q, r) && hexGrid[r][q] != null) {
            hexGrid[r][q] = null;
            count--;
        }
    }

    /**
     * Возвращает неизменяемое представление всех гексов на карте
     */
    public List<Hex> getHexes() {
        List<Hex> result = new ArrayList<>(count);

        for (int r = 0; r < height; r++) {
            for (int q = 0; q < width; q++) {
                if (hexGrid[r][q] != null) {
                    result.add(hexGrid[r][q]);
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * Возвращает количество гексов на карте
     *
     * @return количество гексов
     */
    public int size() {
        return count;
    }

    /**
     * Очищает карту, удаляя все гексы Используется при перезагрузке мира или смене карты
     */
    public void clear() {
        for (int r = 0; r < height; r++) {
            for (int q = 0; q < width; q++) {
                hexGrid[r][q] = null;
            }
        }
        count = 0;
    }

    /**
     * Возвращает ширину карты (количество гексов по оси Q)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Возвращает высоту карты (количество гексов по оси R)
     */
    public int getHeight() {
        return height;
    }

    public Iterator<Hex> getRowIterator(int r, int initialQ) {
        if (r < 0 || r >= height) {
            return Collections.emptyIterator();
        }

        int startQ = Math.max(initialQ, 0);
        if (startQ >= width) {
            return Collections.emptyIterator();
        }

        return new Iterator<>() {
            private int currentQ = startQ;

            @Override
            public boolean hasNext() {
                return currentQ < width;
            }

            @Override
            public Hex next() {
                return hexGrid[r][currentQ++];
            }
        };
    }

    public Iterator<Hex> getRowIteratorUnsafe(int r, int initialQ) {
        int startQ = Math.max(initialQ, 0);
        if (startQ >= width) {
            return Collections.emptyIterator();
        }

        return new Iterator<>() {
            private int currentQ = startQ;

            @Override
            public boolean hasNext() {
                return currentQ < width;
            }

            @Override
            public Hex next() {
                return hexGrid[r][currentQ++];
            }
        };
    }

    public Hex[] getRowDirectAccess(int r) {
//        if (r < 0 || r >= height) {
//            return new Hex[0];
//        }
        return hexGrid[r];
    }

    public Hex[][] getHexGrid() {
        return hexGrid;
    }

    /**
     * Возвращает гекс по индексам массива (для внутреннего использования)
     */
    Hex getHexByIndex(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return null;
        }
        return hexGrid[row][col];
    }

    /**
     * Проверяет, находятся ли координаты в пределах карты
     */
    private boolean isWithinBounds(int q, int r) {
        return q >= 0 && q < width && r >= 0 && r < height;
    }
}
