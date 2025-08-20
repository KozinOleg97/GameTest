package io.github.game.di;

import com.badlogic.ashley.core.PooledEngine;
import dagger.Component;
import dagger.Lazy;
import io.github.game.MainGame;
import io.github.game.core.world.HexMap;
import io.github.game.di.modules.ECSModule;
import io.github.game.di.modules.GameScreenModule;
import io.github.game.di.modules.GraphicsModule;
import io.github.game.di.modules.InputModule;
import io.github.game.di.modules.ResourceModule;
import io.github.game.di.modules.SettingsModule;
import io.github.game.di.modules.WorldModule;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.systems.RenderingSystem;
import io.github.game.services.InputService;
import io.github.game.ui.screens.GameScreen;
import io.github.game.utils.GameSettings;
import javax.inject.Singleton;


@Singleton
@Component(modules = {
    WorldModule.class,
    SettingsModule.class,
    ResourceModule.class,
    ECSModule.class,
    InputModule.class,
    GraphicsModule.class,
    GameScreenModule.class
})
public interface AppComponent {


    // Инжекция в MainGame:
    void inject(MainGame game);


    HexMap getHexMap();

    // Получение зависимостей для ручного использования:
    Lazy<GameScreen> lazyGameScreen();

    GameScreen gameScreen();

    PooledEngine engine();

    EntityFactory entityFactory();

    RenderingSystem renderingSystem();

    InputService inputService();

    GameSettings gameSettings();

}
