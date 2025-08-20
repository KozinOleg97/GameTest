package io.github.game.di.modules;

import com.badlogic.ashley.core.PooledEngine;
import dagger.Module;
import dagger.Provides;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.systems.MovementSystem;
import io.github.game.ecs.systems.NPCLogicSystem;
import io.github.game.ecs.systems.PlayerInputSystem;
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
        MovementSystem movementSystem,
        RenderingSystem renderingSystem,
        EntityFactory entityFactory,
        NPCLogicSystem npcLogicSystem
    ) {
        return new GameScreen(engine, inputSystem, movementSystem, renderingSystem, entityFactory,
            npcLogicSystem);
    }

    @Provides
    @Singleton
    PlayerInputSystem providePlayerInputSystem(InputService inputService,
                                               GameSettings gameSettings) {
        return new PlayerInputSystem(inputService, gameSettings);
    }

    @Provides
    @Singleton
    MovementSystem provideMovementSystem() {
        return new MovementSystem();
    }

    @Provides
    @Singleton
    NPCLogicSystem provideNPCLogicSystem(GameSettings gameSettings) {
        return new NPCLogicSystem(gameSettings);
    }


}

