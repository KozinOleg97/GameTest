package io.github.game.di.modules;

import com.badlogic.ashley.core.PooledEngine;
import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.PooledEngineCnt;
import io.github.game.services.AssetService;
import io.github.game.services.HexMapService;
import io.github.game.services.InputService;
import io.github.game.services.WorldEntityService;
import io.github.game.utils.ResourceManager;
import javax.inject.Singleton;

@Module
public class ServicesModule {

    @Provides
    @Singleton
    public HexMapService provideHexMapService(HexMap hexMap) {
        return new HexMapService(hexMap);
    }

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
    WorldEntityService provideWorldEntityService(PooledEngineCnt engine, HexMap hexMap,
                                                 EntityFactory entityFactory,
                                                 HexMapService hexMapService) {
        return new WorldEntityService(engine, hexMap, entityFactory, hexMapService);
    }
}
