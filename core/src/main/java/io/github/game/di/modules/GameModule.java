package io.github.game.di.modules;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.core.world.generator.GenerationManager;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.systems.CameraControlSystem;
import io.github.game.ecs.systems.MovementSystem;
import io.github.game.ecs.systems.NPCLogicSystem;
import io.github.game.ecs.systems.PlayerInputSystem;
import io.github.game.ecs.systems.RenderingSystem;
import io.github.game.ecs.systems.world.LocationRenderSystem;
import io.github.game.input.BattleInputProcessor;
import io.github.game.input.InputManager;
import io.github.game.input.WorldMapInputProcessor;
import io.github.game.monitoring.PerformanceMonitor;
import io.github.game.renderer.HexMapRenderer;
import io.github.game.services.AssetService;
import io.github.game.services.InputService;
import io.github.game.services.WorldEntityService;
import io.github.game.settings.CameraSettings;
import io.github.game.settings.GameplaySettings;
import io.github.game.settings.GraphicsSettings;
import io.github.game.utils.GameSettingsConstants;
import io.github.game.utils.ResourceManager;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Компоненты связанные с игровым процессом
 */
@Module
public class GameModule {

    // SpriteBatch for main rendering
    @Provides
    @Singleton
    SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }

    // From GraphicsModule
    @Provides
    @Singleton
    OrthographicCamera provideCamera(GraphicsSettings graphicsSettings) {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, graphicsSettings.getResolutionWidth(),
                          graphicsSettings.getResolutionHeight());

        //TODO надо уточнить в какую точку ставить камеру
        camera.position.set(
            graphicsSettings.getResolutionWidth() / 2f,
            graphicsSettings.getResolutionHeight() / 2f,
            0
        );
        camera.update();
        return camera;
    }

    @Provides
    @Singleton
    Viewport provideViewport(OrthographicCamera camera, GraphicsSettings graphicsSettings) {
        return switch (graphicsSettings.getViewportType()) {
            case GameSettingsConstants.VIEWPORT_TYPE_SCREEN -> new ScreenViewport(camera);
            case GameSettingsConstants.VIEWPORT_TYPE_STRETCH -> new StretchViewport(
                graphicsSettings.getResolutionWidth(),
                graphicsSettings.getResolutionHeight(),
                camera
            );
            default -> new FitViewport(
                graphicsSettings.getResolutionWidth(),
                graphicsSettings.getResolutionHeight(),
                camera
            );
        };
    }

    @Provides
    @Singleton
    ShapeRenderer provideShapeRenderer() {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        return shapeRenderer;
    }

//    @Provides
//    @Singleton
//    HexMapRenderer provideHexMapRenderer(GenerationContext context,
//                                         ShapeRenderer shapeRenderer,
//                                         OrthographicCamera camera) {
//        return new HexMapRenderer(context, shapeRenderer, camera);
//    }

    @Provides
    @Singleton
    @Named("uiViewport")
    Viewport provideUiViewport() {
        return new ScreenViewport(); // Viewport для UI, который не зависит от камеры мира
    }

    @Provides
    @Singleton
    @Named("uiSpriteBatch")
    SpriteBatch provideUiSpriteBatch() {
        return new SpriteBatch();
    }

    // From InputModule
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

    // From MonitoringModule
    @Provides
    @Singleton
    PerformanceMonitor providePerformanceMonitor(GraphicsSettings graphicsSettings,
                                                 @Named("uiSpriteBatch") SpriteBatch uiSpriteBatch,
                                                 @Named("uiViewport") Viewport uiViewport,
                                                 BitmapFont font,
                                                 PooledEngine engine) {
        return new PerformanceMonitor(graphicsSettings, uiSpriteBatch, uiViewport, font,
                                      engine);
    }

    // From ServicesModule
    @Provides
    @Singleton
    InputService provideInputService() {
        return new InputService();
    }

    @Provides
    @Singleton
    AssetService provideAssetService(ResourceManager resourceManager) {
        return new AssetService(resourceManager);
    }

    @Provides
    @Singleton
    WorldEntityService provideWorldEntityService(PooledEngine engine,
                                                 EntityFactory entityFactory,
                                                 GenerationManager generationManager,
                                                 GenerationContext generationContext
    ) {
        return new WorldEntityService(engine, entityFactory, generationManager, generationContext);
    }

    // From ECSModule
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

        // Важно: системы об��овления должны быть до системы рендеринга
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
