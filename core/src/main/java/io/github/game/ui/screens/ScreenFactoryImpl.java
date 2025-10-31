package io.github.game.ui.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.ecs.EntityFactory;
import io.github.game.input.InputManager;
import io.github.game.monitoring.PerformanceMonitor;
import io.github.game.renderer.HexMapRenderer;
import io.github.game.services.AssetService;
import io.github.game.services.CharacterEntityService;
import io.github.game.services.WorldEntityService;
import io.github.game.settings.GameplaySettings;
import io.github.game.utils.ResourceManager;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScreenFactoryImpl implements ScreenFactory {

    private final ResourceManager resourceManager;
    private final SpriteBatch spriteBatch;
    private final AssetService assetService;
    private final BitmapFont font;
    private final ScreenSwitcher screenSwitcher;
    private final WorldEntityService worldEntityService;
    private final EntityFactory entityFactory;
    private final PooledEngine engine;
    private final HexMapRenderer hexMapRenderer;
    private final InputManager inputManager;
    private final Viewport viewport;
    private final CharacterEntityService characterEntityService;
    private final PerformanceMonitor performanceMonitor;
    private final GameplaySettings gameplaySettings;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final GenerationContext generationContext;

    @Inject
    public ScreenFactoryImpl(ResourceManager resourceManager,
                             SpriteBatch spriteBatch,
                             AssetService assetService,
                             BitmapFont font,
                             ScreenSwitcher screenSwitcher,
                             WorldEntityService worldEntityService,
                             EntityFactory entityFactory,
                             PooledEngine engine,
                             HexMapRenderer hexMapRenderer,
                             InputManager inputManager,
                             Viewport viewport,
                             CharacterEntityService characterEntityService,
                             PerformanceMonitor performanceMonitor,
                             GameplaySettings gameplaySettings,
                             ShapeRenderer shapeRenderer,
                             OrthographicCamera camera,
                             GenerationContext generationContext
    ) {
        this.resourceManager = resourceManager;
        this.spriteBatch = spriteBatch;
        this.assetService = assetService;
        this.font = font;
        this.screenSwitcher = screenSwitcher;
        this.engine = engine;
        this.worldEntityService = worldEntityService;
        this.entityFactory = entityFactory;
        this.hexMapRenderer = hexMapRenderer;
        this.inputManager = inputManager;
        this.viewport = viewport;
        this.characterEntityService = characterEntityService;
        this.performanceMonitor = performanceMonitor;
        this.gameplaySettings = gameplaySettings;
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
        this.generationContext = generationContext;
    }


    @Override
    public LoadingScreen createLoadingScreen() {
        return new LoadingScreen(
            assetService,
            spriteBatch,
            font,
            screenSwitcher,
            worldEntityService,
            viewport
        );
    }

    @Override
    public GameScreen createGameScreen() {
        return new GameScreen(
            engine,
            entityFactory,
            worldEntityService,
            inputManager,
            viewport,
            characterEntityService,
            performanceMonitor,
            gameplaySettings,
            shapeRenderer,
            camera,
            generationContext

            );
    }

    @Override
    public Screen createBattleScreen() {
        // Возвращаем заглушку вместо исключения
        return new Screen() {
            @Override
            public void show() {
            }

            @Override
            public void render(float delta) {
                // Простой экран с сообщением
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                spriteBatch.begin();
                font.draw(spriteBatch, "Battle Screen - Not Implemented Yet", 100, 100);
                spriteBatch.end();
            }

            @Override
            public void resize(int width, int height) {
            }

            @Override
            public void pause() {
            }

            @Override
            public void resume() {
            }

            @Override
            public void hide() {
            }

            @Override
            public void dispose() {
            }
        };
    }
}
