package io.github.game.core.world.generator;

import com.badlogic.gdx.Gdx;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexType;
import java.util.Random;

/**
 * Продвинутый генератор мира с использованием многоуровневого шума Перлина. Использует
 * комбинированный подход с континентальным и климатическим моделированием.
 */
public class ProceduralWorldGenerator implements WorldGenerator {

    private final int width;
    private final int height;
    private final long seed;
    private final Random random;
    private final PerlinNoise continentalNoise;
    private final PerlinNoise terrainNoise;
    private final PerlinNoise moistureNoise;
    private final PerlinNoise temperatureNoise;
    private final PerlinNoise mountainNoise;
    private final PerlinNoise aridityNoise;

    /**
     * Конструктор генератора мира
     *
     * @param width  ширина мира в гексах
     * @param height высота мира в гексах
     * @param seed   seed для генерации
     */
    public ProceduralWorldGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.random = new Random(seed);
        this.continentalNoise = new PerlinNoise(seed);
        this.terrainNoise = new PerlinNoise(seed + 1);
        this.moistureNoise = new PerlinNoise(seed + 2);
        this.temperatureNoise = new PerlinNoise(seed + 3);
        this.mountainNoise = new PerlinNoise(seed + 4);
        this.aridityNoise = new PerlinNoise(seed + 5);
    }

    @Override
    public HexMap generateWorld() {
        HexMap map = new HexMap();

        int[] typeCounts = new int[HexType.values().length];
        int totalCount = 0;

        // Статистика для отладки
        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;
        float minMoisture = Float.MAX_VALUE;
        float maxMoisture = Float.MIN_VALUE;
        float minTemperature = Float.MAX_VALUE;
        float maxTemperature = Float.MIN_VALUE;
        float minAridity = Float.MAX_VALUE;
        float maxAridity = Float.MIN_VALUE;

        // Генерируем базовые карты шума
        float[][] heightMap = new float[width][height];
        float[][] moistureMap = new float[width][height];
        float[][] temperatureMap = new float[width][height];
        float[][] aridityMap = new float[width][height];

        // Сначала генерируем все значения
        for (int q = 0; q < width; q++) {
            for (int r = 0; r < height; r++) {
                // Нормализованные координаты для шума
                float nx = (float) q / width;
                float ny = (float) r / height;

                // Генерируем базовые значения
                heightMap[q][r] = generateHeightValue(nx, ny);
                moistureMap[q][r] = generateMoistureValue(nx, ny);
                temperatureMap[q][r] = generateTemperatureValue(nx, ny);
                aridityMap[q][r] = generateAridityValue(nx, ny);
            }
        }

        // Нормализуем карты для получения полного диапазона [0, 1]
        normalizeMap(heightMap);
        normalizeMap(moistureMap);
        normalizeMap(temperatureMap);
        normalizeMap(aridityMap);

        // Теперь создаем гексы
        for (int q = 0; q < width; q++) {
            for (int r = 0; r < height; r++) {
                totalCount++;

                float heightValue = heightMap[q][r];
                float moistureValue = moistureMap[q][r];
                float temperatureValue = temperatureMap[q][r];
                float aridityValue = aridityMap[q][r];

                // Обновляем статистику
                minHeight = Math.min(minHeight, heightValue);
                maxHeight = Math.max(maxHeight, heightValue);
                minMoisture = Math.min(minMoisture, moistureValue);
                maxMoisture = Math.max(maxMoisture, moistureValue);
                minTemperature = Math.min(minTemperature, temperatureValue);
                maxTemperature = Math.max(maxTemperature, temperatureValue);
                minAridity = Math.min(minAridity, aridityValue);
                maxAridity = Math.max(maxAridity, aridityValue);

                // Определяем тип гекса
                HexType type = determineHexType(heightValue, moistureValue, temperatureValue,
                                                aridityValue);

                // Считаем статистику
                typeCounts[type.ordinal()]++;

                Hex hex = new Hex(q, r, type);
                hex.setGenerationSeed(
                    (long) (heightValue * 1000 + moistureValue * 100 + temperatureValue * 10 +
                            aridityValue * 1));
                map.addHex(hex);
            }
        }

        // Выводим статистику
        Gdx.app.log("Generation", "World generation complete (%dx%d):".formatted(width, height));
        Gdx.app.log("Generation", "Height range: %.2f - %.2f".formatted(minHeight, maxHeight));
        Gdx.app.log("Generation",
                    "Moisture range: %.2f - %.2f".formatted(minMoisture, maxMoisture));
        Gdx.app.log("Generation", "Temperature range: %.2f - %.2f".formatted(minTemperature,
                                                                             maxTemperature));
        Gdx.app.log("Generation", "Aridity range: %.2f - %.2f".formatted(minAridity, maxAridity));

        for (HexType type : HexType.values()) {
            int count = typeCounts[type.ordinal()];
            double percentage = (double) count / totalCount * 100;
            Gdx.app.log("Generation",
                        "- %s: %d (%.1f%%)".formatted(type.getName(), count, percentage));
        }

        return map;
    }

    /**
     * Нормализует карту для получения полного диапазона [0, 1]
     */
    private void normalizeMap(float[][] map) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        // Находим минимальное и максимальное значения
        for (int q = 0; q < width; q++) {
            for (int r = 0; r < height; r++) {
                min = Math.min(min, map[q][r]);
                max = Math.max(max, map[q][r]);
            }
        }

        // Нормализуем значения
        float range = max - min;
        if (range > 0) {
            for (int q = 0; q < width; q++) {
                for (int r = 0; r < height; r++) {
                    map[q][r] = (map[q][r] - min) / range;
                }
            }
        }
    }

    /**
     * Генерирует значение высоты на основе шума Перлина
     */
    private float generateHeightValue(float nx, float ny) {
        // Континентальный шум - определяет основные массы суши
        float continentalValue = continentalNoise.noise(nx * 1.2f, ny * 1.2f);
        continentalValue = (continentalValue + 1.0f) / 2.0f;

        // Детализированный шум рельефа
        float terrainValue = terrainNoise.noise(nx * 4, ny * 4) * 0.5f
                             + terrainNoise.noise(nx * 8, ny * 8) * 0.3f
                             + terrainNoise.noise(nx * 16, ny * 16) * 0.2f;
        terrainValue = (terrainValue + 1.0f) / 2.0f;

        // Горный шум - создает высокие горные области
        float mountainValue = mountainNoise.noise(nx * 6, ny * 6);
        mountainValue = (mountainValue + 1.0f) / 2.0f;
        mountainValue = (float) Math.pow(mountainValue, 2.2f); // Усиливаем пики

        float plateNoise = continentalNoise.noise(nx * 0.3f, ny * 0.3f);
        plateNoise = (plateNoise + 1.0f) / 2.0f;

        float mountainRidge = mountainNoise.noise(nx * 8, ny * 8);
        mountainRidge = (float) Math.pow(mountainRidge, 3); // Создать более выраженные хребты

        // Комбинируем все шумы
        float heightValue = continentalValue * 0.5f + terrainValue * 0.3f + mountainValue * 0.2f +
                            plateNoise * 0.1f + mountainRidge * 0.15f;

        // Усиливаем контраст для создания более выраженного рельефа
        heightValue = (float) Math.pow(heightValue, 1.3f);

        return heightValue;
    }

    /**
     * Генерирует значение влажности на основе шума Перлина
     */
    private float generateMoistureValue(float nx, float ny) {
        // Многоуровневый шум для влажности
        float noiseValue = moistureNoise.noise(nx * 3, ny * 3) * 0.6f
                           + moistureNoise.noise(nx * 6, ny * 6) * 0.3f
                           + moistureNoise.noise(nx * 12, ny * 12) * 0.1f;

        return (noiseValue + 1.0f) / 2.0f;
    }

    /**
     * Генерирует значение температуры на основе шума Перлина
     */
    private float generateTemperatureValue(float nx, float ny) {
        // Многоуровневый шум для температуры
        float noiseValue = temperatureNoise.noise(nx * 2, ny * 2) * 0.7f
                           + temperatureNoise.noise(nx * 4, ny * 4) * 0.3f;

        // Нормализуем шум к диапазону [0, 1]
        noiseValue = (noiseValue + 1.0f) / 2.0f;

        // Добавляем широтный градиент (холоднее к полюсам)
        float latitudeEffect = Math.abs(ny - 0.5f) * 2.0f; // 0 на экваторе, 1 на полюсах
        noiseValue -= latitudeEffect * 0.4f;

        return Math.max(0.1f, Math.min(1, noiseValue));
    }

    /**
     * Генерирует значение аридности на основе шума Перлина
     */
    private float generateAridityValue(float nx, float ny) {
        // Многоуровневый шум для аридности
        float noiseValue = aridityNoise.noise(nx * 2, ny * 2) * 0.7f
                           + aridityNoise.noise(nx * 4, ny * 4) * 0.3f;

        return (noiseValue + 1.0f) / 2.0f;
    }

    /**
     * Определяет тип гекса на основе высоты, влажности, температуры и аридности
     */
    private HexType determineHexType(float height, float moisture, float temperature,
                                     float aridity) {
        // Определяем базовый биом на основе высоты
        if (height < 0.4f) {
            return HexType.OCEAN;
        } else if (height < 0.45f) {
            return HexType.COAST;
        }

        // Для суши используем комбинацию факторов
        if (height > 0.8f) {
            // Высокогорные биомы
            if (temperature < 0.3f) {
                return HexType.MOUNTAINS; // Заснеженные горы
            } else if (aridity > 0.7f && moisture < 0.4f) {
                return HexType.DESERT; // Высокогорная пустыня
            } else {
                return HexType.MOUNTAINS;
            }
        } else if (height > 0.65f) {
            // Возвышенности
            if (aridity > 0.6f && moisture < 0.4f) {
                return HexType.DESERT;
            } else if (moisture < 0.5f) {
                return HexType.PLAINS;
            } else if (moisture < 0.7f) {
                return HexType.FOREST;
            } else {
                return HexType.SWAMP;
            }
        } else if (height > 0.5f) {
            // Равнины
            if (aridity > 0.6f && moisture < 0.4f) {
                return HexType.DESERT;
            } else if (moisture < 0.6f) {
                return HexType.PLAINS;
            } else if (moisture < 0.8f) {
                return HexType.FOREST;
            } else {
                return HexType.SWAMP;
            }
        } else {
            // Низменности
            if (aridity > 0.6f && moisture < 0.4f) {
                return HexType.DESERT;
            } else if (moisture < 0.6f) {
                return HexType.PLAINS;
            } else if (moisture < 0.8f) {
                return HexType.FOREST;
            } else {
                return HexType.SWAMP;
            }
        }
    }

    /**
     * Реализация шума Перлина для процедурной генерации
     */
    private static class PerlinNoise {

        private final Random random;
        private final int[] permutation;

        public PerlinNoise(long seed) {
            this.random = new Random(seed);
            this.permutation = new int[512];
            initializePermutation();
        }

        public float noise(float x, float y) {
            int X = (int) Math.floor(x) & 255;
            int Y = (int) Math.floor(y) & 255;

            x -= Math.floor(x);
            y -= Math.floor(y);

            float u = fade(x);
            float v = fade(y);

            int a = permutation[X] + Y;
            int aa = permutation[a];
            int ab = permutation[a + 1];
            int b = permutation[X + 1] + Y;
            int ba = permutation[b];
            int bb = permutation[b + 1];

            return lerp(v, lerp(u, grad(permutation[aa], x, y),
                                grad(permutation[ba], x - 1, y)),
                        lerp(u, grad(permutation[ab], x, y - 1),
                             grad(permutation[bb], x - 1, y - 1)));
        }

        private void initializePermutation() {
            int[] p = new int[256];
            for (int i = 0; i < 256; i++) {
                p[i] = i;
            }

            // Перемешиваем массив
            for (int i = 0; i < 256; i++) {
                int j = random.nextInt(256);
                int temp = p[i];
                p[i] = p[j];
                p[j] = temp;
            }

            // Дублируем для упрощения вычислений
            for (int i = 0; i < 256; i++) {
                permutation[256 + i] = permutation[i] = p[i];
            }
        }

        private float fade(float t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private float lerp(float t, float a, float b) {
            return a + t * (b - a);
        }

        private float grad(int hash, float x, float y) {
            int h = hash & 15;
            float u = h < 8 ? x : y;
            float v = h < 4 ? y : (h == 12 || h == 14 ? x : 0);
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
}
