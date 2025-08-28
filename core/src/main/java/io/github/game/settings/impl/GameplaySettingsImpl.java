package io.github.game.settings.impl;

import com.badlogic.gdx.Preferences;
import io.github.game.settings.GameplaySettings;
import io.github.game.utils.GameSettingsConstants;

public class GameplaySettingsImpl extends BaseSettingsImpl implements GameplaySettings {

    // Текущие значения настроек
    private float speedMultiplier;
    private String difficulty;
    private boolean autoSaveEnabled;
    private int autoSaveInterval;

    public GameplaySettingsImpl(Preferences prefs) {
        super(prefs, GameSettingsConstants.PREFIX_GAMEPLAY);
        load();
    }

    @Override
    public void load() {
        speedMultiplier = getFloat(GameSettingsConstants.KEY_SPEED_MULTIPLIER,
                                   GameSettingsConstants.DEFAULT_SPEED_MULTIPLIER);
        difficulty = getString(GameSettingsConstants.KEY_DIFFICULTY,
                               GameSettingsConstants.DEFAULT_DIFFICULTY);
        autoSaveEnabled = getBoolean(GameSettingsConstants.KEY_AUTO_SAVE,
                                     GameSettingsConstants.DEFAULT_AUTO_SAVE);
        autoSaveInterval = getInt(GameSettingsConstants.KEY_AUTO_SAVE_INTERVAL,
                                  GameSettingsConstants.DEFAULT_AUTO_SAVE_INTERVAL);
    }

    @Override
    public void resetToDefaults() {
        setSpeedMultiplier(GameSettingsConstants.DEFAULT_SPEED_MULTIPLIER);
        setDifficulty(GameSettingsConstants.DEFAULT_DIFFICULTY);
        setAutoSaveEnabled(GameSettingsConstants.DEFAULT_AUTO_SAVE);
        setAutoSaveInterval(GameSettingsConstants.DEFAULT_AUTO_SAVE_INTERVAL);
        save();
    }

    @Override
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    @Override
    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = validateFloat(multiplier,
                                             GameSettingsConstants.MIN_SPEED_MULTIPLIER,
                                             GameSettingsConstants.MAX_SPEED_MULTIPLIER,
                                             "Speed multiplier");
        putFloat(GameSettingsConstants.KEY_SPEED_MULTIPLIER, this.speedMultiplier);
        save();
    }

    @Override
    public String getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(String difficulty) {
        this.difficulty = validateDifficulty(difficulty);
        putString(GameSettingsConstants.KEY_DIFFICULTY, this.difficulty);
        save();
    }

    @Override
    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }

    @Override
    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
        putBoolean(GameSettingsConstants.KEY_AUTO_SAVE, this.autoSaveEnabled);
        save();
    }

    @Override
    public int getAutoSaveInterval() {
        return autoSaveInterval;
    }

    @Override
    public void setAutoSaveInterval(int minutes) {
        this.autoSaveInterval = validateInt(minutes,
                                            GameSettingsConstants.MIN_AUTO_SAVE_INTERVAL,
                                            GameSettingsConstants.MAX_AUTO_SAVE_INTERVAL,
                                            "Auto-save interval");
        putInt(GameSettingsConstants.KEY_AUTO_SAVE_INTERVAL, this.autoSaveInterval);
        save();
    }
}
