package io.github.game.di;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Component;
import io.github.game.MainGame;
import io.github.game.di.modules.CoreModule;
import io.github.game.di.modules.ECSModule;
import io.github.game.di.modules.ScreenModule;
import io.github.game.di.modules.ServicesModule;
import io.github.game.di.modules.WorldModule;
import io.github.game.di.modules.GraphicsModule;
import io.github.game.services.AssetService;
import io.github.game.ui.screens.LoadingScreen;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
    CoreModule.class,
    ServicesModule.class,
    ECSModule.class,
    ScreenModule.class,
    WorldModule.class,
    GraphicsModule.class
})
public interface AppComponent {

    void inject(MainGame game);

    // Для экрана загрузки
    SpriteBatch spriteBatch();

    AssetService assetService();

    LoadingScreen loadingScreen();
}
