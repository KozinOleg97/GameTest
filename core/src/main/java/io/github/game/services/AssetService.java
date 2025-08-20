package io.github.game.services;

import io.github.game.utils.ResourceManager;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Сервис для управления загрузкой игровых ресурсов. Предоставляет высокоуровневый API для работы с
 * ресурсами.
 */
@Singleton
public class AssetService {

    private final ResourceManager resourceManager;
    private boolean assetsLoaded = false;

    @Inject
    public AssetService(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * Начинает асинхронную загрузку всех ресурсов игры
     */
    public void loadAssets() {
        if (!assetsLoaded) {
            resourceManager.loadTextures();
            // Здесь можно добавить загрузку других типов ресурсов
            assetsLoaded = true;
        }
    }

    /**
     * Обновляет процесс загрузки ресурсов
     *
     * @return true если все ресурсы загружены
     */
    public boolean update() {
        return resourceManager.update();
    }

    /**
     * Возвращает прогресс загрузки ресурсов
     */
    public float getProgress() {
        return resourceManager.getProgress();
    }

    /**
     * Проверяет, завершена ли загрузка всех ресурсов
     */
    public boolean isFinished() {
        return resourceManager.isFinished();
    }

    /**
     * Блокирует поток до завершения загрузки всех ресурсов
     */
    public void finishLoading() {
        resourceManager.finishLoading();
    }

    /**
     * Возвращает менеджер ресурсов для прямого доступа
     */
    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
