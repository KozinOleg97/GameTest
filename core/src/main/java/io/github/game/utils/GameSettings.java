package io.github.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;

@Singleton
public class GameSettings {

    private static final String PREFS_NAME = "game-settings";
    private static final String KEY_SPEED_MULTIPLIER = "speedMultiplier";
    private static final float DEFAULT_SPEED_MULTIPLIER = 100.0f;

    private final Preferences prefs;

    @Getter
    private float speedMultiplier;

    @Inject
    public GameSettings() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
        this.speedMultiplier = prefs.getFloat(KEY_SPEED_MULTIPLIER, DEFAULT_SPEED_MULTIPLIER);
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        prefs.putFloat(KEY_SPEED_MULTIPLIER, speedMultiplier);
        prefs.flush();
    }
}
