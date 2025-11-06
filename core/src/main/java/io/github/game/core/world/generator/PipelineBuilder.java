package io.github.game.core.world.generator;

import io.github.game.core.world.generator.location.LocationGenerationStep;
import io.github.game.core.world.generator.location.LocationGenerator;
import io.github.game.core.world.generator.location.LocationGeneratorConfig;
import io.github.game.core.world.generator.world.WorldGenerationStep;
import io.github.game.core.world.generator.world.WorldGenerator;
import io.github.game.core.world.generator.world.WorldGeneratorConfig;

/**
 * Builder для создания пайплайнов генерации.
 */
public class PipelineBuilder {

    private final GeneratorPipeline pipeline;
    private final GeneratorFactory generatorFactory;

    public PipelineBuilder(GeneratorFactory generatorFactory) {
        this.generatorFactory = generatorFactory;
        this.pipeline = new GeneratorPipeline();
    }

    /**
     * Добавляет шаг генерации мира
     *
     * @param type   тип генератора мира
     * @param config конфигурация генератора мира
     * @return текущий builder
     */
    public PipelineBuilder withWorld(GeneratorType type, WorldGeneratorConfig config) {
        WorldGenerator worldGenerator = generatorFactory.createWorldGenerator(type, config);
        pipeline.addStep(new WorldGenerationStep(worldGenerator));
        return this;
    }

    /**
     * Добавляет шаг генерации локаций
     *
     * @param type   тип генератора локаций
     * @param config конфигурация генератора локаций
     * @return текущий builder
     */
    public PipelineBuilder withLocations(GeneratorType type, LocationGeneratorConfig config) {
        LocationGenerator locationGenerator = generatorFactory.createLocationGenerator(type, config);
        pipeline.addStep(new LocationGenerationStep(locationGenerator));
        return this;
    }

    /**
     * Добавляет шаг сохранения/загрузки
     *
     * @param filePath путь к файлу
     * @param isLoad   true для загрузки, false для сохранения
     * @return текущий builder
     */
    public PipelineBuilder withSaveLoad(String filePath, boolean isLoad) {
        pipeline.addStep(new SaveLoadStep(filePath, isLoad));
        return this;
    }

    /**
     * Добавляет произвольный шаг генерации
     *
     * @param step шаг генерации для добавления
     * @return текущий builder
     */
    public PipelineBuilder withStep(GeneratorStep step) {
        pipeline.addStep(step);
        return this;
    }

    /**
     * Возвращает готовый пайплайн
     *
     * @return настроенный пайплайн генерации
     */
    public GeneratorPipeline build() {
        return pipeline;
    }

    /**
     * Очищает текущую конфигурацию пайплайна
     *
     * @return текущий builder
     */
    public PipelineBuilder clear() {
        pipeline.clear();
        return this;
    }
}
