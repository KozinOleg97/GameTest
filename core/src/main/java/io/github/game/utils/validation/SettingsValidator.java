package io.github.game.utils.validation;

import com.badlogic.gdx.Gdx;
import io.github.game.utils.GameSettingsConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SettingsValidator {


    public static int validateIntRange(int value, int min, int max, String settingName) {
        if (value < min) {
            logClamping(settingName, value, min, max);
            return min;
        }
        if (value > max) {
            logClamping(settingName, value, min, max);
            return max;
        }
        return value;
    }

    public static float validateFloatRange(float value, float min, float max, String settingName) {
        if (value < min) {
            logClamping(settingName, value, min, max);
            return min;
        }
        if (value > max) {
            logClamping(settingName, value, min, max);
            return max;
        }
        return value;
    }

    public static String validateViewportType(String value) {
        if (!value.equals(GameSettingsConstants.VIEWPORT_TYPE_FIT) &&
            !value.equals(GameSettingsConstants.VIEWPORT_TYPE_SCREEN) &&
            !value.equals(GameSettingsConstants.VIEWPORT_TYPE_STRETCH)) {
            Gdx.app.debug("SettingsValidator",
                          "Invalid viewport type: " + value + ", using default.");
            return GameSettingsConstants.DEFAULT_VIEWPORT_TYPE;
        }
        return value;
    }

    public static String validateDifficulty(String value) {
        if (!value.equals("EASY") &&
            !value.equals("NORMAL") &&
            !value.equals("HARD")) {
            Gdx.app.debug("SettingsValidator",
                          "Invalid difficulty: " + value + ", using default.");
            return GameSettingsConstants.DEFAULT_DIFFICULTY;
        }
        return value;
    }

    // Специализированные методы для часто используемых проверок
    public static float validateVolume(float volume) {
        return validateFloatRange(volume,
                                  GameSettingsConstants.MIN_VOLUME,
                                  GameSettingsConstants.MAX_VOLUME,
                                  "Volume");
    }

    public static float validateZoom(float zoom) {
        return validateFloatRange(zoom,
                                  GameSettingsConstants.MIN_CAMERA_ZOOM,
                                  GameSettingsConstants.MAX_CAMERA_ZOOM,
                                  "Zoom");
    }

    public static float validateUIScale(float scale) {
        return validateFloatRange(scale,
                                  GameSettingsConstants.MIN_UI_SCALE,
                                  GameSettingsConstants.MAX_UI_SCALE,
                                  "UI scale");
    }

    private static void logClamping(String settingName, Object value, Object min, Object max) {
        Gdx.app.debug("SettingsValidator",
                      settingName + " value " + value + " is out of range [" + min + ", " + max +
                      "], clamping.");
    }
}
