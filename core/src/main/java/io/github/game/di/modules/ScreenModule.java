package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.ui.screens.ScreenFactory;
import io.github.game.ui.screens.ScreenFactoryImpl;
import javax.inject.Singleton;

@Module
public class ScreenModule {

    @Provides
    @Singleton
    ScreenFactory provideScreenFactory(ScreenFactoryImpl impl) {
        return impl;
    }
}
