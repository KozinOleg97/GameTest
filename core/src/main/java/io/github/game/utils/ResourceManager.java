package io.github.game.utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import javax.inject.Singleton;


@Singleton
public class ResourceManager {
    private final AssetManager assetManager = new AssetManager();

    // Загрузка текстур
    public void loadTextures() {
        assetManager.load("textures/ball.png", Texture.class);
    }

    // Получение текстуры
    public Texture getTexture(String path) {
        return assetManager.get(path, Texture.class);
    }

    // Обязательно вызывайте assetManager.update() в основном цикле, если загрузка асинхронная.
    public void finishLoading() {
        assetManager.finishLoading();
    }

    public boolean update (){
        return assetManager.update();
    }

    public void checkLoadStatus() {
        if (assetManager.isFinished()) {
            Gdx.app.log("ASSETS", "Textures loaded");
        }else {Gdx.app.log("ASSETS", "Textures NOT loaded");}
    }

    public float getProgress() {
        return assetManager.getProgress();
    }
}
