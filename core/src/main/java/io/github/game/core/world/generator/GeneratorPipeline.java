package io.github.game.core.world.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Пайплайн для последовательного выполнения шагов генерации. Обрабатывает зависимости путем
 * выполнения шагов по порядку и общего доступа к контексту.
 */
public class GeneratorPipeline {

    private final List<GeneratorStep> steps = new ArrayList<>();

    /**
     * Добавляет шаг в пайплайн
     *
     * @param step шаг для добавления
     */
    public void addStep(GeneratorStep step) {
        steps.add(step);
    }

    /**
     * Выполняет все шаги по порядку
     *
     * @param context начальный контекст (может быть пустым или предварительно загруженным для
     *                сохранений)
     * @return финальный контекст после выполнения всех шагов
     */
    public GenerationContext execute(GenerationContext context) {
        for (GeneratorStep step : steps) {
            step.execute(context);
        }
        return context;
    }

    /**
     * Очищает все шаги пайплайна
     */
    public void clear() {
        steps.clear();
    }
}
