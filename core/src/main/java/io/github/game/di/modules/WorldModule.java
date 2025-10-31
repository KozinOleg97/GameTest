package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.core.world.generator.GenerationManager;
import io.github.game.core.world.generator.GeneratorFactory;
import io.github.game.settings.GameplaySettings;
import javax.inject.Singleton;

/**
 * Dagger модуль для предоставления зависимостей, связанных с игровым миром и генерацией.
 */
@Module
public class WorldModule {

    @Provides
    @Singleton
    GeneratorFactory provideGeneratorFactory() {
        return new GeneratorFactory();
    }

    @Provides
    @Singleton
    GenerationManager provideGenerationManager(GeneratorFactory factory) {
        return new GenerationManager(factory);
    }

    @Provides
    @Singleton
    GenerationContext provideGenerationContext(GameplaySettings settings) {
        return new GenerationContext(settings);
    }
}
