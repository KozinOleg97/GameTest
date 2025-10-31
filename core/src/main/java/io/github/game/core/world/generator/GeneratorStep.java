package io.github.game.core.world.generator;

/**
 * Интерфейс для отдельного шага в пайплайне генерации.
 * Каждый шаг модифицирует GenerationContext.
 */
public interface GeneratorStep {

    /**
     * Выполняет очередной шаг генерации
     *
     * @param context общий контекст, содержащий данные из предыдущих шагов
     */
    void execute(GenerationContext context);
}
