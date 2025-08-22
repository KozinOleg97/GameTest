package io.github.game.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import io.github.game.core.world.hex.Hex;
import lombok.Getter;
import lombok.Setter;

/**
 * Компонент, помечающий сущность как гекс на карте мира. Содержит ссылку на данные гекса из модели
 * мира.
 */
@Setter
@Getter
public class HexComponent implements Component, Pool.Poolable {

    private Hex hex;

    public HexComponent() {
    }

    public HexComponent(Hex hex) {
        this.hex = hex;
    }

    @Override
    public void reset() {
        this.hex = null;
    }
}
