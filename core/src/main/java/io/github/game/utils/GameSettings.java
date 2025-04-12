package io.github.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

@Singleton
public class GameSettings {

    private final Preferences prefs = Gdx.app.getPreferences("game-settings");

    @Setter
    @Getter
    private float speedMultiplier; // Значение по умолчанию


    public GameSettings() {
        this.speedMultiplier = prefs.getFloat("speedMultiplier", 100.0f);
    }

    public void save() {
        prefs.putFloat("speedMultiplier", speedMultiplier);
        prefs.flush();
    }


}
