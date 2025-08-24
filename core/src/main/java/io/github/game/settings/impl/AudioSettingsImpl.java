package io.github.game.settings.impl;

import com.badlogic.gdx.Preferences;
import io.github.game.settings.AudioSettings;
import io.github.game.utils.GameSettingsConstants;

public class AudioSettingsImpl extends BaseSettingsImpl implements AudioSettings {

    // Текущие значения настроек
    private float masterVolume;
    private float musicVolume;
    private float sfxVolume;

    public AudioSettingsImpl(Preferences prefs) {
        super(prefs, GameSettingsConstants.PREFIX_AUDIO);
        load();
    }

    @Override
    public void load() {
        masterVolume = getFloat(GameSettingsConstants.KEY_MASTER_VOLUME,
                                GameSettingsConstants.DEFAULT_MASTER_VOLUME);
        musicVolume = getFloat(GameSettingsConstants.KEY_MUSIC_VOLUME,
                               GameSettingsConstants.DEFAULT_MUSIC_VOLUME);
        sfxVolume = getFloat(GameSettingsConstants.KEY_SFX_VOLUME,
                             GameSettingsConstants.DEFAULT_SFX_VOLUME);
    }

    @Override
    public void resetToDefaults() {
        setMasterVolume(GameSettingsConstants.DEFAULT_MASTER_VOLUME);
        setMusicVolume(GameSettingsConstants.DEFAULT_MUSIC_VOLUME);
        setSfxVolume(GameSettingsConstants.DEFAULT_SFX_VOLUME);
        save();
    }

    @Override
    public float getMasterVolume() {
        return masterVolume;
    }

    @Override
    public void setMasterVolume(float volume) {
        this.masterVolume = validateVolume(volume);
        putFloat(GameSettingsConstants.KEY_MASTER_VOLUME, this.masterVolume);
        save();
    }

    @Override
    public float getMusicVolume() {
        return musicVolume;
    }

    @Override
    public void setMusicVolume(float volume) {
        this.musicVolume = validateVolume(volume);
        putFloat(GameSettingsConstants.KEY_MUSIC_VOLUME, this.musicVolume);
        save();
    }

    @Override
    public float getSfxVolume() {
        return sfxVolume;
    }

    @Override
    public void setSfxVolume(float volume) {
        this.sfxVolume = validateVolume(volume);
        putFloat(GameSettingsConstants.KEY_SFX_VOLUME, this.sfxVolume);
        save();
    }
}
