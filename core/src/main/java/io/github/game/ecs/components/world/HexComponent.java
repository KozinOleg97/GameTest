package io.github.game.ecs.components.world;

import com.badlogic.ashley.core.Component;
import io.github.game.core.world.hex.Hex;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Компонент, помечающий сущность как гекс на карте мира. Содержит ссылку на данные гекса из модели
 * мира.
 */
@Getter
@RequiredArgsConstructor
public class HexComponent implements Component {

    private final Hex hex;
}
