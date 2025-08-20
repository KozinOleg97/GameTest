package io.github.game.ui.screens;

import com.badlogic.gdx.Screen;

public interface ScreenFactory {

    LoadingScreen createLoadingScreen();

    GameScreen createGameScreen();

    Screen createBattleScreen(); // Пример для будущего расширения
}
