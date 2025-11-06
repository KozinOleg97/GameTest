package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.core.world.generator.GeneratorFactory;
import io.github.game.ecs.EntityFactory;
import io.github.game.services.NameService;
import io.github.game.settings.GameplaySettings;
import javax.inject.Singleton;

/**
 * Dagger модуль для предоставления зависимостей, связанных с игровым миром и генерацией.
 */
@Module
public class WorldModule {

    @Provides
    @Singleton
    GeneratorFactory provideGeneratorFactory(GenerationContext context, EntityFactory entityFactory) {
        return new GeneratorFactory(context, entityFactory);
    }

    @Provides
    @Singleton
    GenerationContext provideGenerationContext(GameplaySettings settings, NameService nameService) {
        return new GenerationContext(settings, nameService);
    }

    @Provides
    @Singleton
    NameService provideNameService() {
        return new NameService();
    }
}
