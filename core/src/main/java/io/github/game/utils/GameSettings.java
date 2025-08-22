package io.github.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;

@Singleton
public class GameSettings {

    private final Preferences prefs;

    // Основные настройки игры
    @Getter
    private float speedMultiplier;

    // Настройки камеры
    @Getter
    private float cameraMoveSpeed;
    @Getter
    private float cameraMinZoom;
    @Getter
    private float cameraMaxZoom;
    @Getter
    private float cameraZoomSensitivity;

    // Настройки отладки
    @Getter
    private boolean debugMode;

    // Настройки интерфейса
    @Getter
    private float uiScale;

    // Настройки звука
    @Getter
    private float masterVolume;
    @Getter
    private float musicVolume;
    @Getter
    private float sfxVolume;

    // Настройки графики
    @Getter
    private int resolutionWidth;
    @Getter
    private int resolutionHeight;
    @Getter
    private boolean fullscreen;
    @Getter
    private boolean vsync;
    @Getter
    private String viewportType;


    @Inject
    public GameSettings() {
        this.prefs = Gdx.app.getPreferences(GameSettingsConstants.PREFS_NAME);
        loadAllSettings();
    }

    /**
     * Загружает все настройки из Preferences
     */
    private void loadAllSettings() {
        this.speedMultiplier = prefs.getFloat(
            GameSettingsConstants.KEY_SPEED_MULTIPLIER,
            GameSettingsConstants.DEFAULT_SPEED_MULTIPLIER
        );

        this.cameraMoveSpeed = prefs.getFloat(
            GameSettingsConstants.KEY_CAMERA_MOVE_SPEED,
            GameSettingsConstants.DEFAULT_CAMERA_MOVE_SPEED
        );

        this.cameraMinZoom = prefs.getFloat(
            GameSettingsConstants.KEY_CAMERA_MIN_ZOOM,
            GameSettingsConstants.DEFAULT_CAMERA_MIN_ZOOM
        );

        this.cameraMaxZoom = prefs.getFloat(
            GameSettingsConstants.KEY_CAMERA_MAX_ZOOM,
            GameSettingsConstants.DEFAULT_CAMERA_MAX_ZOOM
        );

        this.cameraZoomSensitivity = prefs.getFloat(
            GameSettingsConstants.KEY_CAMERA_ZOOM_SENSITIVITY,
            GameSettingsConstants.DEFAULT_CAMERA_ZOOM_SENSITIVITY
        );

        this.debugMode = prefs.getBoolean(
            GameSettingsConstants.KEY_DEBUG_MODE,
            GameSettingsConstants.DEFAULT_DEBUG_MODE
        );

        this.uiScale = prefs.getFloat(
            GameSettingsConstants.KEY_UI_SCALE,
            GameSettingsConstants.DEFAULT_UI_SCALE
        );

        this.masterVolume = prefs.getFloat(
            GameSettingsConstants.KEY_MASTER_VOLUME,
            GameSettingsConstants.DEFAULT_MASTER_VOLUME
        );

        this.musicVolume = prefs.getFloat(
            GameSettingsConstants.KEY_MUSIC_VOLUME,
            GameSettingsConstants.DEFAULT_MUSIC_VOLUME
        );

        this.sfxVolume = prefs.getFloat(
            GameSettingsConstants.KEY_SFX_VOLUME,
            GameSettingsConstants.DEFAULT_SFX_VOLUME
        );

        this.resolutionWidth = prefs.getInteger(
            GameSettingsConstants.KEY_RESOLUTION_WIDTH,
            GameSettingsConstants.DEFAULT_RESOLUTION_WIDTH
        );
        this.resolutionHeight = prefs.getInteger(
            GameSettingsConstants.KEY_RESOLUTION_HEIGHT,
            GameSettingsConstants.DEFAULT_RESOLUTION_HEIGHT
        );
        this.fullscreen = prefs.getBoolean(
            GameSettingsConstants.KEY_FULLSCREEN,
            GameSettingsConstants.DEFAULT_FULLSCREEN
        );
        this.vsync = prefs.getBoolean(
            GameSettingsConstants.KEY_VSYNC,
            GameSettingsConstants.DEFAULT_VSYNC
        );
        this.viewportType = prefs.getString(
            GameSettingsConstants.KEY_VIEWPORT_TYPE,
            GameSettingsConstants.DEFAULT_VIEWPORT_TYPE
        );
    }

    /**
     * Сохраняет все настройки в Preferences
     */
    public void saveAllSettings() {
        prefs.putFloat(GameSettingsConstants.KEY_SPEED_MULTIPLIER, speedMultiplier);
        prefs.putFloat(GameSettingsConstants.KEY_CAMERA_MOVE_SPEED, cameraMoveSpeed);
        prefs.putFloat(GameSettingsConstants.KEY_CAMERA_MIN_ZOOM, cameraMinZoom);
        prefs.putFloat(GameSettingsConstants.KEY_CAMERA_MAX_ZOOM, cameraMaxZoom);
        prefs.putFloat(GameSettingsConstants.KEY_CAMERA_ZOOM_SENSITIVITY, cameraZoomSensitivity);
        prefs.putBoolean(GameSettingsConstants.KEY_DEBUG_MODE, debugMode);
        prefs.putFloat(GameSettingsConstants.KEY_UI_SCALE, uiScale);
        prefs.putFloat(GameSettingsConstants.KEY_MASTER_VOLUME, masterVolume);
        prefs.putFloat(GameSettingsConstants.KEY_MUSIC_VOLUME, musicVolume);
        prefs.putFloat(GameSettingsConstants.KEY_SFX_VOLUME, sfxVolume);

        // Графические настройки
        prefs.putInteger(GameSettingsConstants.KEY_RESOLUTION_WIDTH, resolutionWidth);
        prefs.putInteger(GameSettingsConstants.KEY_RESOLUTION_HEIGHT, resolutionHeight);
        prefs.putBoolean(GameSettingsConstants.KEY_FULLSCREEN, fullscreen);
        prefs.putBoolean(GameSettingsConstants.KEY_VSYNC, vsync);
        prefs.putString(GameSettingsConstants.KEY_VIEWPORT_TYPE, viewportType);
        prefs.flush();
    }

    // Вспомогательные методы для валидации


    /**
     * Универсальный метод валидации числовых значений в заданном диапазоне. Поддерживает Integer,
     * Float и другие числовые типы.
     *
     * @param <T>         Числовой тип (Integer, Float, Double, etc.)
     * @param value       Значение для валидации
     * @param min         Минимальное допустимое значение
     * @param max         Максимальное допустимое значение
     * @param settingName Название настройки для логгирования
     * @return Значение, ограниченное диапазоном [min, max]
     */
//    @SuppressWarnings("unchecked")
    private <T extends Number & Comparable<T>> T validateRange(T value, T min, T max,
                                                               String settingName) {
        if (value.compareTo(min) < 0) {
            Gdx.app.debug("GameSettings",
                settingName + " value " + value + " is below minimum " + min + ", clamping.");
            return min;
        }
        if (value.compareTo(max) > 0) {
            Gdx.app.debug("GameSettings",
                settingName + " value " + value + " is above maximum " + max + ", clamping.");
            return max;
        }
        return value;
    }

    private float validateVolume(float volume) {
        return validateRange(volume,
            GameSettingsConstants.MIN_VOLUME,
            GameSettingsConstants.MAX_VOLUME,
            "Volume"
        );
    }

    private float validateZoom(float zoom) {
        return validateRange(zoom,
            GameSettingsConstants.MIN_CAMERA_ZOOM,
            GameSettingsConstants.MAX_CAMERA_ZOOM,
            "Zoom"
        );
    }

    // Методы для установки значений с автоматическим сохранением и валидацией

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = validateRange(speedMultiplier,
            GameSettingsConstants.MIN_SPEED_MULTIPLIER,
            GameSettingsConstants.MAX_SPEED_MULTIPLIER,
            "Speed multiplier"
        );
        prefs.putFloat(GameSettingsConstants.KEY_SPEED_MULTIPLIER, this.speedMultiplier);
        prefs.flush();
    }

    public void setCameraMoveSpeed(float cameraMoveSpeed) {
        this.cameraMoveSpeed = validateRange(cameraMoveSpeed,
            GameSettingsConstants.MIN_CAMERA_MOVE_SPEED,
            GameSettingsConstants.MAX_CAMERA_MOVE_SPEED,
            "Camera move speed"
        );
        prefs.putFloat(GameSettingsConstants.KEY_CAMERA_MOVE_SPEED, this.cameraMoveSpeed);
        prefs.flush();
    }

    public void setCameraMinZoom(float cameraMinZoom) {
        this.cameraMinZoom = validateZoom(cameraMinZoom);
        // Убедимся, что минимальный зум не больше максимального
        if (this.cameraMinZoom > this.cameraMaxZoom) {
            this.cameraMaxZoom = this.cameraMinZoom;
            prefs.putFloat(GameSettingsConstants.KEY_CAMERA_MAX_ZOOM, this.cameraMaxZoom);
        }
        prefs.putFloat(GameSettingsConstants.KEY_CAMERA_MIN_ZOOM, this.cameraMinZoom);
        prefs.flush();
    }

    public void setCameraMaxZoom(float cameraMaxZoom) {
        this.cameraMaxZoom = validateZoom(cameraMaxZoom);
        // Убедимся, что максимальный зум не меньше минимального
        if (this.cameraMaxZoom < this.cameraMinZoom) {
            this.cameraMinZoom = this.cameraMaxZoom;
            prefs.putFloat(GameSettingsConstants.KEY_CAMERA_MIN_ZOOM, this.cameraMinZoom);
        }
        prefs.putFloat(GameSettingsConstants.KEY_CAMERA_MAX_ZOOM, this.cameraMaxZoom);
        prefs.flush();
    }

    public void setCameraZoomSensitivity(float cameraZoomSensitivity) {
        this.cameraZoomSensitivity = validateRange(cameraZoomSensitivity,
            GameSettingsConstants.MIN_ZOOM_SENSITIVITY,
            GameSettingsConstants.MAX_ZOOM_SENSITIVITY,
            "Zoom sensitivity"
        );
        prefs.putFloat(GameSettingsConstants.KEY_CAMERA_ZOOM_SENSITIVITY,
            this.cameraZoomSensitivity);
        prefs.flush();
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        prefs.putBoolean(GameSettingsConstants.KEY_DEBUG_MODE, debugMode);
        prefs.flush();
    }

    public void setUiScale(float uiScale) {
        this.uiScale = validateRange(uiScale,
            GameSettingsConstants.MIN_UI_SCALE,
            GameSettingsConstants.MAX_UI_SCALE,
            "UI scale"
        );
        prefs.putFloat(GameSettingsConstants.KEY_UI_SCALE, this.uiScale);
        prefs.flush();
    }

    public void setMasterVolume(float masterVolume) {
        this.masterVolume = validateVolume(masterVolume);
        prefs.putFloat(GameSettingsConstants.KEY_MASTER_VOLUME, this.masterVolume);
        prefs.flush();
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = validateVolume(musicVolume);
        prefs.putFloat(GameSettingsConstants.KEY_MUSIC_VOLUME, this.musicVolume);
        prefs.flush();
    }

    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = validateVolume(sfxVolume);
        prefs.putFloat(GameSettingsConstants.KEY_SFX_VOLUME, this.sfxVolume);
        prefs.flush();
    }

    // Методы для установки графических настроек

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = validateRange(
            resolutionWidth,
            GameSettingsConstants.MIN_RESOLUTION_WIDTH,
            GameSettingsConstants.MAX_RESOLUTION_WIDTH,
            "Resolution width"
        );
        prefs.putInteger(GameSettingsConstants.KEY_RESOLUTION_WIDTH, this.resolutionWidth);
        prefs.flush();
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = validateRange(
            resolutionHeight,
            GameSettingsConstants.MIN_RESOLUTION_HEIGHT,
            GameSettingsConstants.MAX_RESOLUTION_HEIGHT,
            "Resolution height"
        );
        prefs.putInteger(GameSettingsConstants.KEY_RESOLUTION_HEIGHT, this.resolutionHeight);
        prefs.flush();
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        prefs.putBoolean(GameSettingsConstants.KEY_FULLSCREEN, fullscreen);
        prefs.flush();
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        prefs.putBoolean(GameSettingsConstants.KEY_VSYNC, vsync);
        prefs.flush();
    }

    public void setViewportType(String viewportType) {
        // Валидация типа вьюпорта
        if (!viewportType.equals(GameSettingsConstants.VIEWPORT_TYPE_FIT) &&
            !viewportType.equals(GameSettingsConstants.VIEWPORT_TYPE_SCREEN) &&
            !viewportType.equals(GameSettingsConstants.VIEWPORT_TYPE_STRETCH)) {
            Gdx.app.debug("GameSettings",
                "Invalid viewport type: " + viewportType + ", using default.");
            this.viewportType = GameSettingsConstants.DEFAULT_VIEWPORT_TYPE;
        } else {
            this.viewportType = viewportType;
        }
        prefs.putString(GameSettingsConstants.KEY_VIEWPORT_TYPE, this.viewportType);
        prefs.flush();
    }

    /**
     * Сбрасывает все настройки к значениям по умолчанию
     */
    public void resetToDefaults() {
        this.speedMultiplier = GameSettingsConstants.DEFAULT_SPEED_MULTIPLIER;
        this.cameraMoveSpeed = GameSettingsConstants.DEFAULT_CAMERA_MOVE_SPEED;
        this.cameraMinZoom = GameSettingsConstants.DEFAULT_CAMERA_MIN_ZOOM;
        this.cameraMaxZoom = GameSettingsConstants.DEFAULT_CAMERA_MAX_ZOOM;
        this.cameraZoomSensitivity = GameSettingsConstants.DEFAULT_CAMERA_ZOOM_SENSITIVITY;
        this.debugMode = GameSettingsConstants.DEFAULT_DEBUG_MODE;
        this.uiScale = GameSettingsConstants.DEFAULT_UI_SCALE;
        this.masterVolume = GameSettingsConstants.DEFAULT_MASTER_VOLUME;
        this.musicVolume = GameSettingsConstants.DEFAULT_MUSIC_VOLUME;
        this.sfxVolume = GameSettingsConstants.DEFAULT_SFX_VOLUME;

        saveAllSettings();
    }

    /**
     * Валидирует все текущие значения настроек
     */
    public void validateAllSettings() {
        setSpeedMultiplier(this.speedMultiplier);
        setCameraMoveSpeed(this.cameraMoveSpeed);
        setCameraMinZoom(this.cameraMinZoom);
        setCameraMaxZoom(this.cameraMaxZoom);
        setCameraZoomSensitivity(this.cameraZoomSensitivity);
        setUiScale(this.uiScale);
        setMasterVolume(this.masterVolume);
        setMusicVolume(this.musicVolume);
        setSfxVolume(this.sfxVolume);
        setResolutionWidth(this.resolutionWidth);
        setResolutionHeight(this.resolutionHeight);
    }
}
