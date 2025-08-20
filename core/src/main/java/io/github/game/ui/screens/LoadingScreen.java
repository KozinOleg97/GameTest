package io.github.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.game.services.AssetService;
import javax.inject.Inject;

public class LoadingScreen implements Screen {

    private final AssetService assetService;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final ScreenSwitcher screenSwitcher;

    @Inject
    public LoadingScreen(AssetService assetService,
                         SpriteBatch spriteBatch,
                         BitmapFont font,
                         ScreenSwitcher screenSwitcher) {
        this.assetService = assetService;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.screenSwitcher = screenSwitcher;
    }

    @Override
    public void show() {
        // Начинаем загрузку ресурсов при показе экрана
        assetService.loadAssets();
    }

    @Override
    public void render(float delta) {
        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновление процесса загрузки
        boolean finished = assetService.update();
        float progress = assetService.getProgress();

        // Отрисовка прогресса загрузки
        spriteBatch.begin();
        font.draw(spriteBatch, "Loading: " + (int) (progress * 100) + "%",
            Gdx.graphics.getWidth() / 2 - 50,
            Gdx.graphics.getHeight() / 2);
        spriteBatch.end();

        // Если загрузка завершена, переключаемся на главный экран
        if (finished) {
            screenSwitcher.switchToGameScreen();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // Очистка ресурсов, специфичных для этого экрана
        font.dispose();
    }

    // Остальные методы Screen...
}
