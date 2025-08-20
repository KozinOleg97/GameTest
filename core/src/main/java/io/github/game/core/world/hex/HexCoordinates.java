package io.github.game.core.world.hex;

import lombok.Value;

/**
 * Immutable класс для представления координат гекса. Используется как ключ в карте гексов.
 * Автоматически генерирует equals, hashCode и toString через Lombok
 */
@Value
public class HexCoordinates {

    int q;
    int r;
}
