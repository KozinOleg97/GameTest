package io.github.game.services;

import io.github.game.input.InputMode;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

@Singleton
@Setter
@Getter
public class InputService {

    private boolean isRightPressed = false;
    private boolean isLeftPressed = false;
    private boolean isUpPressed = false;
    private boolean isDownPressed = false;
    private InputMode currentMode = InputMode.WORLD_MAP;

    @Inject
    public InputService() {
        // Конструктор для Dagger
    }

    public void setInputMode(InputMode mode) {
        this.currentMode = mode;
        // Сброс состояний при смене режима
        resetInputStates();
    }

    public InputMode getCurrentMode() {
        return currentMode;
    }

    private void resetInputStates() {
        isRightPressed = false;
        isLeftPressed = false;
        isUpPressed = false;
        isDownPressed = false;
    }

    // Методы для обновления состояния
    public void setRightPressed(boolean pressed) {
        isRightPressed = pressed;
    }

    public void setLeftPressed(boolean pressed) {
        isLeftPressed = pressed;
    }

    public void setUpPressed(boolean pressed) {
        isUpPressed = pressed;
    }

    public void setDownPressed(boolean pressed) {
        isDownPressed = pressed;
    }

    // Получение значений осей
    public float getHorizontalAxis() {
        return (isRightPressed ? 1 : 0) + (isLeftPressed ? -1 : 0);
    }

    public float getVerticalAxis() {
        return (isUpPressed ? 1 : 0) + (isDownPressed ? -1 : 0);
    }
}
