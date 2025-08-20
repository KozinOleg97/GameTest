package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.HexType;
import javax.inject.Singleton;

@Module
public class WorldModule {

    @Provides
    @Singleton
    public HexMap provideHexMap() {
        HexMap map = new HexMap();
        // Здесь можно добавить базовую генерацию мира
        map.generateRectangularArea(-5, -5, 10, 10, HexType.PLAINS);
        return map;
    }
}
