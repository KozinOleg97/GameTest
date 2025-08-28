package io.github.game.settings.impl;

import com.badlogic.gdx.Preferences;
import io.github.game.settings.CameraSettings;
import io.github.game.utils.GameSettingsConstants;

public class CameraSettingsImpl extends BaseSettingsImpl implements CameraSettings {

    // Текущие значения настроек
    private float cameraMoveSpeed;
    private float cameraMinZoom;
    private float cameraMaxZoom;
    private float cameraZoomSensitivity;
    private float cameraSmoothFactor;
    private boolean isCameraSmoothingEnabled;

    public CameraSettingsImpl(Preferences prefs) {
        super(prefs, GameSettingsConstants.PREFIX_CAMERA);
        load();
    }

    @Override
    public void load() {
        cameraMoveSpeed = getFloat(GameSettingsConstants.KEY_CAMERA_MOVE_SPEED,
                                   GameSettingsConstants.DEFAULT_CAMERA_MOVE_SPEED);
        cameraMinZoom = getFloat(GameSettingsConstants.KEY_CAMERA_MIN_ZOOM,
                                 GameSettingsConstants.DEFAULT_CAMERA_MIN_ZOOM);
        cameraMaxZoom = getFloat(GameSettingsConstants.KEY_CAMERA_MAX_ZOOM,
                                 GameSettingsConstants.DEFAULT_CAMERA_MAX_ZOOM);
        cameraZoomSensitivity = getFloat(GameSettingsConstants.KEY_CAMERA_ZOOM_SENSITIVITY,
                                         GameSettingsConstants.DEFAULT_CAMERA_ZOOM_SENSITIVITY);
        cameraSmoothFactor = getFloat(GameSettingsConstants.KEY_CAMERA_SMOOTH_FACTOR,
                                      GameSettingsConstants.DEFAULT_CAMERA_SMOOTH_FACTOR);
        isCameraSmoothingEnabled = getBoolean(GameSettingsConstants.KEY_CAMERA_SMOOTHING_ENABLED,
                                              GameSettingsConstants.DEFAULT_CAMERA_SMOOTHING_ENABLED);
    }

    @Override
    public void resetToDefaults() {
        setCameraMoveSpeed(GameSettingsConstants.DEFAULT_CAMERA_MOVE_SPEED);
        setCameraMinZoom(GameSettingsConstants.DEFAULT_CAMERA_MIN_ZOOM);
        setCameraMaxZoom(GameSettingsConstants.DEFAULT_CAMERA_MAX_ZOOM);
        setCameraZoomSensitivity(GameSettingsConstants.DEFAULT_CAMERA_ZOOM_SENSITIVITY);
        save();
    }

    @Override
    public float getCameraMoveSpeed() {
        return cameraMoveSpeed;
    }

    @Override
    public void setCameraMoveSpeed(float speed) {
        this.cameraMoveSpeed = validateFloat(speed, GameSettingsConstants.MIN_CAMERA_MOVE_SPEED,
                                             GameSettingsConstants.MAX_CAMERA_MOVE_SPEED,
                                             "Camera move speed");
        putFloat(GameSettingsConstants.KEY_CAMERA_MOVE_SPEED, this.cameraMoveSpeed);
        save();
    }

    @Override
    public float getCameraMinZoom() {
        return cameraMinZoom;
    }

    @Override
    public void setCameraMinZoom(float zoom) {
        this.cameraMinZoom = validateZoom(zoom);
        putFloat(GameSettingsConstants.KEY_CAMERA_MIN_ZOOM, this.cameraMinZoom);

        // Убедимся, что минимальный зум не больше максимального
        if (this.cameraMinZoom > this.cameraMaxZoom) {
            setCameraMaxZoom(this.cameraMinZoom);
        }

        save();
    }

    @Override
    public float getCameraMaxZoom() {
        return cameraMaxZoom;
    }

    @Override
    public void setCameraMaxZoom(float zoom) {
        this.cameraMaxZoom = validateZoom(zoom);
        putFloat(GameSettingsConstants.KEY_CAMERA_MAX_ZOOM, this.cameraMaxZoom);

        // Убедимся, что максимальный зум не меньше минимального
        if (this.cameraMaxZoom < this.cameraMinZoom) {
            setCameraMinZoom(this.cameraMaxZoom);
        }

        save();
    }

    @Override
    public float getCameraZoomSensitivity() {
        return cameraZoomSensitivity;
    }

    @Override
    public void setCameraZoomSensitivity(float sensitivity) {
        this.cameraZoomSensitivity = validateFloat(sensitivity,
                                                   GameSettingsConstants.MIN_ZOOM_SENSITIVITY,
                                                   GameSettingsConstants.MAX_ZOOM_SENSITIVITY,
                                                   "Zoom sensitivity");
        putFloat(GameSettingsConstants.KEY_CAMERA_ZOOM_SENSITIVITY, this.cameraZoomSensitivity);
        save();
    }

    @Override
    public float getCameraSmoothFactor() {
        return cameraSmoothFactor;
    }

    @Override
    public void setCameraSmoothFactor(float smoothTime) {
        this.cameraSmoothFactor = validateFloat(smoothTime,
                                                GameSettingsConstants.MIN_CAMERA_SMOOTH_FACTOR,
                                                GameSettingsConstants.MAX_CAMERA_SMOOTH_FACTOR,
                                                "Camera smooth factor");
        putFloat(GameSettingsConstants.KEY_CAMERA_SMOOTH_FACTOR, this.cameraSmoothFactor);
        save();

    }

    @Override
    public boolean isCameraSmoothingEnabled() {
        return isCameraSmoothingEnabled;
    }

    @Override
    public void setCameraSmoothingEnabled(boolean enabled) {
        this.isCameraSmoothingEnabled = enabled;
        putBoolean(GameSettingsConstants.KEY_CAMERA_SMOOTHING_ENABLED,
                   this.isCameraSmoothingEnabled);
        save();

    }
}
