package io.github.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.game.di.AppComponent;
import io.github.game.di.DaggerAppComponent;
import io.github.game.di.modules.CoreModule;
import io.github.game.ui.screens.GameScreen;
import io.github.game.ui.screens.LoadingScreen;
import io.github.game.ui.screens.ScreenFactory;
import io.github.game.ui.screens.ScreenSwitcher;
import io.github.game.utils.GameSettings;
import javax.inject.Inject;

public class MainGame extends Game implements ScreenSwitcher {

    @Inject
    ScreenFactory screenFactory;
    @Inject
    SpriteBatch spriteBatch;
    @Inject
    GameSettings gameSettings;

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

        // Применяем графические настройки
        applyGraphicsSettings();

        // Создаем экраны через фабрику
        loadingScreen = screenFactory.createLoadingScreen();
        // GameScreen создадим позже, после загрузки ресурсов

        // Установка экрана загрузки
        switchToLoadingScreen();
    }

    private void applyGraphicsSettings() {
        // Применяем настройки графики
        if (gameSettings.isFullscreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(
                gameSettings.getResolutionWidth(),
                gameSettings.getResolutionHeight()
            );
        }

        Gdx.graphics.setVSync(gameSettings.isVsync());
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
    public void resize(int width, int height) {
        super.resize(width, height);
        // При изменении размера окна можно обновить настройки
        if (!gameSettings.isFullscreen()) {
            gameSettings.setResolutionWidth(width);
            gameSettings.setResolutionHeight(height);
        }
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

        // Принудительный вызов сборщика мусора
        System.gc();
    }
}
