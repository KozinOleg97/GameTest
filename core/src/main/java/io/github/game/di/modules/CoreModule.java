package io.github.game.di.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;
import io.github.game.MainGame;
import io.github.game.ui.screens.ScreenSwitcher;
import io.github.game.utils.ResourceManager;
import javax.inject.Singleton;

@Module
public class CoreModule {

    private final MainGame game;

    public CoreModule(MainGame game) {
        this.game = game;
    }


    @Provides
    @Singleton
    MainGame provideMainGame() {
        return game;
    }

    @Provides
    @Singleton
    ScreenSwitcher provideScreenSwitcher(MainGame game) {
        return game;
    }

    @Provides
    @Singleton
    BitmapFont provideBitmapFont() {
        // Создаем базовый шрифт
        return new BitmapFont();

        // Или загружаем из файла, если нужно:
        // return new BitmapFont(Gdx.files.internal("fonts/myfont.fnt"));
    }


    @Provides
    @Singleton
    Input provideInput() {
        return Gdx.input;
    }

    @Provides
    @Singleton
    SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }

    @Provides
    @Singleton
    ResourceManager provideResourceManager() {
        return new ResourceManager();
    }
}
