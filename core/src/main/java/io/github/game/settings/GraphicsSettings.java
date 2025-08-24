package io.github.game.settings;

/**
 * Настройки графики
 */
public interface GraphicsSettings extends BaseSettings {

    int getResolutionWidth();

    void setResolutionWidth(int width);

    int getResolutionHeight();

    void setResolutionHeight(int height);

    boolean isFullscreen();

    void setFullscreen(boolean fullscreen);

    boolean isVsync();

    void setVsync(boolean vsync);

    String getViewportType();

    void setViewportType(String viewportType);

    // методы для управления UI
    float getUiScale();

    void setUiScale(float scale);

    boolean isDebugMode();

    void setDebugMode(boolean debugMode);
}
