package io.github.game.di.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dagger.Module;
import dagger.Provides;
import io.github.game.services.InputService;
import javax.inject.Singleton;


@Module
public class InputModule {

    @Provides
    @Singleton
    Input provideInput() {
        return Gdx.input;
    }

    @Provides
    @Singleton
    InputService provideInputService(Input input) {
        return new InputService();
    }
}
