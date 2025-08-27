package io.github.game.di.modules;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;
import io.github.game.monitoring.PerformanceMonitor;
import io.github.game.settings.GraphicsSettings;
import javax.inject.Singleton;

@Module
public class MonitoringModule {

    @Provides
    @Singleton
    PerformanceMonitor providePerformanceMonitor(GraphicsSettings graphicsSettings,
                                                 SpriteBatch spriteBatch,
                                                 BitmapFont font) {
        return new PerformanceMonitor(graphicsSettings, spriteBatch, font);
    }
}
