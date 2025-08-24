package io.github.game.core.world.generator;

import io.github.game.core.world.HexMap;

/**
 * Интерфейс для генерации игрового мира. Позволяет реализовать различные алгоритмы генерации
 **/
public interface WorldGenerator {

    /**
     * Генерирует карту гексов
     *
     * @return заполненная карта гексов
     */
    HexMap generateWorld();
}
