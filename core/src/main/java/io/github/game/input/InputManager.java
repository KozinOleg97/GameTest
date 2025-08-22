package io.github.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import io.github.game.services.InputService;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;

@Singleton
public class InputManager {

    private final InputService inputService;
    @Getter
    private final InputMultiplexer inputMultiplexer;
    private final Map<InputMode, InputProcessor> processors;
    private InputMode currentMode;

    @Inject
    public InputManager(InputService inputService,
                        WorldMapInputProcessor worldMapProcessor,
                        BattleInputProcessor battleProcessor) {
        this.inputService = inputService;
        this.inputMultiplexer = new InputMultiplexer();
        this.processors = new HashMap<>();

        // Регистрируем процессоры для каждого режима
        processors.put(InputMode.WORLD_MAP, worldMapProcessor);
        processors.put(InputMode.BATTLE, battleProcessor);

        // Устанавливаем начальный режим
        setInputMode(InputMode.WORLD_MAP);
    }

    public void setInputMode(InputMode mode) {
        this.currentMode = mode;
        inputService.setInputMode(mode);

        // Очищаем мультиплексор и добавляем нужный процессор
        inputMultiplexer.clear();

        InputProcessor processor = processors.get(mode);
        if (processor != null) {
            inputMultiplexer.addProcessor(processor);
            Gdx.app.log("InputManager", "Input mode set to: " + mode);
        } else {
            Gdx.app.error("InputManager", "No processor registered for mode: " + mode);
            // Можно добавить fallback-процессор или оставить мультиплексор пустым
        }

        // Можно добавить общие процессоры, которые работают всегда
    }

    public InputMode getCurrentMode() {
        return currentMode;
    }
}
