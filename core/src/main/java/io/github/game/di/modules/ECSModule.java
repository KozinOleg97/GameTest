package io.github.game.di.modules;

import com.badlogic.ashley.core.PooledEngine;
import dagger.Module;
import dagger.Provides;
import io.github.game.ecs.EntityFactory;
import io.github.game.utils.ResourceManager;
import javax.inject.Singleton;


@Module
public class ECSModule {

    @Provides
    @Singleton
    PooledEngine providePooledEngine() {
        return new PooledEngine(); // Создание ECS-движка
    }

    @Provides
    @Singleton
    EntityFactory provideEntityFactory(PooledEngine engine, ResourceManager resourceManager) {
        return new EntityFactory(engine, resourceManager);
    }
}
