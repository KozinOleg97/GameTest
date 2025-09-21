package io.github.game.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class GameSettingsConstants {

    // Имя файла настроек
    public static final String PREFS_NAME = "game-settings";

    // Префиксы для разных категорий настроек
    public static final String PREFIX_GRAPHICS = "graphics.";
    public static final String PREFIX_AUDIO = "audio.";
    public static final String PREFIX_GAMEPLAY = "gameplay.";
    public static final String PREFIX_CAMERA = "camera.";

    // Ключи для графических настроек
    public static final String KEY_RESOLUTION_WIDTH = "resolutionWidth";
    public static final String KEY_RESOLUTION_HEIGHT = "resolutionHeight";
    public static final String KEY_FULLSCREEN = "fullscreen";
    public static final String KEY_VSYNC = "vsync";
    public static final String KEY_VIEWPORT_TYPE = "viewportType";
    public static final String KEY_UI_SCALE = "uiScale";
    public static final String KEY_DEBUG_MODE = "debugMode";

    // Ключи для аудио настроек
    public static final String KEY_MASTER_VOLUME = "masterVolume";
    public static final String KEY_MUSIC_VOLUME = "musicVolume";
    public static final String KEY_SFX_VOLUME = "sfxVolume";

    // Ключи для настроек игрового процесса
    public static final String KEY_SPEED_MULTIPLIER = "speedMultiplier";
    public static final String KEY_DIFFICULTY = "difficulty";
    public static final String KEY_AUTO_SAVE = "autoSaveEnabled";
    public static final String KEY_AUTO_SAVE_INTERVAL = "autoSaveInterval";
    public static final String KEY_HEX_SIZE_DEBUG = "hexSize";

    // Ключи для настроек камеры
    public static final String KEY_CAMERA_MOVE_SPEED = "cameraMoveSpeed";
    public static final String KEY_CAMERA_MIN_ZOOM = "cameraMinZoom";
    public static final String KEY_CAMERA_MAX_ZOOM = "cameraMaxZoom";
    public static final String KEY_CAMERA_ZOOM_SENSITIVITY = "cameraZoomSensitivity";
    public static final String KEY_CAMERA_SMOOTHING_ENABLED = "cameraSmoothingEnabled";
    public static final String KEY_CAMERA_SMOOTH_FACTOR = "cameraSmoothingFactor";

    // Значения по умолчанию для графических настроек
    public static final int DEFAULT_RESOLUTION_WIDTH = 960;
    public static final int DEFAULT_RESOLUTION_HEIGHT = 720;
    public static final boolean DEFAULT_FULLSCREEN = false;
    public static final boolean DEFAULT_VSYNC = true;
    public static final String DEFAULT_VIEWPORT_TYPE = "fit";
    public static final float DEFAULT_UI_SCALE = 1.0f;
    public static final boolean DEFAULT_DEBUG_MODE = false;

    // Значения по умолчанию для аудио настроек
    public static final float DEFAULT_MASTER_VOLUME = 0.8f;
    public static final float DEFAULT_MUSIC_VOLUME = 0.7f;
    public static final float DEFAULT_SFX_VOLUME = 0.9f;

    // Значения по умолчанию для настроек игрового процесса
    public static final float DEFAULT_SPEED_MULTIPLIER = 100.0f;
    public static final String DEFAULT_DIFFICULTY = "NORMAL";
    public static final boolean DEFAULT_AUTO_SAVE = true;
    public static final int DEFAULT_AUTO_SAVE_INTERVAL = 5;
    public static final int DEFAULT_HEX_SIZE = 200;

    // Значения по умолчанию для настроек камеры
    public static final float DEFAULT_CAMERA_MOVE_SPEED = 300.0f;
    public static final float DEFAULT_CAMERA_MIN_ZOOM = 0.1f;
    public static final float DEFAULT_CAMERA_MAX_ZOOM = 6.0f;
    public static final float DEFAULT_CAMERA_ZOOM_SENSITIVITY = 0.1f;
    public static final boolean DEFAULT_CAMERA_SMOOTHING_ENABLED = true;
    public static final float DEFAULT_CAMERA_SMOOTH_FACTOR = 0.1f;

    // Ограничения значений
    public static final int MIN_RESOLUTION_WIDTH = 640;
    public static final int MAX_RESOLUTION_WIDTH = 3840;
    public static final int MIN_RESOLUTION_HEIGHT = 480;
    public static final int MAX_RESOLUTION_HEIGHT = 2160;
    public static final float MIN_UI_SCALE = 0.5f;
    public static final float MAX_UI_SCALE = 2.0f;
    public static final float MIN_VOLUME = 0.0f;
    public static final float MAX_VOLUME = 1.0f;
    public static final float MIN_SPEED_MULTIPLIER = 10.0f;
    public static final float MAX_SPEED_MULTIPLIER = 5000.0f;
    public static final float MIN_CAMERA_MOVE_SPEED = 50.0f;
    public static final float MAX_CAMERA_MOVE_SPEED = 1000.0f;
    public static final float MIN_CAMERA_ZOOM = 0.05f;
    public static final float MAX_CAMERA_ZOOM = 5.0f;
    public static final float MIN_CAMERA_SMOOTH_FACTOR = 0.1f;
    public static final float MAX_CAMERA_SMOOTH_FACTOR = 1.0f;
    public static final float MIN_ZOOM_SENSITIVITY = 0.01f;
    public static final float MAX_ZOOM_SENSITIVITY = 0.5f;
    public static final int MIN_AUTO_SAVE_INTERVAL = 1;
    public static final int MAX_AUTO_SAVE_INTERVAL = 60;

    // Типы вьюпортов
    public static final String VIEWPORT_TYPE_FIT = "fit";
    public static final String VIEWPORT_TYPE_SCREEN = "screen";
    public static final String VIEWPORT_TYPE_STRETCH = "stretch";

}
