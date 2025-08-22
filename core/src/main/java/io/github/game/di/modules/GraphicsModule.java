package io.github.game.di.modules;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.renderer.HexMapRenderer;
import io.github.game.utils.GameSettings;
import io.github.game.utils.GameSettingsConstants;
import javax.inject.Singleton;

@Module
public class GraphicsModule {

    @Provides
    @Singleton
    OrthographicCamera provideCamera(GameSettings gameSettings) {
        OrthographicCamera camera = new OrthographicCamera();
        // Устанавливаем начальные параметры камеры
        camera.setToOrtho(false, gameSettings.getResolutionWidth(),
                          gameSettings.getResolutionHeight());
        camera.position.set(
            gameSettings.getResolutionWidth() / 2f,
            gameSettings.getResolutionHeight() / 2f,
            0
        );
        camera.update();
        return camera;
    }

    @Provides
    @Singleton
    Viewport provideViewport(OrthographicCamera camera, GameSettings gameSettings) {
        // Создаем viewport в зависимости от настроек
        return switch (gameSettings.getViewportType()) {
            case GameSettingsConstants.VIEWPORT_TYPE_SCREEN -> new ScreenViewport(camera);
            case GameSettingsConstants.VIEWPORT_TYPE_STRETCH -> new StretchViewport(
                gameSettings.getResolutionWidth(),
                gameSettings.getResolutionHeight(),
                camera
            );
            default -> new FitViewport(
                gameSettings.getResolutionWidth(),
                gameSettings.getResolutionHeight(),
                camera
            );
        };
    }


    @Provides
    @Singleton
    ShapeRenderer provideShapeRenderer() {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        // Можно установить дополнительные параметры рендерера, если нужно
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
}
