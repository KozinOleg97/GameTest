package io.github.game.core.world.generator;

import io.github.game.core.world.generator.location.LocationGenerationStep;
import io.github.game.core.world.generator.location.LocationGenerator;
import io.github.game.core.world.generator.location.LocationGeneratorConfig;
import io.github.game.core.world.generator.world.WorldGenerationStep;
import io.github.game.core.world.generator.world.WorldGenerator;
import io.github.game.core.world.generator.world.WorldGeneratorConfig;

/**
 * Менеджер для настройки и выполнения пайплайнов генерации. Предоставляет методы для стандартных
 * сценариев.
 */
public class GenerationManager {

    private final GeneratorFactory factory;

    public GenerationManager(GeneratorFactory factory) {
        this.factory = factory;
    }

    /**
     * Создает стандартный пайплайн генерации: мир -> локации
     *
     * @param worldType      тип генератора мира
     * @param worldConfig    конфигурация генератора мира
     * @param locationType   тип генератора локаций
     * @param locationConfig конфигурация генератора локаций
     * @return настроенный пайплайн генерации
     */
    public GeneratorPipeline createStandardPipeline(GeneratorType worldType,
                                                    WorldGeneratorConfig worldConfig,
                                                    GeneratorType locationType,
                                                    LocationGeneratorConfig locationConfig) {
        GeneratorPipeline pipeline = new GeneratorPipeline();

        WorldGenerator worldGen = GeneratorFactory.createWorldGenerator(worldType, worldConfig);
        pipeline.addStep(new WorldGenerationStep(worldGen));

        LocationGenerator locGen = GeneratorFactory.createLocationGenerator(locationType, locationConfig);
        pipeline.addStep(new LocationGenerationStep(locGen));

        return pipeline;
    }

    /**
     * Создает пайплайн загрузки: загрузка из сохранения
     *
     * @param saveFilePath путь к файлу сохранения
     * @return пайплайн для загрузки
     */
    public GeneratorPipeline createLoadPipeline(String saveFilePath) {
        GeneratorPipeline pipeline = new GeneratorPipeline();
        pipeline.addStep(new SaveLoadStep(saveFilePath, true));
        return pipeline;
    }

    /**
     * Создает пайплайн сохранения: генерация -> сохранение
     *
     * @param generationPipeline пайплайн генерации
     * @param saveFilePath       путь для сохранения
     * @return модифицированный пайплайн с шагом сохранения
     */
    public GeneratorPipeline createSavePipeline(GeneratorPipeline generationPipeline,
                                                String saveFilePath) {
        generationPipeline.addStep(new SaveLoadStep(saveFilePath, false));
        return generationPipeline;
    }
}
