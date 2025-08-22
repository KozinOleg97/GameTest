package io.github.game.ecs.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import lombok.Getter;
import lombok.Setter;

/**
 * Компонент для хранения позиции сущности в 2D-пространстве.
 */
@Getter
@Setter
public class PositionComponent implements Component, Pool.Poolable {

    private final Vector2 coordinates = new Vector2(); // Текущие координаты (x, y)
    private int z; // Z-уровень (для сортировки слоёв отрисовки)

    public PositionComponent() {
    }

    public PositionComponent(float x, float y) {
        this.coordinates.set(x, y);
    }


    @Override
    public void reset() {
        coordinates.set(0, 0);
        z = 0;
    }
}
