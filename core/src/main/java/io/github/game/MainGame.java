package io.github.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.game.di.AppComponent;
import io.github.game.di.DaggerAppComponent;
import io.github.game.di.modules.CoreModule;
import io.github.game.di.modules.GameModule;
import io.github.game.di.modules.ScreenModule;
import io.github.game.di.modules.WorldModule;
import io.github.game.monitoring.PerformanceMonitor;
import io.github.game.settings.GraphicsSettings;
import io.github.game.ui.screens.GameScreen;
import io.github.game.ui.screens.LoadingScreen;
import io.github.game.ui.screens.ScreenFactory;
import io.github.game.ui.screens.ScreenSwitcher;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.inject.Inject;

public class MainGame extends Game implements ScreenSwitcher {

    @Inject
    ScreenFactory screenFactory;
    @Inject
    SpriteBatch spriteBatch;
    @Inject
    GraphicsSettings graphicsSettings;
    @Inject
    PerformanceMonitor performanceMonitor;

    private AppComponent appComponent;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;

    @Override
    public void create() {
        // Инициализация Dagger
        appComponent = DaggerAppComponent.builder().coreModule(new CoreModule(this)).gameModule(new GameModule())
                                         .screenModule(new ScreenModule()).worldModule(new WorldModule()).build();
        appComponent.inject(this);

        // Применяем графические настройки
        applyGraphicsSettings();

        // Создаем экраны через фабрику
        loadingScreen = screenFactory.createLoadingScreen();
        // GameScreen создадим позже, после загрузки ресурсов

        appComponent.generationContext();

        checkEncoding();

        // Установка экрана загрузки
        switchToLoadingScreen();
    }

    public void checkEncoding() {
        System.out.println("File encoding: " + System.getProperty("file.encoding"));
        System.out.println("Console encoding: " + System.getProperty("console.encoding"));
        System.out.println("Default charset: " + Charset.defaultCharset());

        // Тест вывода кириллицы
        String testText = "Тест кириллицы: деревня город";
        System.out.println("Test output: " + testText);

        // Проверка байтов
        byte[] bytes = testText.getBytes(StandardCharsets.UTF_8);
        System.out.println("Bytes: " + Arrays.toString(bytes));
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
            spriteBatch.dispose();
            spriteBatch = null;
        }

        // Явное завершение процесса
        Gdx.app.log("App", "Application is shutting down...");
        Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        // При изменении размера окна можно обновить настройки
        if (!graphicsSettings.isFullscreen()) {
            graphicsSettings.setResolutionWidth(width);
            graphicsSettings.setResolutionHeight(height);
        }

        // Обновляем UI viewport
        performanceMonitor.resize(width, height);
    }

    private void applyGraphicsSettings() {
        // Применяем настройки графики
        if (graphicsSettings.isFullscreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(graphicsSettings.getResolutionWidth(), graphicsSettings.getResolutionHeight());
        }

        Gdx.graphics.setVSync(graphicsSettings.isVsync());
    }
}
