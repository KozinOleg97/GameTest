package io.github.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import io.github.game.ecs.systems.CameraControlSystem;
import io.github.game.services.InputService;
import javax.inject.Inject;

public class WorldMapInputProcessor extends InputAdapter {

    private final InputService inputService;
    private final CameraControlSystem cameraControlSystem;

    @Inject
    public WorldMapInputProcessor(InputService inputService,
                                  CameraControlSystem cameraControlSystem) {
        this.inputService = inputService;
        this.cameraControlSystem = cameraControlSystem;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!isWorldMapMode()) {
            return false;
        }

        return switch (keycode) {
            case Input.Keys.W, Input.Keys.UP -> {
                inputService.setUpPressed(true);
                yield true;
            }
            case Input.Keys.S, Input.Keys.DOWN -> {
                inputService.setDownPressed(true);
                yield true;
            }
            case Input.Keys.A, Input.Keys.LEFT -> {
                inputService.setLeftPressed(true);
                yield true;
            }
            case Input.Keys.D, Input.Keys.RIGHT -> {
                inputService.setRightPressed(true);
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!isWorldMapMode()) {
            return false;
        }

        return switch (keycode) {
            case Input.Keys.W, Input.Keys.UP -> {
                inputService.setUpPressed(false);
                yield true;
            }
            case Input.Keys.S, Input.Keys.DOWN -> {
                inputService.setDownPressed(false);
                yield true;
            }
            case Input.Keys.A, Input.Keys.LEFT -> {
                inputService.setLeftPressed(false);
                yield true;
            }
            case Input.Keys.D, Input.Keys.RIGHT -> {
                inputService.setRightPressed(false);
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isWorldMapMode()) {
            return false;
        }

        // Делегируем обработку касаний системе управления камерой
        return cameraControlSystem.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!isWorldMapMode()) {
            return false;
        }

        return cameraControlSystem.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isWorldMapMode()) {
            return false;
        }

        return cameraControlSystem.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (!isWorldMapMode()) {
            return false;
        }

        return cameraControlSystem.scrolled(amountX, amountY);
    }

    private boolean isWorldMapMode() {
        return inputService.getCurrentMode() == InputMode.WORLD_MAP;
    }
}
