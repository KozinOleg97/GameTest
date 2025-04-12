package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.utils.GameSettings;
import javax.inject.Singleton;

@Module
public class SettingsModule {

    @Provides
    @Singleton
    GameSettings provideGameSettings() {
        return new GameSettings();
    }
}
