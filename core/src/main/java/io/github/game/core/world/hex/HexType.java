package io.github.game.core.world.hex;

/**
 * Перечисление возможных типов местности для гекса.
 */
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

    public String getName() {
        return name;
    }
}
