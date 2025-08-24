package io.github.game.settings;

/**
 * Настройки аудио
 */
public interface AudioSettings extends BaseSettings {

    float getMasterVolume();

    void setMasterVolume(float volume);

    float getMusicVolume();

    void setMusicVolume(float volume);

    float getSfxVolume();

    void setSfxVolume(float volume);
}
