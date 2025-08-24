package io.github.game.settings.impl;

import com.badlogic.gdx.Preferences;
import io.github.game.settings.BaseSettings;
import io.github.game.utils.validation.SettingsValidator;

/**
 * Базовая реализация настроек. Содержит общую логику работы с Preferences.
 */
public abstract class BaseSettingsImpl implements BaseSettings {

    protected final Preferences prefs;
    protected final String prefix;

    protected BaseSettingsImpl(Preferences prefs, String prefix) {
        this.prefs = prefs;
        this.prefix = prefix;
    }

    @Override
    public void save() {
        prefs.flush();
    }

    protected int getInt(String key, int defaultValue) {
        return prefs.getInteger(prefix + key, defaultValue);
    }

    protected void putInt(String key, int value) {
        prefs.putInteger(prefix + key, value);
    }

    protected float getFloat(String key, float defaultValue) {
        return prefs.getFloat(prefix + key, defaultValue);
    }

    protected void putFloat(String key, float value) {
        prefs.putFloat(prefix + key, value);
    }

    protected boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(prefix + key, defaultValue);
    }

    protected void putBoolean(String key, boolean value) {
        prefs.putBoolean(prefix + key, value);
    }

    protected String getString(String key, String defaultValue) {
        return prefs.getString(prefix + key, defaultValue);
    }

    protected void putString(String key, String value) {
        prefs.putString(prefix + key, value);
    }

    // Методы валидации для использования в подклассах
    protected int validateInt(int value, int min, int max, String settingName) {
        return SettingsValidator.validateIntRange(value, min, max, settingName);
    }

    protected float validateFloat(float value, float min, float max, String settingName) {
        return SettingsValidator.validateFloatRange(value, min, max, settingName);
    }

    protected String validateViewportType(String value) {
        return SettingsValidator.validateViewportType(value);
    }

    protected String validateDifficulty(String value) {
        return SettingsValidator.validateDifficulty(value);
    }

    protected float validateVolume(float volume) {
        return SettingsValidator.validateVolume(volume);
    }

    protected float validateZoom(float zoom) {
        return SettingsValidator.validateZoom(zoom);
    }

    protected float validateUIScale(float scale) {
        return SettingsValidator.validateUIScale(scale);
    }
}
