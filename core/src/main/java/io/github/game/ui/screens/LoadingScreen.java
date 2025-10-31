package io.github.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.services.AssetService;
import io.github.game.services.WorldEntityService;
import javax.inject.Inject;

public class LoadingScreen implements Screen {

    private final AssetService assetService;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final ScreenSwitcher screenSwitcher;
    private final WorldEntityService worldEntityService;
    private final Viewport viewport;

    @Inject
    public LoadingScreen(AssetService assetService,
                         SpriteBatch spriteBatch,
                         BitmapFont font,
                         ScreenSwitcher screenSwitcher,
                         WorldEntityService worldEntityService,
                         Viewport viewport) {
        this.assetService = assetService;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.screenSwitcher = screenSwitcher;
        this.worldEntityService = worldEntityService;
        this.viewport = viewport;
    }

    @Override
    public void show() {
        // Начинаем загрузку ресурсов при показе экрана
        assetService.loadAssets();

        GenerationContext generationContext = worldEntityService.generateAll();
    }

    @Override
    public void render(float delta) {
        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        // Обновление процесса загрузки
        boolean finished = assetService.update();
        float progress = assetService.getProgress();

        // Отрисовка прогресса загрузки
        spriteBatch.begin();
        spriteBatch.setColor(Color.CORAL);
        font.draw(spriteBatch, "Battle Screen - Not Implemented Yet", 100, 100);

        font.draw(spriteBatch, "Loading: " + (int) (progress * 100) + "%",
                  (float) Gdx.graphics.getWidth() / 2 - 50,
                  (float) Gdx.graphics.getHeight() / 2);
        spriteBatch.end();

        // Если загрузка завершена, переключаемся на главный экран
        if (finished) {
            screenSwitcher.switchToGameScreen();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Обработка изменения размера основного viewport
        viewport.update(width, height, true);
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
}
