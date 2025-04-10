package io.github.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class GameInputAdapter extends InputAdapter {
    private final InputService inputService;


    public GameInputAdapter(InputService inputService) {
        this.inputService = inputService;
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("INPUT","Key pressed: " + keycode);
        switch (keycode) {
            case Keys.RIGHT -> inputService.setRightPressed(true);
            case Keys.LEFT -> inputService.setLeftPressed(true);
            case Keys.UP -> inputService.setUpPressed(true);
            case Keys.DOWN -> inputService.setDownPressed(true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log("INPUT", "Key released: " + keycode);
        switch (keycode) {
            case Keys.RIGHT -> inputService.setRightPressed(false);
            case Keys.LEFT -> inputService.setLeftPressed(false);
            case Keys.UP -> inputService.setUpPressed(false);
            case Keys.DOWN -> inputService.setDownPressed(false);
        }
        return true;
    }
}
