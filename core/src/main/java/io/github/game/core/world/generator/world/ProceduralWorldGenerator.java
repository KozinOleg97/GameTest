package io.github.game.core.world.generator.world;

import com.badlogic.gdx.Gdx;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexType;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Продвинутый генератор мира с использованием многоуровневого шума Перлина. Использует
 * комбинированный подход с континентальным и климатическим моделированием.
 * <p>
 * ОСНОВНАЯ ЛОГИКА: 1. Генерация отдельных карт высот, влажности, температуры и аридности с помощью
 * шума Перлина. 2. Нормализация каждой карты в диапазон [0, 1] для согласованности. 3.
 * Комбинирование параметров для каждого гекса в функции determineHexType(). 4. Создание гексов и
 * сбор статистики генерации.
 */
public class ProceduralWorldGenerator implements WorldGenerator {

    private final int width;
    private final int height;
    private final long seed;
    private final Random random;

    // Различные генераторы шума для разных аспектов мира
    // Каждый использует свой seed для независимости паттернов
    private final PerlinNoise continentalNoise;  // Основные формы континентов
    private final PerlinNoise terrainNoise;      // Детали рельефа
    private final PerlinNoise moistureNoise;     // Распределение влажности
    private final PerlinNoise temperatureNoise;  // Распределение температуры
    private final PerlinNoise mountainNoise;     // Специфически для горных хребтов
    private final PerlinNoise aridityNoise;      // Уровень засушливости

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
        // Каждый тип шума использует свой seed для независимых паттернов
        this.continentalNoise = new PerlinNoise(seed);
        this.terrainNoise = new PerlinNoise(seed + 1);
        this.moistureNoise = new PerlinNoise(seed + 2);
        this.temperatureNoise = new PerlinNoise(seed + 3);
        this.mountainNoise = new PerlinNoise(seed + 4);
        this.aridityNoise = new PerlinNoise(seed + 5);
    }

    @Override
    public HexMap generateWorld() {
        // Создаем карту с указанием размеров
        HexMap map = new HexMap(width, height);

        // Массивы для сбора статистики по типам гексов
        int[] typeCounts = new int[HexType.values().length];
        int totalCount = 0;

        // Переменные для отслеживания диапазонов параметров (отладка/балансировка)
        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;
        float minMoisture = Float.MAX_VALUE;
        float maxMoisture = Float.MIN_VALUE;
        float minTemperature = Float.MAX_VALUE;
        float maxTemperature = Float.MIN_VALUE;
        float minAridity = Float.MAX_VALUE;
        float maxAridity = Float.MIN_VALUE;

        // Предварительная генерация всех карт параметров
        float[][] heightMap = new float[width][height];
        float[][] moistureMap = new float[width][height];
        float[][] temperatureMap = new float[width][height];
        float[][] aridityMap = new float[width][height];

        // ПАРАЛЛЕЛЬНАЯ ГЕНЕРАЦИЯ КАРТ: увеличивает производительность на больших картах
        IntStream.range(0, width)
                 .parallel() // Использует многопоточность для ускорения
                 .forEach(q -> {
                     for (int r = 0; r < height; r++) {
                         // Нормализованные координаты позволяют масштабировать шум независимо от размера карты
                         float nx = (float) q / width;
                         float ny = (float) r / height;

                         // Заполняем карты исходными значениями
                         heightMap[q][r] = generateHeightValue(nx, ny);
                         moistureMap[q][r] = generateMoistureValue(nx, ny);
                         temperatureMap[q][r] = generateTemperatureValue(nx, ny);
                         aridityMap[q][r] = generateAridityValue(nx, ny);
                     }
                 });

        long startTime = System.currentTimeMillis();

        // МНОГОПОТОЧНАЯ НОРМАЛИЗАЦИЯ: каждая карта нормализуется в отдельном потоке
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CompletableFuture[] futures = new CompletableFuture[]{
            CompletableFuture.runAsync(() -> normalizeMap(heightMap), executorService),
            CompletableFuture.runAsync(() -> normalizeMap(moistureMap), executorService),
            CompletableFuture.runAsync(() -> normalizeMap(temperatureMap), executorService),
            CompletableFuture.runAsync(() -> normalizeMap(aridityMap), executorService)
        };
        CompletableFuture.allOf(futures).join(); // Ожидаем завершения всех задач
        executorService.shutdown();

        long duration = System.currentTimeMillis() - startTime;

        Gdx.app.log("Custom", "Hex entities creation completed " +
                              duration + " mils");

        // ОСНОВНОЙ ЦИКЛ: создание гексов на основе нормализованных карт
        for (int q = 0; q < width; q++) {
            for (int r = 0; r < height; r++) {
                totalCount++;

                // Получаем нормализованные значения для текущего гекса
                float heightValue = heightMap[q][r];
                float moistureValue = moistureMap[q][r];
                float temperatureValue = temperatureMap[q][r];
                float aridityValue = aridityMap[q][r];

                // Обновляем статистику диапазонов
                minHeight = Math.min(minHeight, heightValue);
                maxHeight = Math.max(maxHeight, heightValue);
                minMoisture = Math.min(minMoisture, moistureValue);
                maxMoisture = Math.max(maxMoisture, moistureValue);
                minTemperature = Math.min(minTemperature, temperatureValue);
                maxTemperature = Math.max(maxTemperature, temperatureValue);
                minAridity = Math.min(minAridity, aridityValue);
                maxAridity = Math.max(maxAridity, aridityValue);

                // КЛЮЧЕВАЯ ЛОГИКА: определение типа гекса по комбинации параметров
                HexType type = determineHexType(heightValue, moistureValue, temperatureValue,
                                                aridityValue);

                // Статистика по типам
                typeCounts[type.ordinal()]++;

                // Создаем гекс. Уникальный seed на основе параметров позволяет
                // дополнительно варьировать детали (растительность и т.д.)
                Hex hex = new Hex(q, r, type);
                hex.setGenerationSeed(
                    (long) (heightValue * 1000 + moistureValue * 100 + temperatureValue * 10 +
                            aridityValue * 1));

                // Добавляем гекс на карту
                try {
                    map.addHex(hex);
                } catch (IllegalArgumentException e) {
                    Gdx.app.error("Generation",
                                  "Failed to add hex at (" + q + ", " + r + "): " + e.getMessage());
                }
            }
        }

        // ВЫВОД СТАТИСТИКИ: полезно для балансировки и отладки
        Gdx.app.log("Generation", "World generation complete (%dx%d):".formatted(width, height));
        Gdx.app.log("Generation", "Height range: %.2f - %.2f".formatted(minHeight, maxHeight));
        Gdx.app.log("Generation",
                    "Moisture range: %.2f - %.2f".formatted(minMoisture, maxMoisture));
        Gdx.app.log("Generation", "Temperature range: %.2f - %.2f".formatted(minTemperature,
                                                                             maxTemperature));
        Gdx.app.log("Generation", "Aridity range: %.2f - %.2f".formatted(minAridity, maxAridity));

        // Распределение биомов в процентах
        for (HexType type : HexType.values()) {
            int count = typeCounts[type.ordinal()];
            double percentage = (double) count / totalCount * 100;
            Gdx.app.log("Generation",
                        "- %s: %d (%.1f%%)".formatted(type.getName(), count, percentage));
        }

        return map;
    }

    /**
     * Нормализует карту для получения полного диапазона [0, 1] Это важно для согласованности
     * пороговых значений в determineHexType
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
     * Генерирует значение высоты на основе шума Перлина. Ключевые особенности: - continentalNoise:
     * задает базовые очертания континентов (крупные формы) - terrainNoise: добавляет детализацию
     * рельефа (фрактальный шум) - mountainNoise: создает специфические горные хребты - plateNoise:
     * очень низкочастотный шум для тектонических плит - mountainRidge: высокочастотный шум для
     * острых горных пиков - Комбинирование с весами: основные формы + детали + горы - Возведение в
     * степень (1.3): увеличивает контраст, подчеркивая долины и пики
     */
    private float generateHeightValue(float nx, float ny) {
        // Континентальный шум - определяет основные массы суши
        float continentalValue = continentalNoise.noise(nx * 1.2f, ny * 1.2f);
        continentalValue = (continentalValue + 1.0f) / 2.0f;

        // Детализированный шум рельефа (фрактальный шум с октавами)
        float terrainValue = terrainNoise.noise(nx * 4, ny * 4) * 0.5f
                             + terrainNoise.noise(nx * 8, ny * 8) * 0.3f
                             + terrainNoise.noise(nx * 16, ny * 16) * 0.2f;
        terrainValue = (terrainValue + 1.0f) / 2.0f;

        // Горный шум - создает высокие горные области
        float mountainValue = mountainNoise.noise(nx * 6, ny * 6);
        mountainValue = (mountainValue + 1.0f) / 2.0f;
        mountainValue = (float) Math.pow(mountainValue, 2.2f); // Усиливаем пики

        // Низкочастотный шум для тектонических плит
        float plateNoise = continentalNoise.noise(nx * 0.3f, ny * 0.3f);
        plateNoise = (plateNoise + 1.0f) / 2.0f;

        // Высокочастотный шум для горных хребтов
        float mountainRidge = mountainNoise.noise(nx * 8, ny * 8);
        mountainRidge = (float) Math.pow(mountainRidge, 3); // Создать более выраженные хребты

        // Комбинируем все шумы с весовыми коэффициентами
        float heightValue = continentalValue * 0.5f + terrainValue * 0.3f + mountainValue * 0.2f +
                            plateNoise * 0.1f + mountainRidge * 0.15f;

        // Усиливаем контраст для создания более выраженного рельефа
        heightValue = (float) Math.pow(heightValue, 1.3f);

        return heightValue;
    }

    /**
     * Генерирует значение влажности. Использует фрактальный шум (октавы) для создания естественных
     * паттернов осадков.
     */
    private float generateMoistureValue(float nx, float ny) {
        // Многоуровневый шум для влажности (фрактальный шум)
        float noiseValue = moistureNoise.noise(nx * 3, ny * 3) * 0.6f
                           + moistureNoise.noise(nx * 6, ny * 6) * 0.3f
                           + moistureNoise.noise(nx * 12, ny * 12) * 0.1f;

        return (noiseValue + 1.0f) / 2.0f;
    }

    /**
     * Генерирует значение температуры. Особенности: - Фрактальный шум для базового распределения -
     * Широтный градиент (latitudeEffect): температура падает к полюсам (ny = Y-координата) -
     * Клиппинг значения: ограничение диапазона [0.1, 1.0]
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

        // Ограничиваем диапазон для избежания экстремальных значений
        return Math.max(0.1f, Math.min(1, noiseValue));
    }

    /**
     * Генерирует значение аридности (засушливости). Аридность может коррелировать с влажностью, но
     * использует независимый шум для большего разнообразия биомов (например, высокогорные
     * пустыни).
     */
    private float generateAridityValue(float nx, float ny) {
        // Многоуровневый шум для аридности
        float noiseValue = aridityNoise.noise(nx * 2, ny * 2) * 0.7f
                           + aridityNoise.noise(nx * 4, ny * 4) * 0.3f;

        return (noiseValue + 1.0f) / 2.0f;
    }

    /**
     * Определяет тип гекса на основе высоты, влажности, температуры и аридности. Логика выстроена в
     * виде каскада условий от самого определяющего фактора (высота) к более тонким (влажность,
     * аридность).
     * <p>
     * ИЕРАРХИЯ ПРИНЯТИЯ РЕШЕНИЙ: 1. ВЫСОТА: вода/берег/суша 2. Для суши: высотные пояса
     * (низменности, равнины, возвышенности, горы) 3. В каждом поясе: влажность и аридность
     * определяют конкретный биом
     * <p>
     * Пороговые значения (0.4, 0.6 и т.д.) - ключевые параметры для балансировки мира. Их можно
     * настраивать для изменения соотношения биомов.
     */
    private HexType determineHexType(float height, float moisture, float temperature,
                                     float aridity) {
        // 1. Вода и береговая линия - самые определяющие факторы
        if (height < 0.4f) {
            return HexType.OCEAN;
        } else if (height < 0.45f) {
            return HexType.COAST; // Узкая полоса побережья
        }

        // 2. СУША: Разделение по высотным поясам
        if (height > 0.8f) {
            // Высокогорье
            if (temperature < 0.3f) {
                return HexType.MOUNTAINS; // Снежные горы
            } else if (aridity > 0.7f && moisture < 0.4f) {
                return HexType.DESERT; // Высокогорная пустыня (редко)
            } else {
                return HexType.MOUNTAINS; // Обычные горы
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
     * Упрощенная реализация шума Перлина для процедурной генерации. Основные принципы: -
     * permutation table: таблица псевдослучайных градиентов для воспроизводимости - fade-функция:
     * сглаживает интерполяцию для естественного вида - grad-функция: вычисляет скалярное
     * произведение для градиента - lerp: линейная интерполяция между значениями
     * <p>
     * Это классическая реализация, хорошо подходящая для задач генерации ландшафта.
     */
    private static class PerlinNoise {

        private final Random random;
        private final int[] permutation; // Таблица перестановок для градиентов

        public PerlinNoise(long seed) {
            this.random = new Random(seed);
            this.permutation = new int[512];
            initializePermutation();
        }

        /**
         * Основная функция шума Перлина
         *
         * @param x координата X
         * @param y координата Y
         * @return значение шума в диапазоне примерно [-1, 1]
         */
        public float noise(float x, float y) {
            // Определяем единичный квадрат, в котором находится точка
            int X = (int) Math.floor(x) & 255;
            int Y = (int) Math.floor(y) & 255;

            // Локальные координаты внутри квадрата
            x -= Math.floor(x);
            y -= Math.floor(y);

            // Вычисляем fade-кривые для сглаживания
            float u = fade(x);
            float v = fade(y);

            // Получаем градиенты для четырех углов квадрата
            int a = permutation[X] + Y;
            int aa = permutation[a];
            int ab = permutation[a + 1];
            int b = permutation[X + 1] + Y;
            int ba = permutation[b];
            int bb = permutation[b + 1];

            // Интерполируем между градиентами
            return lerp(v, lerp(u, grad(permutation[aa], x, y),
                                grad(permutation[ba], x - 1, y)),
                        lerp(u, grad(permutation[ab], x, y - 1),
                             grad(permutation[bb], x - 1, y - 1)));
        }

        /**
         * Инициализация таблицы перестановок
         */
        private void initializePermutation() {
            int[] p = new int[256];
            for (int i = 0; i < 256; i++) {
                p[i] = i;
            }

            // Перемешиваем массив используя seed
            for (int i = 0; i < 256; i++) {
                int j = random.nextInt(256);
                int temp = p[i];
                p[i] = p[j];
                p[j] = temp;
            }

            // Дублируем для упрощения вычислений (избегание операций modulo)
            for (int i = 0; i < 256; i++) {
                permutation[256 + i] = permutation[i] = p[i];
            }
        }

        /**
         * Функция сглаживания (fade function) для плавной интерполяции
         */
        private float fade(float t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        /**
         * Линейная интерполяция
         */
        private float lerp(float t, float a, float b) {
            return a + t * (b - a);
        }

        /**
         * Вычисление градиента
         */
        private float grad(int hash, float x, float y) {
            int h = hash & 15; // Младшие 4 бита определяют градиент
            float u = h < 8 ? x : y;
            float v = h < 4 ? y : (h == 12 || h == 14 ? x : 0);
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }

    private float optimizePower(float value, float exponent) {
        // Для целочисленных степеней используем умножение
        if (exponent == 2) return value * value;
        if (exponent == 3) return value * value * value;
        return (float) Math.pow(value, exponent);
    }
}
