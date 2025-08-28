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

    // Поля для интерполяции
    private final Vector3 targetPosition = new Vector3();
    private final Vector3 smoothVelocity = new Vector3();
    private final boolean useSmoothing = true;
    private float targetZoom = 1.0f;
    private float smoothZoomVelocity;

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

        this.targetPosition.set(camera.position);
        this.targetZoom = camera.zoom;
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

        // Применяем интерполяцию, если она включена
        if (useSmoothing) {
            applyCameraSmoothing(deltaTime);
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

        touchStart.set(screenX, screenY, 0);
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
            float deltaX = (screenX - touchStart.x) * targetZoom;
            float deltaY = (touchStart.y - screenY) * targetZoom;

            targetPosition.set(cameraStart.x - deltaX, cameraStart.y - deltaY, 0);

            // Если сглаживание отключено, применяем изменения сразу
            if (!useSmoothing) {
                camera.position.set(targetPosition);
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

        float newZoom = targetZoom - amountY * cameraSettings.getCameraZoomSensitivity();
        targetZoom = Math.max(
            cameraSettings.getCameraMinZoom(),
            Math.min(cameraSettings.getCameraMaxZoom(), newZoom)
        );

        // Если сглаживание отключено, применяем zoom сразу
        if (!useSmoothing) {
            camera.zoom = targetZoom;
        }

        return true;
    }

    /**
     * Возвращает текущую камеру (может быть полезно для других систем)
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Применяет сглаживание движения камеры с помощью интерполяции
     */
    private void applyCameraSmoothing(float deltaTime) {
        // Используем линейную интерполяцию (LERP) для плавного перемещения
        float smoothTime = cameraSettings.getCameraSmoothFactor(); // Например, 0.1f

        camera.position.x += (targetPosition.x - camera.position.x) * smoothTime * deltaTime * 50;
        camera.position.y += (targetPosition.y - camera.position.y) * smoothTime * deltaTime * 50;
        camera.zoom += (targetZoom - camera.zoom) * smoothTime * deltaTime * 50;
    }

    /**
     * Обрабатывает клавиатурный ввод для перемещения камеры. Использует состояние кнопок из
     * InputService.
     *
     * @param deltaTime Время, прошедшее с последнего кадра
     */
    private void handleKeyboardInput(float deltaTime) {
        float moveAmount = cameraSettings.getCameraMoveSpeed() * deltaTime;

        if (inputService.isRightPressed()) {
            targetPosition.x += moveAmount / targetZoom;
        }
        if (inputService.isLeftPressed()) {
            targetPosition.x -= moveAmount / targetZoom;
        }
        if (inputService.isUpPressed()) {
            targetPosition.y += moveAmount / targetZoom;
        }
        if (inputService.isDownPressed()) {
            targetPosition.y -= moveAmount / targetZoom;
        }

        // Если сглаживание отключено, применяем изменения сразу
        if (!useSmoothing) {
            camera.position.set(targetPosition);
        }
    }

}
