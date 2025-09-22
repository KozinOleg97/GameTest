package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.ecs.systems.CameraControlSystem;
import io.github.game.input.BattleInputProcessor;
import io.github.game.input.InputManager;
import io.github.game.input.WorldMapInputProcessor;
import io.github.game.services.InputService;
import javax.inject.Singleton;

@Module
public class InputModule {

    @Provides
    @Singleton
    WorldMapInputProcessor provideWorldMapInputProcessor(InputService inputService,
                                                         CameraControlSystem cameraControlSystem) {
        return new WorldMapInputProcessor(inputService, cameraControlSystem);
    }

    @Provides
    @Singleton
    BattleInputProcessor provideBattleInputProcessor() {
        return new BattleInputProcessor();
    }

    @Provides
    @Singleton
    InputManager provideInputManager(InputService inputService,
                                     WorldMapInputProcessor worldMapProcessor,
                                     BattleInputProcessor battleProcessor) {
        return new InputManager(inputService, worldMapProcessor, battleProcessor);
    }
}
