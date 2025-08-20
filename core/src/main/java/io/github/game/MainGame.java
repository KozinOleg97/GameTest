package io.github.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import dagger.Lazy;
import io.github.game.di.AppComponent;
import io.github.game.di.DaggerAppComponent;
import io.github.game.services.GameInputAdapter;
import io.github.game.services.InputService;
import io.github.game.ui.screens.GameScreen;
import io.github.game.utils.GameSettings;
import io.github.game.utils.ResourceManager;
import java.text.MessageFormat;
import javax.inject.Inject;


public class MainGame extends Game {

    @Inject
    ResourceManager resourceManager;

    @Inject
    InputService inputService;

    @Inject
    Lazy<GameScreen> lazyGameScreen;

    @Inject
    GameSettings gameSettings;

    private AppComponent appComponent;

    @Override
    public void create() {
        // Инициализация Dagger
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        // Установка ввода
        Gdx.input.setInputProcessor(new GameInputAdapter(inputService));

        // Загрузка текстур
        resourceManager.loadTextures();
        while (!resourceManager.update()) {
            logAssetsLoadProgress();
        }
        logAssetsLoadProgress();

        setScreen(lazyGameScreen.get()); // Экран создается через Dagger
    }

    private void logAssetsLoadProgress() {
        Gdx.app.log("ASSETS",
            MessageFormat.format("loaded {0}%", resourceManager.getProgress() * 100));
    }
}

