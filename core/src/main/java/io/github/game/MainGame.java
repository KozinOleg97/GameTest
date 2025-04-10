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

        // Адаптер использует внедренный сервис
        GameInputAdapter inputAdapter = new GameInputAdapter(inputService);
        Gdx.input.setInputProcessor(inputAdapter);

        // Загрузка текстур
        resourceManager.loadTextures();
        while (!resourceManager.update()) {
          Gdx.app.log("ASSETS", MessageFormat.format("loaded {0}%", resourceManager.getProgress() * 100));
        }
        Gdx.app.log("ASSETS", MessageFormat.format("loaded {0}%", resourceManager.getProgress() * 100));


       gameSettings.save();


        setScreen(lazyGameScreen.get()); // Экран создается через Dagger
    }
}

