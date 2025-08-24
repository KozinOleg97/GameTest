package io.github.game.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.game.input.InputMode;
import io.github.game.services.InputService;
import io.github.game.settings.CameraSettings;
import io.github.game.settings.GraphicsSettings;
import javax.inject.Inject;

/**
 * Система управления камерой на карте мира. Обрабатывает перемещение камеры с клавиатуры,
 * перетаскивание мышью и зум колесиком. Работает только в режиме WORLD_MAP.
 */
public class CameraControlSystem extends EntitySystem {

    private final OrthographicCamera camera;
    private final InputService inputService;
    private final GraphicsSettings graphicsSettings;
    private final CameraSettings cameraSettings;

    // Векторы для хранения начальных позиций касания и камеры
    private final Vector3 touchStart = new Vector3();
    private final Vector3 cameraStart = new Vector3();
    private final Vector3 currentWorldCoords = new Vector3();

    // Флаг перетаскивания
    private boolean isDragging = false;

    /**
     * Конструктор системы управления камерой.
     *
     * @param camera       Камера, которой управляет система
     * @param inputService Сервис ввода для проверки состояния клавиш и режима
     */
    @Inject
    public CameraControlSystem(OrthographicCamera camera,
                               InputService inputService,
                               GraphicsSettings graphicsSettings,
                               CameraSettings cameraSettings) {
        this.camera = camera;
        this.inputService = inputService;
        this.graphicsSettings = graphicsSettings;
        this.cameraSettings = cameraSettings;
    }

    /**
     * Основной метод обновления системы. Вызывается каждый кадр для обработки клавиатурного ввода и
     * обновления камеры.
     *
     * @param deltaTime Время, прошедшее с последнего кадра
     */
    @Override
    public void update(float deltaTime) {
        // Обрабатываем ввод только в режиме карты мира
        if (inputService.getCurrentMode() == InputMode.WORLD_MAP) {
            handleKeyboardInput(deltaTime);
        }

        // Всегда обновляем камеру (даже если не в режиме WORLD_MAP)
        camera.update();
    }

    /**
     * Обрабатывает начало касания/нажатия мыши.
     *
     * @param screenX Координата X касания
     * @param screenY Координата Y касания
     * @param pointer Указатель (палец)
     * @param button  Кнопка мыши
     * @return true, если событие обработано
     */
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Обрабатываем только в режиме карты мира и левую кнопку мыши
        if (inputService.getCurrentMode() != InputMode.WORLD_MAP || button != Input.Buttons.LEFT) {
            return false;
        }

        isDragging = true;

        // Конвертируем экранные координаты в мировые
        currentWorldCoords.set(screenX, screenY, 0);
        camera.unproject(currentWorldCoords);

        // Сохраняем начальные позиции
        touchStart.set(currentWorldCoords.x, currentWorldCoords.y, 0);
        cameraStart.set(camera.position);

        if (graphicsSettings.isDebugMode()) {
            Gdx.app.log("CameraControlSystem", "Touch started at: " + touchStart);
        }
        return true;
    }

    /**
     * Обрабатывает окончание касания/отпускание мыши.
     *
     * @param screenX Координата X касания
     * @param screenY Координата Y касания
     * @param pointer Указатель (палец)
     * @param button  Кнопка мыши
     * @return true, если событие обработано
     */
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDragging = false;
            return true;
        }
        return false;
    }

    /**
     * Обрабатывает перемещение мыши/пальца при нажатой кнопке.
     *
     * @param screenX Координата X касания
     * @param screenY Координата Y касания
     * @param pointer Указатель (палец)
     * @return true, если событие обработано
     */
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isDragging && inputService.getCurrentMode() == InputMode.WORLD_MAP) {
            // Конвертируем экранные координаты в мировые
            currentWorldCoords.set(screenX, screenY, 0);
            camera.unproject(currentWorldCoords);

            // Вычисляем смещение в мировых координатах
            float deltaX = currentWorldCoords.x - touchStart.x;
            float deltaY = currentWorldCoords.y - touchStart.y;

            // Перемещаем камеру
            camera.position.set(cameraStart.x - deltaX, cameraStart.y - deltaY, 0);

            if (graphicsSettings.isDebugMode()) {
                Gdx.app.log("CameraControlSystem",
                            "Camera position: " + camera.position.x + ", " + camera.position.y);
            }
            return true;
        }
        return false;
    }

    /**
     * Обрабатывает прокрутку колесика мыши.
     *
     * @param amountX Горизонтальная составляющая прокрутки
     * @param amountY Вертикальная составляющая прокрутки (основная)
     * @return true, если событие обработано
     */
    public boolean scrolled(float amountX, float amountY) {
        // Обрабатываем только в режиме карты мира
        if (inputService.getCurrentMode() != InputMode.WORLD_MAP) {
            return false;
        }

        // Используем настройки из GameSettings
        float newZoom = camera.zoom - amountY * cameraSettings.getCameraZoomSensitivity();
        camera.zoom = Math.max(
            cameraSettings.getCameraMinZoom(),
            Math.min(cameraSettings.getCameraMaxZoom(), newZoom)
        );
        return true;
    }

    /**
     * Возвращает текущую камеру (может быть полезно для других систем)
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Обрабатывает клавиатурный ввод для перемещения камеры. Использует состояние кнопок из
     * InputService.
     *
     * @param deltaTime Время, прошедшее с последнего кадра
     */
    private void handleKeyboardInput(float deltaTime) {
        // Используем настройки из GameSettings
        float moveAmount = cameraSettings.getCameraMoveSpeed() * deltaTime;

        // Движение вправо
        if (inputService.isRightPressed()) {
            camera.position.x += moveAmount / camera.zoom;
        }

        // Движение влево
        if (inputService.isLeftPressed()) {
            camera.position.x -= moveAmount / camera.zoom;
        }

        // Движение вверх
        if (inputService.isUpPressed()) {
            camera.position.y += moveAmount / camera.zoom;
        }

        // Движение вниз
        if (inputService.isDownPressed()) {
            camera.position.y -= moveAmount / camera.zoom;
        }
    }
}
