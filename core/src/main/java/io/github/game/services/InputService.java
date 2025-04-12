package io.github.game.services;

public class InputService {

    private boolean isRightPressed = false;
    private boolean isLeftPressed = false;
    private boolean isUpPressed = false;
    private boolean isDownPressed = false;

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
        return (float) (isRightPressed ? 1 : 0) + (isLeftPressed ? -1 : 0);
    }

    public float getVerticalAxis() {
        return (float) (isUpPressed ? 1 : 0) + (isDownPressed ? -1 : 0);
    }
}
