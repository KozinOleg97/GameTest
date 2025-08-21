package io.github.game.di.modules;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.renderer.HexMapRenderer;
import javax.inject.Singleton;

@Module
public class GraphicsModule {

    @Provides
    @Singleton
    OrthographicCamera provideCamera() {
        OrthographicCamera camera = new OrthographicCamera();
        // Устанавливаем начальные параметры камеры
        camera.setToOrtho(false, 800, 480);
        camera.position.set(400, 240, 0); // Центрируем камеру
        camera.update(); // Важно: обновляем камеру после изменения параметров
        return camera;
    }

    @Provides
    @Singleton
    Viewport provideViewport(OrthographicCamera camera) {
        FitViewport viewport = new FitViewport(800, 480, camera);
        return viewport;
    }

    @Provides
    @Singleton
    ShapeRenderer provideShapeRenderer() {
        return new ShapeRenderer();
    }

    @Provides
    @Singleton
    HexMapRenderer provideHexMapRenderer(HexMap hexMap, ShapeRenderer shapeRenderer,
                                         OrthographicCamera camera) {
        return new HexMapRenderer(hexMap, shapeRenderer, camera);
    }
}
