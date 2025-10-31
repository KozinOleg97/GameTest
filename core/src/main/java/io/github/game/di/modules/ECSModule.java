package io.github.game.di.modules;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.systems.CameraControlSystem;
import io.github.game.ecs.systems.MovementSystem;
import io.github.game.ecs.systems.NPCLogicSystem;
import io.github.game.ecs.systems.PlayerInputSystem;
import io.github.game.ecs.systems.RenderingSystem;
import io.github.game.ecs.systems.world.LocationRenderSystem;
import io.github.game.services.InputService;
import io.github.game.settings.CameraSettings;
import io.github.game.settings.GameplaySettings;
import io.github.game.settings.GraphicsSettings;
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
    PlayerInputSystem providePlayerInputSystem(InputService inputService,
                                               GameplaySettings gameplaySettings) {
        return new PlayerInputSystem(inputService, gameplaySettings);
    }

    @Provides
    @Singleton
    MovementSystem provideMovementSystem() {
        return new MovementSystem();
    }

    @Provides
    @Singleton
    NPCLogicSystem provideNPCLogicSystem(GameplaySettings gameplaySettings) {
        return new NPCLogicSystem(gameplaySettings);
    }


    @Provides
    @Singleton
    RenderingSystem provideRenderingSystem(SpriteBatch batch, OrthographicCamera camera) {
        return new RenderingSystem(batch, camera);
    }

    @Provides
    @Singleton
    PooledEngine providePooledEngine(PlayerInputSystem playerInputSystem,
                                     MovementSystem movementSystem,
                                     NPCLogicSystem npcLogicSystem,
                                     CameraControlSystem cameraControlSystem,

                                     RenderingSystem renderingSystem,
                                     LocationRenderSystem locationRenderSystem
    ) {
        PooledEngine engine = new PooledEngine();

        // Важно: системы обновления должны быть до системы рендеринга

        engine.addSystem(playerInputSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(npcLogicSystem);
        engine.addSystem(cameraControlSystem);

        engine.addSystem(renderingSystem);
        engine.addSystem(locationRenderSystem);

        return engine;
    }

    @Provides
    @Singleton
    CameraControlSystem provideCameraControlSystem(OrthographicCamera camera,
                                                   InputService inputService,
                                                   GraphicsSettings graphicsSettings,
                                                   CameraSettings cameraSettings) {
        return new CameraControlSystem(camera, inputService, graphicsSettings, cameraSettings);
    }


    @Provides
    @Singleton
    LocationRenderSystem provideLocationRenderingSystem(SpriteBatch batch,
                                                        OrthographicCamera camera) {
        return new LocationRenderSystem(batch, camera);
    }

}
