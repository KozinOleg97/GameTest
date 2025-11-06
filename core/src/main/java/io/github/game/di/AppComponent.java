package io.github.game.di;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Component;
import io.github.game.MainGame;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.di.modules.CoreModule;
import io.github.game.di.modules.GameModule;
import io.github.game.di.modules.ScreenModule;
import io.github.game.di.modules.SettingsModule;
import io.github.game.di.modules.WorldModule;
import io.github.game.monitoring.PerformanceMonitor;
import io.github.game.services.AssetService;
import io.github.game.settings.AudioSettings;
import io.github.game.settings.CameraSettings;
import io.github.game.settings.GameplaySettings;
import io.github.game.settings.GraphicsSettings;
import io.github.game.settings.impl.SettingsFacade;
import io.github.game.ui.screens.LoadingScreen;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
    CoreModule.class,
    GameModule.class,
    ScreenModule.class,
    WorldModule.class,
    SettingsModule.class
})
public interface AppComponent {

    void inject(MainGame game);


    PerformanceMonitor performanceMonitor();

    GraphicsSettings graphicsSettings();

    AudioSettings audioSettings();

    GameplaySettings gameplaySettings();

    CameraSettings cameraSettings();

    SettingsFacade settingsFacade();

    // Для экрана загрузки
    SpriteBatch spriteBatch();

    AssetService assetService();

    LoadingScreen loadingScreen();

    GenerationContext generationContext();
}
