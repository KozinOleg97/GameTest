package io.github.game.di.modules;

import dagger.Module;
import dagger.Provides;
import io.github.game.settings.AudioSettings;
import io.github.game.settings.CameraSettings;
import io.github.game.settings.GameplaySettings;
import io.github.game.settings.GraphicsSettings;
import io.github.game.settings.impl.SettingsFacade;
import javax.inject.Singleton;

@Module
public class SettingsModule {

    @Provides
    @Singleton
    SettingsFacade provideSettingsFacade() {
        return new SettingsFacade();
    }

    @Provides
    @Singleton
    GraphicsSettings provideGraphicsSettings(SettingsFacade settingsFacade) {
        return settingsFacade;
    }

    @Provides
    @Singleton
    AudioSettings provideAudioSettings(SettingsFacade settingsFacade) {
        return settingsFacade;
    }

    @Provides
    @Singleton
    GameplaySettings provideGameplaySettings(SettingsFacade settingsFacade) {
        return settingsFacade;
    }

    @Provides
    @Singleton
    CameraSettings provideCameraSettings(SettingsFacade settingsFacade) {
        return settingsFacade;
    }
}
