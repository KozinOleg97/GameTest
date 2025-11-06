package io.github.game.di.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import dagger.Module;
import dagger.Provides;
import io.github.game.MainGame;
import io.github.game.ui.screens.ScreenSwitcher;
import io.github.game.utils.ResourceManager;
import javax.inject.Singleton;

/**
 * Основные компоненты приложения
 */
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
        final String FONT_CHARS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/GoodHeadPro-Light.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 18;

        parameter.color = Color.WHITE;
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;

        parameter.characters = FONT_CHARS;
        BitmapFont font12 = generator.generateFont(parameter);
        generator.dispose();

        return font12;
    }


    @Provides
    @Singleton
    Input provideInput() {
        return Gdx.input;
    }


    @Provides
    @Singleton
    ResourceManager provideResourceManager() {
        return new ResourceManager();
    }
}
