package io.github.game.settings;

/**
 * Настройки камеры
 */
public interface CameraSettings extends BaseSettings {

    float getCameraMoveSpeed();

    void setCameraMoveSpeed(float speed);

    float getCameraMinZoom();

    void setCameraMinZoom(float zoom);

    float getCameraMaxZoom();

    void setCameraMaxZoom(float zoom);

    float getCameraZoomSensitivity();

    void setCameraZoomSensitivity(float sensitivity);
}
