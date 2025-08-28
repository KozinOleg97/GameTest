package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.generator.AdvancedWorldGenerator;
import io.github.game.core.world.generator.WorldGenerator;
import javax.inject.Singleton;

/**
 * Dagger модуль для предоставления зависимостей, связанных с игровым миром.
 */
@Module
public class WorldModule {

    /**
     * Предоставляет реализацию генератора мира. В данном случае - генератор прямоугольной области.
     *
     * @return реализация WorldGenerator
     */
    @Provides
    @Singleton
    public WorldGenerator provideWorldGenerator() {
        // Параметры генерации можно вынести в конфигурацию
//        return new RectangularWorldGenerator(-5, -5, 100, 100, HexType.PLAINS);

        return new AdvancedWorldGenerator(200, 200, 123452222);
    }

    /**
     * Предоставляет карту гексов, сгенерированную с помощью WorldGenerator.
     *
     * @param worldGenerator генератор мира для создания карты
     * @return заполненная карта гексов
     */
    @Provides
    @Singleton
    public HexMap provideHexMap(WorldGenerator worldGenerator) {
        return worldGenerator.generateWorld();
    }
}
