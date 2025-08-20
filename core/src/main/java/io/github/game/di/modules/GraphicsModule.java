package io.github.game.di.modules;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import io.github.game.core.world.HexMap;
import io.github.game.renderer.HexMapRenderer;
import io.github.game.utils.ResourceManager;
import javax.inject.Singleton;

@Module
public class GraphicsModule {
    @Provides
    @Singleton
    OrthographicCamera provideCamera() {
        return new OrthographicCamera();
    }

    @Provides
    @Singleton
    Viewport provideViewport(OrthographicCamera camera) {
        return new FitViewport(800, 480, camera);
    }

    @Provides
    @Singleton
    ShapeRenderer provideShapeRenderer() {
        return new ShapeRenderer();
    }

    @Provides
    @Singleton
    HexMapRenderer provideHexMapRenderer(HexMap hexMap, ShapeRenderer shapeRenderer) {
        return new HexMapRenderer(hexMap, shapeRenderer);
    }
}
