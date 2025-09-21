package io.github.game.settings;


/**
 * Настройки игрового процесса
 */
public interface GameplaySettings extends BaseSettings {

    float getSpeedMultiplier();

    void setSpeedMultiplier(float multiplier);


    String getDifficulty();

    void setDifficulty(String difficulty);

    boolean isAutoSaveEnabled();

    void setAutoSaveEnabled(boolean enabled);

    int getAutoSaveInterval();

    void setAutoSaveInterval(int minutes);

    int getHexSize();

    void setHexSize(int size);
}
