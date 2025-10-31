package io.github.game.di.modules;

import com.badlogic.gdx.Gdx;
import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.generator.location.LocationGenerator;
import io.github.game.core.world.generator.location.RandomLocationGenerator;
import io.github.game.core.world.generator.world.ProceduralWorldGenerator;
import io.github.game.core.world.generator.world.WorldGenerator;
import io.github.game.settings.GameplaySettings;
import javax.inject.Singleton;

/**
 * Dagger модуль для предоставления зависимостей, связанных с игровым миром и генерацией.
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
    public WorldGenerator provideWorldGenerator(GameplaySettings settings) {
        // Параметры генерации можно вынести в конфигурацию
//        return new RectangularWorldGenerator(-5, -5, 100, 100, HexType.PLAINS);

        return new ProceduralWorldGenerator(settings.getHexSize(), settings.getHexSize(), 5050);
    }

    @Provides
    @Singleton
    public LocationGenerator provideLocationGenerator(HexMap hexMap) {
        return new RandomLocationGenerator(hexMap, 20);
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

        long startTime = System.currentTimeMillis();

        HexMap hexMap = worldGenerator.generateWorld();

        long duration = System.currentTimeMillis() - startTime;
        Gdx.app.log("WorldInit", "Hex entities creation completed " + duration + " mils");

        return hexMap;
    }
}
