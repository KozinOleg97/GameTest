package io.github.game.core.world.generator;

import lombok.Getter;

/**
 * Базовый класс конфигурации для генераторов.
 * Специфичные генераторы могут расширять этот класс для своих параметров.
 */
@Getter
public abstract class GeneratorConfig {
    // Общие поля
    protected long seed;


    public GeneratorConfig(long seed) {
        this.seed = seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
}
