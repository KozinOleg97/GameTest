package io.github.game.di.modules;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;
import io.github.game.ecs.systems.RenderingSystem;
import javax.inject.Singleton;

@Module
public class GraphicsModule {
    @Provides
    @Singleton
    SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }

    @Provides
    @Singleton
    RenderingSystem provideRenderingSystem(SpriteBatch batch) {
        return new RenderingSystem(batch);
    }
}
