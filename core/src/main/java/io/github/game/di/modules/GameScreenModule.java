package io.github.game.di.modules;

import com.badlogic.ashley.core.PooledEngine;
import dagger.Module;
import dagger.Provides;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.systems.PlayerInputSystem;
import io.github.game.ecs.systems.PlayerMovementSystem;
import io.github.game.ecs.systems.RenderingSystem;
import io.github.game.services.InputService;
import io.github.game.ui.screens.GameScreen;
import io.github.game.utils.GameSettings;
import javax.inject.Singleton;

@Module
public class GameScreenModule {

    @Provides
    @Singleton
    GameScreen provideGameScreen(
        PooledEngine engine,
        PlayerInputSystem inputSystem,
        PlayerMovementSystem movementSystem,
        RenderingSystem renderingSystem,
        EntityFactory entityFactory
    ) {
        return new GameScreen(engine, inputSystem, movementSystem, renderingSystem, entityFactory);
    }

    @Provides
    @Singleton
    PlayerInputSystem providePlayerInputSystem(InputService inputService, GameSettings gameSettings ) {
        return new PlayerInputSystem(inputService, gameSettings);
    }

    @Provides
    @Singleton
    PlayerMovementSystem providePlayerMovementSystem() {
        return new PlayerMovementSystem();
    }

}

