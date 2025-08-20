package io.github.game.di.modules;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.systems.MovementSystem;
import io.github.game.ecs.systems.NPCLogicSystem;
import io.github.game.ecs.systems.PlayerInputSystem;
import io.github.game.ecs.systems.RenderingSystem;
import io.github.game.ecs.systems.world.WorldSimulationSystem;
import io.github.game.services.InputService;
import io.github.game.utils.GameSettings;
import io.github.game.utils.ResourceManager;
import javax.inject.Singleton;

@Module
public class ECSModule {

    @Provides
    @Singleton
    EntityFactory provideEntityFactory(PooledEngine engine, ResourceManager resourceManager) {
        return new EntityFactory(engine, resourceManager);
    }

    @Provides
    @Singleton
    WorldSimulationSystem provideWorldSimulationSystem() {
        return new WorldSimulationSystem();
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


    @Provides
    @Singleton
    RenderingSystem provideRenderingSystem(SpriteBatch batch) {
        return new RenderingSystem(batch);
    }
}
