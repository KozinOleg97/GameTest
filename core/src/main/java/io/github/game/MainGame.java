package io.github.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.game.di.AppComponent;
import io.github.game.di.DaggerAppComponent;
import io.github.game.di.modules.CoreModule;
import io.github.game.ui.screens.GameScreen;
import io.github.game.ui.screens.LoadingScreen;
import io.github.game.ui.screens.ScreenFactory;
import io.github.game.ui.screens.ScreenSwitcher;
import javax.inject.Inject;

public class MainGame extends Game implements ScreenSwitcher {

    @Inject
    ScreenFactory screenFactory;
    @Inject
    SpriteBatch spriteBatch;

    private AppComponent appComponent;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;

    @Override
    public void create() {
        // Инициализация Dagger
//        appComponent = DaggerAppComponent.create();
        appComponent = DaggerAppComponent.builder()
            .coreModule(new CoreModule(this))
            .build();
        appComponent.inject(this);

        // Создаем экраны через фабрику
        loadingScreen = screenFactory.createLoadingScreen();
        // GameScreen создадим позже, после загрузки ресурсов

        // Установка экрана загрузки
        switchToLoadingScreen();
    }

    @Override
    public void switchToGameScreen() {
        if (gameScreen == null) {
            gameScreen = screenFactory.createGameScreen();
        }
        setScreen(gameScreen);
    }

    @Override
    public void switchToLoadingScreen() {
        setScreen(loadingScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (loadingScreen != null) {
            loadingScreen.dispose();
            loadingScreen = null;
        }
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
        if (spriteBatch != null) {
            spriteBatch.dispose(); // Освобождаем SpriteBatch
            spriteBatch = null;
        }
    }
}
