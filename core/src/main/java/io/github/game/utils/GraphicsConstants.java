package io.github.game.utils;

public final class GraphicsConstants {

    // Начальные размеры окна
    public static final int INITIAL_WINDOW_WIDTH = 800;
    public static final int INITIAL_WINDOW_HEIGHT = 480;
    // Начальная позиция камеры (центр экрана)
    public static final float INITIAL_CAMERA_X = 400f;
    public static final float INITIAL_CAMERA_Y = 240f;
    public static final float INITIAL_CAMERA_Z = 0f;
    // Типы вьюпортов (можно расширять)
    public static final String VIEWPORT_TYPE_FIT = "fit";
    public static final String VIEWPORT_TYPE_SCREEN = "screen";
    public static final String VIEWPORT_TYPE_STRETCH = "stretch";
    // Настройки рендерера по умолчанию
    public static final int SHAPE_RENDERER_MAX_VERTICES = 5000;

    private GraphicsConstants() {
        // Запрещаем создание экземпляров класса
    }
}
