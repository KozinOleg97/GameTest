package io.github.game.ecs.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

/**
 * Компонент для хранения позиции сущности в 2D-пространстве.
 */
@Getter
public class PositionComponent implements Component {

    public static final ComponentMapper<PositionComponent> MAPPER = ComponentMapper.getFor(PositionComponent.class);
    private final Vector2 coordinates = new Vector2(); // Текущие координаты (x, y)
    private int z = 0; // Z-уровень (для сортировки слоёв отрисовки)

    /**
     * Конструктор с начальными координатами.
     * @param x Начальная позиция по X.
     * @param y Начальная позиция по Y.
     */
    public PositionComponent(float x, float y) {
        coordinates.set(x, y);
    }

    /**
     * Конструктор с начальными координатами и Z-уровнем.
     * @param x Начальная позиция по X.
     * @param y Начальная позиция по Y.
     * @param z Z-уровень (например, 0 — земля, 1 — объекты, 2 — небо).
     */
    public PositionComponent(float x, float y, int z) {
        coordinates.set(x, y);
        this.z = z;
    }
}
