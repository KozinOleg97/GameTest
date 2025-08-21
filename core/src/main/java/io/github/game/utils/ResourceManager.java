package io.github.game.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

/**
 * Управляет загрузкой и доступом к игровым ресурсам. Инкапсулирует работу с AssetManager.
 */
public class ResourceManager implements Disposable {

    private final AssetManager assetManager;

    public ResourceManager() {
        this.assetManager = new AssetManager();
        this.assetManager.setLoader(Texture.class,
            new TextureLoader(new InternalFileHandleResolver()));
    }

    /**
     * Загружает все необходимые текстуры игры
     */
    public void loadTextures() {
        // TODO: Перейти на использование TextureAtlas
        assetManager.load("textures/ball.png", Texture.class);
        assetManager.load("textures/red_ball.png", Texture.class);
    }

    /**
     * Загружает атлас текстур
     */
    public void loadTextureAtlas(String atlasPath) {
        assetManager.load(atlasPath, TextureAtlas.class);
    }

    /**
     * Возвращает текстуру по пути
     */
    public Texture getTexture(String path) {
        return assetManager.get(path, Texture.class);
    }

    /**
     * Возвращает атлас текстур
     */
    public TextureAtlas getTextureAtlas(String atlasPath) {
        return assetManager.get(atlasPath, TextureAtlas.class);
    }

    /**
     * Обновляет процесс загрузки ресурсов
     *
     * @return true если все ресурсы загружены
     */
    public boolean update() {
        return assetManager.update();
    }

    /**
     * Блокирует поток до завершения загрузки всех ресурсов
     */
    public void finishLoading() {
        assetManager.finishLoading();
    }

    /**
     * Возвращает прогресс загрузки ресурсов
     */
    public float getProgress() {
        return assetManager.getProgress();
    }

    /**
     * Проверяет, завершена ли загрузка всех ресурсов
     */
    public boolean isFinished() {
        return assetManager.isFinished();
    }

    /**
     * Освобождает все ресурсы
     */
    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
