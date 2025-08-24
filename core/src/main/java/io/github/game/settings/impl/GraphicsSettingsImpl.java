package io.github.game.settings.impl;

import com.badlogic.gdx.Preferences;
import io.github.game.settings.GraphicsSettings;
import io.github.game.utils.GameSettingsConstants;

public class GraphicsSettingsImpl extends BaseSettingsImpl implements GraphicsSettings {

    // Текущие значения настроек
    private int resolutionWidth;
    private int resolutionHeight;
    private boolean fullscreen;
    private boolean vsync;
    private String viewportType;
    private float uiScale;
    private boolean debugMode;

    public GraphicsSettingsImpl(Preferences prefs) {
        super(prefs, GameSettingsConstants.PREFIX_GRAPHICS);
        load();
    }

    @Override
    public void load() {
        resolutionWidth = getInt(GameSettingsConstants.KEY_RESOLUTION_WIDTH,
                                 GameSettingsConstants.DEFAULT_RESOLUTION_WIDTH);
        resolutionHeight = getInt(GameSettingsConstants.KEY_RESOLUTION_HEIGHT,
                                  GameSettingsConstants.DEFAULT_RESOLUTION_HEIGHT);
        fullscreen = getBoolean(GameSettingsConstants.KEY_FULLSCREEN,
                                GameSettingsConstants.DEFAULT_FULLSCREEN);
        vsync = getBoolean(GameSettingsConstants.KEY_VSYNC,
                           GameSettingsConstants.DEFAULT_VSYNC);
        viewportType = getString(GameSettingsConstants.KEY_VIEWPORT_TYPE,
                                 GameSettingsConstants.DEFAULT_VIEWPORT_TYPE);
        uiScale = getFloat(GameSettingsConstants.KEY_UI_SCALE,
                           GameSettingsConstants.DEFAULT_UI_SCALE);
        debugMode = getBoolean(GameSettingsConstants.KEY_DEBUG_MODE,
                               GameSettingsConstants.DEFAULT_DEBUG_MODE);
    }

    @Override
    public void resetToDefaults() {
        setResolutionWidth(GameSettingsConstants.DEFAULT_RESOLUTION_WIDTH);
        setResolutionHeight(GameSettingsConstants.DEFAULT_RESOLUTION_HEIGHT);
        setFullscreen(GameSettingsConstants.DEFAULT_FULLSCREEN);
        setVsync(GameSettingsConstants.DEFAULT_VSYNC);
        setViewportType(GameSettingsConstants.DEFAULT_VIEWPORT_TYPE);
        setUiScale(GameSettingsConstants.DEFAULT_UI_SCALE);
        setDebugMode(GameSettingsConstants.DEFAULT_DEBUG_MODE);
        save();
    }

    @Override
    public int getResolutionWidth() {
        return resolutionWidth;
    }

    @Override
    public void setResolutionWidth(int width) {
        this.resolutionWidth = validateInt(width,
                                           GameSettingsConstants.MIN_RESOLUTION_WIDTH,
                                           GameSettingsConstants.MAX_RESOLUTION_WIDTH,
                                           "Resolution width");
        putInt(GameSettingsConstants.KEY_RESOLUTION_WIDTH, this.resolutionWidth);
        save();
    }

    @Override
    public int getResolutionHeight() {
        return resolutionHeight;
    }

    @Override
    public void setResolutionHeight(int height) {
        this.resolutionHeight = validateInt(height,
                                            GameSettingsConstants.MIN_RESOLUTION_HEIGHT,
                                            GameSettingsConstants.MAX_RESOLUTION_HEIGHT,
                                            "Resolution height");
        putInt(GameSettingsConstants.KEY_RESOLUTION_HEIGHT, this.resolutionHeight);
        save();
    }

    @Override
    public boolean isFullscreen() {
        return fullscreen;
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        putBoolean(GameSettingsConstants.KEY_FULLSCREEN, this.fullscreen);
        save();
    }

    @Override
    public boolean isVsync() {
        return vsync;
    }

    @Override
    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        putBoolean(GameSettingsConstants.KEY_VSYNC, this.vsync);
        save();
    }

    @Override
    public String getViewportType() {
        return viewportType;
    }

    @Override
    public void setViewportType(String viewportType) {
        this.viewportType = validateViewportType(viewportType);
        putString(GameSettingsConstants.KEY_VIEWPORT_TYPE, this.viewportType);
        save();
    }

    @Override
    public float getUiScale() {
        return uiScale;
    }

    @Override
    public void setUiScale(float scale) {
        this.uiScale = validateUIScale(scale);
        putFloat(GameSettingsConstants.KEY_UI_SCALE, this.uiScale);
        save();
    }

    @Override
    public boolean isDebugMode() {
        return debugMode;
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        putBoolean(GameSettingsConstants.KEY_DEBUG_MODE, this.debugMode);
        save();
    }
}
