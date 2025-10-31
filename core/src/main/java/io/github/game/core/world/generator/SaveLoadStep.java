package io.github.game.core.world.generator;

import com.badlogic.gdx.Gdx;

/**
 * Пайплайн для последовательного выполнения шагов генерации. Обрабатывает зависимости путем
 * выполнения шагов по порядку и общего доступа к контексту.
 */
public class SaveLoadStep implements GeneratorStep {

    private final String filePath;
    private final boolean isLoad; // true for a load, false for save

    public SaveLoadStep(String filePath, boolean isLoad) {
        this.filePath = filePath;
        this.isLoad = isLoad;
    }

    @Override
    public void execute(GenerationContext context) {
        if (isLoad) {
            // Load context from the save file
            Gdx.app.log("SaveLoadStep", "Loading from: " + filePath);
            // TODO: реализовать десериализацию context = Json.fromJson(GenerationContext.class, file);
        } else {
            // Save context to file
            Gdx.app.log("SaveLoadStep", "Saving to: " + filePath);
            // TODO: реализовать сериализацию  json.toJson(context, file);
        }
    }
}
