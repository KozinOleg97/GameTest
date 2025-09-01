package io.github.game.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import io.github.game.core.world.hex.HexCoordinates;
import lombok.Getter;
import lombok.Setter;

/**
 * Компонент, содержащий только координаты гекса.
 * Все данные гекса хранятся в HexMap, к которым можно получить доступ через HexMapService.
 * Это устраняет дублирование данных и обеспечивает единый источник истины.
 */
@Getter
@Setter
public class HexComponent implements Component, Pool.Poolable {
    private HexCoordinates coordinates;

    public HexComponent() {
    }

    public HexComponent(HexCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public void reset() {
        this.coordinates = null;
    }
}
