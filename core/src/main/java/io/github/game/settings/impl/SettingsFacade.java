package io.github.game.settings.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import io.github.game.settings.AudioSettings;
import io.github.game.settings.CameraSettings;
import io.github.game.settings.GameplaySettings;
import io.github.game.settings.GraphicsSettings;
import io.github.game.utils.GameSettingsConstants;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Фасад для доступа ко всем настройкам игры. Реализует все интерфейсы настроек и делегирует вызовы
 * конкретным реализациям.
 */
@Singleton
public class SettingsFacade implements GraphicsSettings, AudioSettings,
    GameplaySettings, CameraSettings {

    private final GraphicsSettings graphics;
    private final AudioSettings audio;
    private final GameplaySettings gameplay;
    private final CameraSettings camera;

    @Inject
    public SettingsFacade() {
        Preferences prefs = Gdx.app.getPreferences(GameSettingsConstants.PREFS_NAME);
        this.graphics = new GraphicsSettingsImpl(prefs);
        this.audio = new AudioSettingsImpl(prefs);
        this.gameplay = new GameplaySettingsImpl(prefs);
        this.camera = new CameraSettingsImpl(prefs);
    }

    /**
     * Загружает все настройки
     */
    public void loadAll() {
        graphics.load();
        audio.load();
        gameplay.load();
        camera.load();
    }

    /**
     * Сохраняет все настройки
     */
    public void saveAll() {
        graphics.save();
        audio.save();
        gameplay.save();
        camera.save();
    }

    /**
     * Сбрасывает все настройки к значениям по умолчанию
     */
    public void resetAllToDefaults() {
        graphics.resetToDefaults();
        audio.resetToDefaults();
        gameplay.resetToDefaults();
        camera.resetToDefaults();
        saveAll();
    }

    // Делегирование методов GraphicsSettings
    @Override
    public int getResolutionWidth() {
        return graphics.getResolutionWidth();
    }

    @Override
    public void setResolutionWidth(int width) {
        graphics.setResolutionWidth(width);
    }

    @Override
    public int getResolutionHeight() {
        return graphics.getResolutionHeight();
    }

    @Override
    public void setResolutionHeight(int height) {
        graphics.setResolutionHeight(height);
    }

    @Override
    public boolean isFullscreen() {
        return graphics.isFullscreen();
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        graphics.setFullscreen(fullscreen);
    }

    @Override
    public boolean isVsync() {
        return graphics.isVsync();
    }

    @Override
    public void setVsync(boolean vsync) {
        graphics.setVsync(vsync);
    }

    @Override
    public String getViewportType() {
        return graphics.getViewportType();
    }

    @Override
    public void setViewportType(String viewportType) {
        graphics.setViewportType(viewportType);
    }

    @Override
    public float getUiScale() {
        return graphics.getUiScale();
    }

    @Override
    public void setUiScale(float scale) {
        graphics.setUiScale(scale);
    }

    @Override
    public boolean isDebugMode() {
        return graphics.isDebugMode();
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        graphics.setDebugMode(debugMode);
    }

    // Делегирование методов AudioSettings
    @Override
    public float getMasterVolume() {
        return audio.getMasterVolume();
    }

    @Override
    public void setMasterVolume(float volume) {
        audio.setMasterVolume(volume);
    }

    @Override
    public float getMusicVolume() {
        return audio.getMusicVolume();
    }

    @Override
    public void setMusicVolume(float volume) {
        audio.setMusicVolume(volume);
    }

    @Override
    public float getSfxVolume() {
        return audio.getSfxVolume();
    }

    @Override
    public void setSfxVolume(float volume) {
        audio.setSfxVolume(volume);
    }

    // Делегирование методов GameplaySettings
    @Override
    public float getSpeedMultiplier() {
        return gameplay.getSpeedMultiplier();
    }

    @Override
    public void setSpeedMultiplier(float multiplier) {
        gameplay.setSpeedMultiplier(multiplier);
    }

    @Override
    public String getDifficulty() {
        return gameplay.getDifficulty();
    }

    @Override
    public void setDifficulty(String difficulty) {
        gameplay.setDifficulty(difficulty);
    }

    @Override
    public boolean isAutoSaveEnabled() {
        return gameplay.isAutoSaveEnabled();
    }

    @Override
    public void setAutoSaveEnabled(boolean enabled) {
        gameplay.setAutoSaveEnabled(enabled);
    }

    @Override
    public int getAutoSaveInterval() {
        return gameplay.getAutoSaveInterval();
    }

    @Override
    public void setAutoSaveInterval(int minutes) {
        gameplay.setAutoSaveInterval(minutes);
    }

    // Делегирование методов CameraSettings
    @Override
    public float getCameraMoveSpeed() {
        return camera.getCameraMoveSpeed();
    }

    @Override
    public void setCameraMoveSpeed(float speed) {
        camera.setCameraMoveSpeed(speed);
    }

    @Override
    public float getCameraMinZoom() {
        return camera.getCameraMinZoom();
    }

    @Override
    public void setCameraMinZoom(float zoom) {
        camera.setCameraMinZoom(zoom);
    }

    @Override
    public float getCameraMaxZoom() {
        return camera.getCameraMaxZoom();
    }

    @Override
    public void setCameraMaxZoom(float zoom) {
        camera.setCameraMaxZoom(zoom);
    }

    @Override
    public float getCameraZoomSensitivity() {
        return camera.getCameraZoomSensitivity();
    }

    @Override
    public void setCameraZoomSensitivity(float sensitivity) {
        camera.setCameraZoomSensitivity(sensitivity);
    }

    @Override
    public float getCameraSmoothFactor() {
        return camera.getCameraSmoothFactor();
    }

    @Override
    public void setCameraSmoothFactor(float smoothTime) {
        camera.setCameraSmoothFactor(smoothTime);
    }

    @Override
    public boolean isCameraSmoothingEnabled() {
        return camera.isCameraSmoothingEnabled();
    }

    @Override
    public void setCameraSmoothingEnabled(boolean enabled) {
        camera.setCameraSmoothingEnabled(enabled);
    }

    // Реализация методов BaseSettings
    @Override
    public void load() {
        loadAll();
    }

    @Override
    public void save() {
        saveAll();
    }

    @Override
    public void resetToDefaults() {
        resetAllToDefaults();
    }
}
