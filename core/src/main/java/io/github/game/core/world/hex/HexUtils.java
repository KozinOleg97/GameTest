package io.github.game.core.world.hex;

import com.badlogic.ashley.core.Entity;
import io.github.game.ecs.components.world.HexComponent;
import io.github.game.services.HexMapService;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/**
 * Утилитарный класс для математических операций с гексами. Поддерживает как работу напрямую с
 * объектами Hex, так и через HexMapService.
 */
@UtilityClass
public final class HexUtils {

    /**
     * Направления для шести соседних гексов в осевых координатах (q, r)
     */
    public static final HexCoordinates[] HEX_DIRECTIONS = {
        new HexCoordinates(1, 0), new HexCoordinates(1, -1), new HexCoordinates(0, -1),
        new HexCoordinates(-1, 0), new HexCoordinates(-1, 1), new HexCoordinates(0, 1)
    };

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
     * Вычисляет расстояние между двумя сущностями гексов через HexMapService
     */
    public static int distance(Entity a, Entity b, HexMapService hexMapService) {
        Hex hexA = getHexFromEntity(a, hexMapService);
        Hex hexB = getHexFromEntity(b, hexMapService);



        return distance(hexA, hexB);
    }

    /**
     * Проверяет, являются ли два гекса соседями
     */
    public static boolean areNeighbors(Hex a, Hex b) {
        return distance(a, b) == 1;
    }

    /**
     * Проверяет, являются ли две сущности гексов соседями через HexMapService
     */
    public static boolean areNeighbors(Entity a, Entity b, HexMapService hexMapService) {
        return distance(a, b, hexMapService) == 1;
    }

    /**
     * Получает координаты из сущности гекса
     */
    public static HexCoordinates getCoordinates(Entity entity) {
        HexComponent comp = entity.getComponent(HexComponent.class);
        return comp.getCoordinates();
    }

    /**
     * Получает данные гекса из сущности через HexMapService
     */
    public static Hex getHexFromEntity(Entity entity, HexMapService hexMapService) {

        return hexMapService.getHex(getCoordinates(entity));

    }

    /**
     * Получает координаты соседа в заданном направлении
     */
    public static HexCoordinates getNeighborCoordinates(HexCoordinates coordinates, int direction) {
        if (direction < 0 || direction >= 6) {
            throw new IllegalArgumentException("Direction must be between 0 and 5");
        }

        HexCoordinates dir = HEX_DIRECTIONS[direction];
        return new HexCoordinates(coordinates.getQ() + dir.getQ(), coordinates.getR() + dir.getR());
    }

    /**
     * Получает координаты соседа в заданном направлении для сущности гекса
     */
    public static HexCoordinates getNeighborCoordinates(Entity entity, int direction,
                                                        HexMapService hexMapService) {

        return getNeighborCoordinates(getCoordinates(entity), direction);
    }

    /**
     * Находит все соседние координаты для заданных координат
     */
    public static HexCoordinates[] getAllNeighborCoordinates(HexCoordinates coordinates) {
        HexCoordinates[] neighbors = new HexCoordinates[6];
        for (int i = 0; i < 6; i++) {
            neighbors[i] = getNeighborCoordinates(coordinates, i);
        }
        return neighbors;
    }

    /**
     * Находит все соседние координаты для сущности гекса
     */
    public static HexCoordinates[] getAllNeighborCoordinates(Entity entity,
                                                             HexMapService hexMapService) {
        return HexUtils.getAllNeighborCoordinates(getCoordinates(entity));
    }

    /**
     * Преобразует осевые координаты (q, r) в пиксельные координаты
     */
    public static float[] axialToPixel(HexCoordinates coordinates, float hexSize) {
        float x = hexSize *
                  (float) (Math.sqrt(3) * (coordinates.getQ() + 0.5 * (coordinates.getR() & 1)));
        float y = hexSize * 1.5f * coordinates.getR();
        return new float[]{x, y};
    }

    /**
     * Преобразует осевые координаты сущности в пиксельные координаты
     */
    public static float[] axialToPixel(Entity entity, float hexSize,
                                       HexMapService hexMapService) {
        return axialToPixel(getCoordinates(entity), hexSize);
    }

    /**
     * Возвращает координаты всех шести соседних гексов
     */
    public static HexCoordinates[] getNeighborCoordinates(HexCoordinates coordinates) {
        int q = coordinates.getQ();
        int r = coordinates.getR();

        return new HexCoordinates[]{
            new HexCoordinates(q + 1, r),      // Восток
            new HexCoordinates(q + 1, r - 1),  // Северо-восток
            new HexCoordinates(q, r - 1),      // Северо-запад
            new HexCoordinates(q - 1, r),      // Запад
            new HexCoordinates(q - 1, r + 1),  // Юго-запад
            new HexCoordinates(q, r + 1)       // Юго-восток
        };
    }
}
