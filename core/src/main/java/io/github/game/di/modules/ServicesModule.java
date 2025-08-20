package io.github.game.di.modules;

import com.badlogic.ashley.core.PooledEngine;
import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.ecs.EntityFactory;
import io.github.game.services.AssetService;
import io.github.game.services.InputService;
import io.github.game.services.WorldInitService;
import io.github.game.utils.ResourceManager;
import javax.inject.Singleton;

@Module
public class ServicesModule {

    @Provides
    @Singleton
    InputService provideInputService() {
        return new InputService();
    }

    @Provides
    @Singleton
    AssetService provideAssetService(ResourceManager resourceManager) {
        return new AssetService(resourceManager);
    }

    @Provides
    @Singleton
    WorldInitService provideWorldInitService(PooledEngine engine, HexMap hexMap,
                                             EntityFactory entityFactory) {
        return new WorldInitService(engine, hexMap, entityFactory);
    }
}
