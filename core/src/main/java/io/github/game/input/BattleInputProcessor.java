package io.github.game.input;

import com.badlogic.gdx.InputAdapter;
import javax.inject.Inject;

public class BattleInputProcessor extends InputAdapter {

    @Inject
    public BattleInputProcessor() {
        // TODO: Реализовать логику ввода для боя
    }

    // Заглушка - будет реализована позже
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Логика выбора цели или указания действий в бою
        return true;
    }
}
