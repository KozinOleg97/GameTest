package io.github.game.core.world.hex;

import lombok.Getter;

/**
 * Перечисление возможных типов местности для гекса.
 */
@Getter
public enum HexType {
    PLAINS("Равнины"),
    FOREST("Лес"),
    MOUNTAINS("Горы"),
    DESERT("Пустыня"),
    OCEAN("Океан"),
    COAST("Побережье"),
    SWAMP("Болото");

    private final String name;

    HexType(String name) {
        this.name = name;
    }

}
