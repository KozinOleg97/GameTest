package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.utils.ResourceManager;
import javax.inject.Singleton;

@Module
public class ResourceModule {

    @Provides
    @Singleton
    ResourceManager provideResourceManager() {
        return new ResourceManager(); // Создание менеджера ресурсов
    }
}
