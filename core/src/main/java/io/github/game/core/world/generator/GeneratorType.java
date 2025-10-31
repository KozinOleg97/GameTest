package io.github.game.core.world.generator;

/**
 * Enum для различных типов генераторов. Используется GeneratorFactory для создания соответствующего
 * генератора.
 */
public enum GeneratorType {
    // World generators
    PROCEDURAL_WORLD,
    RECTANGULAR_WORLD,
    SAVED_WORLD,

    // Location generators
    RANDOM_LOCATION,
    SAVED_LOCATION,

    // Будущие генераторы
    // ITEM_RANDOM,
    // NPC_RANDOM,
    // etc.
}
