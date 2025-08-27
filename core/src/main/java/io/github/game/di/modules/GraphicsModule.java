package io.github.game.di.modules;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.renderer.HexMapRenderer;
import io.github.game.settings.GraphicsSettings;
import io.github.game.utils.GameSettingsConstants;
import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class GraphicsModule {

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

    @Provides
    @Singleton
    HexMapRenderer provideHexMapRenderer(HexMap hexMap,
                                         ShapeRenderer shapeRenderer,
                                         OrthographicCamera camera) {
        return new HexMapRenderer(hexMap, shapeRenderer, camera);
    }

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
}
