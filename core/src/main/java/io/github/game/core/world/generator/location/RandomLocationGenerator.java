package io.github.game.core.world.generator.location;

import com.badlogic.ashley.core.Entity;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.generator.GenerationContext;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexCoordinates;
import io.github.game.core.world.hex.HexType;
import io.github.game.core.world.hex.HexUtils;
import io.github.game.ecs.EntityFactory;
import io.github.game.ecs.components.world.GlobalPositionComponent;
import io.github.game.ecs.components.world.LocationComponent;
import io.github.game.services.NameService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Улучшенный генератор случайных локаций с равномерным распределением по карте
 */
public class RandomLocationGenerator implements LocationGenerator {

    // Конфигурационные параметры
    private static final int CITY_RESERVE_RADIUS = 5;          // 3
    private static final int SETTLEMENT_RESERVE_RADIUS = 3;    // 1
    private static final int SPECIAL_RESERVE_RADIUS = 2;       // 1
    private static final int MIN_DISTANCE_BETWEEN_CITIES = 25; // 15
    private static final int SETTLEMENTS_PER_CITY = 2;         // 3
    private static final int SEARCH_RADIUS_AROUND_CITIES = 15; // 12
    private final int numberOfLocations;
    private final long seed;
    private final Random random;
    private final GenerationContext context;
    private final EntityFactory entityFactory;
    private final NameService nameService;
    @Getter
    private HexMap hexMap;

    public RandomLocationGenerator(int numberOfLocations, long seed, GenerationContext context,
                                   EntityFactory entityFactory) {
        this.numberOfLocations = numberOfLocations;
        this.seed = seed;
        this.random = new Random(seed);
        this.context = context;
        this.entityFactory = entityFactory;
        this.nameService = context.getNameService();
    }

    @Override
    public List<Entity> generateLocations() {
        this.hexMap = context.getHexMap();

        if (hexMap == null) {
            throw new IllegalStateException("HexMap must be set before generating locations");
        }

        List<Entity> locations = new ArrayList<>();
        Set<HexCoordinates> usedCoordinates = new HashSet<>();

        // 1. Генерация крупных городов с зональным распределением
        generateMajorCities(locations, usedCoordinates);

        // 2. Генерация поселений вокруг городов
        generateSettlements(locations, usedCoordinates);

        // 3. Генерация специальных локаций
        generateSpecialLocations(locations, usedCoordinates);

        // 4. Заполнение оставшихся локаций случайными поселениями
        fillRemainingLocations(locations, usedCoordinates);

        logLocationDistribution(locations);
        return locations;
    }

    /**
     * Генерация крупных городов с равномерным распределением по зонам
     */
    private void generateMajorCities(List<Entity> locations, Set<HexCoordinates> usedCoordinates) {
        int majorCities = Math.min(3, numberOfLocations / 4);

        // Разделяем карту на зоны для равномерного распределения
        List<Zone> zones = createZones(majorCities);

        for (Zone zone : zones) {
            Hex suitableHex = findBestHexInZone(zone, usedCoordinates, this::isSuitableForCity, this::rateCityLocation);
            if (suitableHex != null) {
                String cityName = nameService.getRandomName("cities");
                Entity city = createLocationEntity(suitableHex, "CITY", cityName, 3);
                locations.add(city);
                usedCoordinates.add(suitableHex.getCoordinates());

                // Резервируем территорию вокруг города
                reserveArea(suitableHex.getCoordinates(), CITY_RESERVE_RADIUS, usedCoordinates);
            }
        }
    }

    /**
     * Генерация поселений вокруг крупных городов
     */
    private void generateSettlements(List<Entity> locations, Set<HexCoordinates> usedCoordinates) {
        // Находим существующие города для создания вокруг них
        List<HexCoordinates> cityCoordinates = getCityCoordinates(locations);

        if (cityCoordinates.isEmpty()) {
            // Если городов нет, создаем поселения равномерно по карте
            generateDistributedSettlements(locations, usedCoordinates, numberOfLocations / 2);
            return;
        }

        int settlementsPerCity = SETTLEMENTS_PER_CITY;
        int totalSettlements = Math.min(numberOfLocations - locations.size(),
                                        cityCoordinates.size() * settlementsPerCity);

        for (HexCoordinates cityCoord : cityCoordinates) {
            for (int i = 0; i < settlementsPerCity && locations.size() < numberOfLocations; i++) {
                Hex suitableHex = findSettlementNearCity(cityCoord, usedCoordinates);
                if (suitableHex != null) {
                    createSettlement(suitableHex, locations, usedCoordinates);
                }
            }
        }

        // Если остались места для поселений, размещаем их равномерно
        int remainingSettlements = numberOfLocations - locations.size();
        if (remainingSettlements > 0) {
            generateDistributedSettlements(locations, usedCoordinates, remainingSettlements);
        }
    }

    /**
     * Генерация равномерно распределенных поселений
     */
    private void generateDistributedSettlements(List<Entity> locations, Set<HexCoordinates> usedCoordinates,
                                                int count) {
        List<Zone> zones = createZones(count);

        for (Zone zone : zones) {
            if (locations.size() >= numberOfLocations) {
                break;
            }

            Hex suitableHex = findBestHexInZone(zone, usedCoordinates, this::isSuitableForSettlement,
                                                this::rateSettlementLocation);

            if (suitableHex != null) {
                createSettlement(suitableHex, locations, usedCoordinates);
            }
        }
    }

    /**
     * Создание поселения
     */
    private void createSettlement(Hex hex, List<Entity> locations, Set<HexCoordinates> usedCoordinates) {
        String[] settlementTypes = {"VILLAGE", "TOWN", "OUTPOST"};
        String type = settlementTypes[random.nextInt(settlementTypes.length)];

        String name = getSettlementName(type);
        Entity settlement = createLocationEntity(hex, type, name, 1);
        locations.add(settlement);
        usedCoordinates.add(hex.getCoordinates());
        reserveArea(hex.getCoordinates(), SETTLEMENT_RESERVE_RADIUS, usedCoordinates);
    }

    /**
     * Генерация специальных локаций
     */
    private void generateSpecialLocations(List<Entity> locations, Set<HexCoordinates> usedCoordinates) {
        int remaining = numberOfLocations - locations.size();
        if (remaining <= 0) {
            return;
        }

        // Распределяем типы специальных локаций
        Map<SpecialLocationType, Integer> typeDistribution = createSpecialLocationDistribution(remaining);

        for (Map.Entry<SpecialLocationType, Integer> entry : typeDistribution.entrySet()) {
            for (int i = 0; i < entry.getValue() && locations.size() < numberOfLocations; i++) {
                Hex suitableHex = findSuitableHexForSpecialLocation(entry.getKey(), usedCoordinates);
                if (suitableHex != null) {
                    SpecialLocation special = createSpecialLocation(entry.getKey(), suitableHex);
                    Entity specialEntity = createLocationEntity(suitableHex, special.type, special.name, special.size);
                    locations.add(specialEntity);
                    usedCoordinates.add(suitableHex.getCoordinates());
                    reserveArea(suitableHex.getCoordinates(), SPECIAL_RESERVE_RADIUS, usedCoordinates);
                }
            }
        }
    }

    /**
     * Заполнение оставшихся слотов локациями
     */
    private void fillRemainingLocations(List<Entity> locations, Set<HexCoordinates> usedCoordinates) {
        int remaining = numberOfLocations - locations.size();
        if (remaining <= 0) {
            return;
        }

        // Ищем любые подходящие гексы для оставшихся локаций
        List<Hex> availableHexes = findAllSuitableHexes(usedCoordinates, this::isSuitableForSettlement);
        Collections.shuffle(availableHexes, random);

        for (int i = 0; i < Math.min(remaining, availableHexes.size()); i++) {
            Hex hex = availableHexes.get(i);
            createSettlement(hex, locations, usedCoordinates);
        }
    }


    /**
     * Поиск лучшего гекса в зоне по критериям
     */
    private Hex findBestHexInZone(Zone zone, Set<HexCoordinates> usedCoordinates,
                                  java.util.function.Predicate<Hex> suitabilityChecker,
                                  java.util.function.Function<Hex, Integer> rater) {
        Hex bestHex = null;
        int bestScore = -1;

        // Обходим зону в случайном порядке для разнообразия
        List<Integer> qList = createShuffledRange(zone.startQ, zone.endQ);
        List<Integer> rList = createShuffledRange(zone.startR, zone.endR);

        for (Integer q : qList) {
            for (Integer r : rList) {
                Hex hex = hexMap.getHex(q, r);
                if (hex != null && !usedCoordinates.contains(hex.getCoordinates()) && suitabilityChecker.test(hex)) {
                    int score = rater.apply(hex);
                    if (score > bestScore) {
                        bestScore = score;
                        bestHex = hex;
                    }
                }
            }
        }

        return bestHex;
    }

    /**
     * Поиск поселения рядом с городом
     */
    private Hex findSettlementNearCity(HexCoordinates cityCoord, Set<HexCoordinates> usedCoordinates) {
        List<Hex> candidates = new ArrayList<>();

        int searchRadius = SEARCH_RADIUS_AROUND_CITIES;
        for (int r = Math.max(0, cityCoord.getR() - searchRadius);
             r <= Math.min(hexMap.getHeight() - 1, cityCoord.getR() + searchRadius); r++) {
            for (int q = Math.max(0, cityCoord.getQ() - searchRadius);
                 q <= Math.min(hexMap.getWidth() - 1, cityCoord.getQ() + searchRadius); q++) {

                Hex hex = hexMap.getHex(q, r);
                if (hex != null && !usedCoordinates.contains(hex.getCoordinates())) {
                    int distance = HexUtils.distance(cityCoord, hex.getCoordinates());
                    // Поселения должны быть на расстоянии 3-8 гексов от города
                    if (distance >= 3 && distance <= 8 && isSuitableForSettlement(hex)) {
                        candidates.add(hex);
                    }
                }
            }
        }

        if (!candidates.isEmpty()) {
            // Сортируем по качеству и берем лучший
            candidates.sort((h1, h2) -> Integer.compare(rateSettlementLocation(h2), rateSettlementLocation(h1)));
            return candidates.get(0);
        }

        return null;
    }

    /**
     * Поиск гекса для специальной локации
     */
    private Hex findSuitableHexForSpecialLocation(SpecialLocationType specialType,
                                                  Set<HexCoordinates> usedCoordinates) {
        List<Hex> candidates = new ArrayList<>();

        for (int r = 0; r < hexMap.getHeight(); r++) {
            for (int q = 0; q < hexMap.getWidth(); q++) {
                Hex hex = hexMap.getHex(q, r);
                if (hex != null && !usedCoordinates.contains(hex.getCoordinates())) {
                    if (isSuitableForSpecialLocation(hex, specialType) &&
                        isWellPlacedSpecialLocation(hex, specialType)) {
                        candidates.add(hex);
                    }
                }
            }
        }

        if (!candidates.isEmpty()) {
            // Сортируем по пригодности для конкретного типа
            candidates.sort((h1, h2) -> Integer.compare(rateSpecialLocation(h2, specialType),
                                                        rateSpecialLocation(h1, specialType)));
            return candidates.get(0);
        }

        return null;
    }

    /**
     * Поиск всех подходящих гексов
     */
    private List<Hex> findAllSuitableHexes(Set<HexCoordinates> usedCoordinates,
                                           java.util.function.Predicate<Hex> suitabilityChecker) {
        List<Hex> suitableHexes = new ArrayList<>();

        for (int r = 0; r < hexMap.getHeight(); r++) {
            for (int q = 0; q < hexMap.getWidth(); q++) {
                Hex hex = hexMap.getHex(q, r);
                if (hex != null && !usedCoordinates.contains(hex.getCoordinates()) && suitabilityChecker.test(hex)) {
                    suitableHexes.add(hex);
                }
            }
        }

        return suitableHexes;
    }


    /**
     * Оценка гекса для города
     */
    private int rateCityLocation(Hex hex) {
        int score = 0;

        // Бонус за тип местности
        score += switch (hex.getType()) {
            case PLAINS -> 10;
            case COAST -> 8;
            case FOREST -> 5;
            default -> 0;
        };

        // Бонус за количество подходящих соседей
        score += countSuitableNeighbors(hex) * 3;

        // Бонус за доступ к воде (для портов и торговли)
        if (hasWaterAccess(hex)) {
            score += 5;
        }

        // Бонус за центральное положение в зоне
        score += calculateCentralityBonus(hex);

        return score;
    }

    /**
     * Оценка гекса для поселения
     */
    private int rateSettlementLocation(Hex hex) {
        int score = 0;

        // Базовый бонус за тип местности
        score += switch (hex.getType()) {
            case PLAINS -> 8;
            case FOREST -> 6;
            case COAST -> 4;
            default -> 0;
        };

        // Бонус за соседей
        score += countSuitableNeighbors(hex) * 2;

        // Бонус за близость к ресурсам
        if (hasResourcePotential(hex)) {
            score += 3;
        }

        return score;
    }

    /**
     * Оценка гекса для специальной локации
     */
    private int rateSpecialLocation(Hex hex, SpecialLocationType specialType) {
        return switch (specialType) {
            case PORT -> ratePortLocation(hex);
            case FORTRESS -> rateFortressLocation(hex);
            case MONASTERY -> rateMonasteryLocation(hex);
            case MINE -> rateMineLocation(hex);
            case WATCHTOWER -> rateWatchtowerLocation(hex);
            case BRIDGE -> rateBridgeLocation(hex);
        };
    }

    private int rateMonasteryLocation(Hex hex) {
        int score = 0;

        // Базовый бонус за тип местности
        score += switch (hex.getType()) {
            case FOREST -> 15;
            case MOUNTAINS -> 12;
            case PLAINS -> 8;
            default -> 0;
        };

        // Бонус за уединенность (мало соседей подходящих для поселений)
        int settlementNeighbors = countNeighbors(hex, this::isSuitableForSettlement);
        score += Math.max(0, 10 - settlementNeighbors * 2);

        // Бонус за естественную защиту (скалы, вода)
        if (hasNaturalProtection(hex)) {
            score += 8;
        }

        // Бонус за наличие источника воды рядом
        if (hasFreshWaterAccess(hex)) {
            score += 10;
        }

        // Бонус за высоту (близость к небесам)
        if (isOnHighGround(hex)) {
            score += 5;
        }

        // Штраф за близость к городам и оживленным местам
        if (isNearCivilization(hex)) {
            score -= 15;
        }

        // Бонус за живописное окружение (разные типы ландшафта)
        if (hasScenicViews(hex)) {
            score += 7;
        }

        return Math.max(0, score);
    }

    private int rateMineLocation(Hex hex) {
        int score = 0;

        // Основной бонус за тип местности
        score += switch (hex.getType()) {
            case MOUNTAINS -> 20;
            case PLAINS -> 12;
            case FOREST -> 8;
            default -> 0;
        };

        // Бонус за богатство недр (определяем по соседним горам)
        int mountainNeighbors = countNeighborsByType(hex, HexType.MOUNTAINS);
        score += mountainNeighbors * 4;

        // Бонус за доступ к воде (для обработки руды)
        if (hasWaterAccess(hex)) {
            score += 8;
        }

        // Бонус за транспортную доступность
        if (hasGoodTransportAccess(hex)) {
            score += 10;
        }

        // Бонус за близость к существующим поселениям (рабочая сила)
        if (isNearExistingSettlements(hex)) {
            score += 6;
        }

        // Бонус за разнообразие ресурсов (разные типы соседей)
        int differentTerrainTypes = countDifferentTerrainTypes(hex);
        score += differentTerrainTypes * 2;

        // Штраф за слишком труднодоступное место
        if (isTooRemote(hex)) {
            score -= 12;
        }

        // Бонус за естественные укрытия для оборудования
        if (hasNaturalShelter(hex)) {
            score += 5;
        }

        return Math.max(0, score);
    }


    private int rateWatchtowerLocation(Hex hex) {
        int score = 0;

        // Основной бонус за высоту и обзор
        score += switch (hex.getType()) {
            case MOUNTAINS -> 18;
            case PLAINS -> 10;
            case FOREST -> 3;
            case SWAMP -> 2;
            default -> 0;
        };

        // Бонус за высоту положения
        if (isOnHighGround(hex)) {
            score += 15;
        }

        // Бонус за стратегическое положение (контроль путей)
        int controlledPaths = countControlledPaths(hex);
        score += controlledPaths * 6;

        // Бонус за обзор (количество видимых гексов)
        int visibleHexes = calculateVisibility(hex);
        score += Math.min(20, visibleHexes / 10);

        // Бонус за пограничное положение
        if (isOnBorder(hex)) {
            score += 12;
        }

        // Бонус за близость к важным объектам (города, дороги)
        if (isNearStrategicLocations(hex)) {
            score += 8;
        }

        // Бонус за естественную защиту
        if (hasNaturalProtection(hex)) {
            score += 7;
        }

        // Штраф за плохую доступность для своих войск
        if (isTooInaccessible(hex)) {
            score -= 10;
        }

        // Бонус за линию видимости с другими башнями
        if (hasLineOfSightToOtherTowers(hex)) {
            score += 5;
        }

        return Math.max(0, score);
    }

    private int rateBridgeLocation(Hex hex) {
        int score = 0;

        // Основной бонус - соединение разных типов местности
        if (connectsDifferentTerrains(hex)) {
            score += 25;
        }

        // Бонус за количество соединяемых разных типов
        int differentTerrainTypes = countDifferentTerrainTypes(hex);
        score += differentTerrainTypes * 8;

        // Бонус за узкое место (минимальная ширина "реки")
        if (isNarrowPassage(hex)) {
            score += 15;
        }

        // Бонус за стратегическую важность (контроль торговых путей)
        if (controlsTradeRoute(hex)) {
            score += 20;
        }

        // Бонус за наличие естественных опор (скалы по берегам)
        if (hasNaturalFoundations(hex)) {
            score += 12;
        }

        // Бонус за безопасные берега (не болотистые)
        if (hasStableBanks(hex)) {
            score += 8;
        }

        // Бонус за прямой путь (минимум изгибов)
        if (isStraightCrossing(hex)) {
            score += 10;
        }

        // Бонус за связь между важными регионами
        if (connectsImportantRegions(hex)) {
            score += 15;
        }

        // Штраф за слишком широкий переход
        if (isTooWide(hex)) {
            score -= 15;
        }

        // Штраф за нестабильное основание (болота, песок)
        if (hasUnstableFoundation(hex)) {
            score -= 20;
        }

        return Math.max(0, score);
    }


    private int ratePortLocation(Hex hex) {
        int score = 0;
        if (hex.getType() == HexType.COAST) {
            score += 10;
        }
        if (hasDeepWaterAccess(hex)) {
            score += 5;
        }
        if (hasNaturalHarbor(hex)) {
            score += 8;
        }
        return score;
    }

    private int rateFortressLocation(Hex hex) {
        int score = 0;
        if (hex.getType() == HexType.MOUNTAINS) {
            score += 10;
        }
        if (isOnHighGround(hex)) {
            score += 8;
        }
        if (hasStrategicValue(hex)) {
            score += 7;
        }
        return score;
    }


    /**
     * Создание зон для равномерного распределения
     */
    private List<Zone> createZones(int numberOfZones) {
        List<Zone> zones = new ArrayList<>();

        int zonesPerRow = (int) Math.ceil(Math.sqrt(numberOfZones));
        int zoneWidth = hexMap.getWidth() / zonesPerRow;
        int zoneHeight = hexMap.getHeight() / zonesPerRow;

        for (int row = 0; row < zonesPerRow; row++) {
            for (int col = 0; col < zonesPerRow && zones.size() < numberOfZones; col++) {
                int startQ = col * zoneWidth;
                int endQ = (col == zonesPerRow - 1) ? hexMap.getWidth() : (col + 1) * zoneWidth;
                int startR = row * zoneHeight;
                int endR = (row == zonesPerRow - 1) ? hexMap.getHeight() : (row + 1) * zoneHeight;

                zones.add(new Zone(startQ, endQ, startR, endR));
            }
        }

        // Перемешиваем зоны для случайного порядка обработки
        Collections.shuffle(zones, random);
        return zones;
    }

    /**
     * Создание перемешанного диапазона чисел
     */
    private List<Integer> createShuffledRange(int start, int end) {
        List<Integer> range = new ArrayList<>();
        for (int i = start; i < end; i++) {
            range.add(i);
        }
        Collections.shuffle(range, random);
        return range;
    }

    /**
     * Получение координат городов
     */
    private List<HexCoordinates> getCityCoordinates(List<Entity> locations) {
        // В реальной реализации нужно извлекать координаты из компонентов сущностей
        // Здесь упрощенная версия

//        locations.stream().filter(e -> e.getComponent(LocationComponent.class)).map(LocationComponent::getCoordinates).orElse(null));
        return locations.stream().filter(e -> {
                            if (e.getComponent(LocationComponent.class) != null) {
                                return true;
                            } else {
                                return false;
                            }
                        })

                        .map(e -> e.getComponent(GlobalPositionComponent.class).getCoordinates()).toList();
    }

    /**
     * Создание распределения специальных локаций
     */
    private Map<SpecialLocationType, Integer> createSpecialLocationDistribution(int total) {
        Map<SpecialLocationType, Integer> distribution = new LinkedHashMap<>();
        int baseCount = total / 6;

        distribution.put(SpecialLocationType.PORT, baseCount);
        distribution.put(SpecialLocationType.FORTRESS, baseCount);
        distribution.put(SpecialLocationType.MONASTERY, baseCount);
        distribution.put(SpecialLocationType.MINE, baseCount);
        distribution.put(SpecialLocationType.WATCHTOWER, baseCount);
        distribution.put(SpecialLocationType.BRIDGE, total - baseCount * 5);

        return distribution;
    }

    /**
     * Проверка пригодности гекса для города
     */
    private boolean isSuitableForCity(Hex hex) {
        HexType type = hex.getType();
        return type == HexType.PLAINS || type == HexType.COAST || type == HexType.FOREST;
    }

    /**
     * Проверка пригодности для поселения
     */
    private boolean isSuitableForSettlement(Hex hex) {
        HexType type = hex.getType();
        return type != HexType.OCEAN && type != HexType.MOUNTAINS;
    }

    /**
     * Проверка пригодности для специальной локации
     */
    private boolean isSuitableForSpecialLocation(Hex hex, SpecialLocationType specialType) {
        return switch (specialType) {
            case PORT -> hex.getType() == HexType.COAST;
            case FORTRESS -> hex.getType() == HexType.MOUNTAINS;
            case MONASTERY -> hex.getType() == HexType.FOREST;
            case MINE -> hex.getType() == HexType.MOUNTAINS || hex.getType() == HexType.PLAINS;
            case WATCHTOWER -> true;
            case BRIDGE -> connectsDifferentTerrains(hex);
        };
    }

    /**
     * Проверка оптимального размещения специальной локации
     */
    private boolean isWellPlacedSpecialLocation(Hex hex, SpecialLocationType specialType) {
        return switch (specialType) {
            case PORT -> hasOceanAccess(hex);
            case FORTRESS -> isOnHighGround(hex) && hasStrategicValue(hex);
            case BRIDGE -> connectsDifferentTerrains(hex);
            case WATCHTOWER -> isOnBorder(hex) || isOnImportantRoute(hex);
            default -> true;
        };
    }

    // ========== ГЕОГРАФИЧЕСКИЕ ПРОВЕРКИ ==========

    private boolean hasOceanAccess(Hex hex) {
        return checkNeighbors(hex, neighbor -> neighbor.getType() == HexType.OCEAN);
    }

    private boolean hasDeepWaterAccess(Hex hex) {
        // Проверяем, что есть глубоководный доступ (несколько соседних океанских гексов)
        int oceanNeighbors = countNeighborsByType(hex, HexType.OCEAN);
        return oceanNeighbors >= 2;
    }

    private boolean hasNaturalHarbor(Hex hex) {
        // Проверяем форму побережья для естественной гавани
        return hasDeepWaterAccess(hex) && countNeighborsByType(hex, HexType.COAST) >= 2;
    }

    private boolean isOnHighGround(Hex hex) {
        return hex.getType() == HexType.MOUNTAINS || countSuitableNeighbors(hex) <= 3;
    }

    private boolean hasStrategicValue(Hex hex) {
        return countControlledPaths(hex) >= 2;
    }

    private boolean connectsDifferentTerrains(Hex hex) {
        Set<HexType> neighborTypes = new HashSet<>();
        HexCoordinates[] neighbors = HexUtils.getAllNeighborCoordinates(hex.getCoordinates());

        for (HexCoordinates neighborCoord : neighbors) {
            if (isValidCoordinate(neighborCoord)) {
                Hex neighbor = hexMap.getHex(neighborCoord);
                if (neighbor != null) {
                    neighborTypes.add(neighbor.getType());
                }
            }
        }

        return neighborTypes.size() >= 3; // Соединяет как минимум 3 разных типа terrain
    }

    private boolean isOnBorder(Hex hex) {
        // Проверяем, находится ли гекс near карты или near другого биома
        return hex.getCoordinates().getQ() <= 5 || hex.getCoordinates().getQ() >= hexMap.getWidth() - 5 ||
               hex.getCoordinates().getR() <= 5 || hex.getCoordinates().getR() >= hexMap.getHeight() - 5;
    }

    private boolean isOnImportantRoute(Hex hex) {
        // Упрощенная проверка - гекс находится на пути между крупными скоплениями terrain
        return countControlledPaths(hex) >= 3;
    }

    private boolean hasWaterAccess(Hex hex) {
        return checkNeighbors(hex,
                              neighbor -> neighbor.getType() == HexType.OCEAN || neighbor.getType() == HexType.COAST);
    }

    private boolean hasResourcePotential(Hex hex) {
        // Проверяем соседей на наличие гор (полезные ископаемые) или лесов (древесина)
        return checkNeighbors(hex, neighbor -> neighbor.getType() == HexType.MOUNTAINS ||
                                               neighbor.getType() == HexType.FOREST);
    }

    private int calculateCentralityBonus(Hex hex) {
        // Бонус за центральное положение в карте
        int centerQ = hexMap.getWidth() / 2;
        int centerR = hexMap.getHeight() / 2;
        int distanceToCenter = HexUtils.distance(hex.getCoordinates(), new HexCoordinates(centerQ, centerR));

        return Math.max(0, 10 - distanceToCenter / 5);
    }

    // ========== СЧЕТЧИКИ И ПРОВЕРКИ СОСЕДЕЙ ==========

    private int countSuitableNeighbors(Hex hex) {
        return countNeighbors(hex, this::isSuitableForSettlement);
    }

    private int countNeighborsByType(Hex hex, HexType type) {
        return countNeighbors(hex, neighbor -> neighbor.getType() == type);
    }

    private int countControlledPaths(Hex hex) {
        return countNeighbors(hex, neighbor -> isPassableTerrain(neighbor.getType()));
    }

    private int countNeighbors(Hex hex, java.util.function.Predicate<Hex> condition) {
        int count = 0;
        HexCoordinates[] neighbors = HexUtils.getAllNeighborCoordinates(hex.getCoordinates());

        for (HexCoordinates neighborCoord : neighbors) {
            if (isValidCoordinate(neighborCoord)) {
                Hex neighbor = hexMap.getHex(neighborCoord);
                if (neighbor != null && condition.test(neighbor)) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean checkNeighbors(Hex hex, java.util.function.Predicate<Hex> condition) {
        HexCoordinates[] neighbors = HexUtils.getAllNeighborCoordinates(hex.getCoordinates());

        for (HexCoordinates neighborCoord : neighbors) {
            if (isValidCoordinate(neighborCoord)) {
                Hex neighbor = hexMap.getHex(neighborCoord);
                if (neighbor != null && condition.test(neighbor)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPassableTerrain(HexType type) {
        return type != HexType.OCEAN && type != HexType.MOUNTAINS && type != HexType.SWAMP;
    }

    private boolean isValidCoordinate(HexCoordinates coord) {
        return coord.getQ() >= 0 && coord.getQ() < hexMap.getWidth() && coord.getR() >= 0 &&
               coord.getR() < hexMap.getHeight();
    }

    // ========== СЛУЖЕБНЫЕ МЕТОДЫ ==========

    /**
     * Резервирование территории вокруг локации
     */
    private void reserveArea(HexCoordinates center, int radius, Set<HexCoordinates> usedCoordinates) {
        for (int r = 0; r < hexMap.getHeight(); r++) {
            for (int q = 0; q < hexMap.getWidth(); q++) {
                Hex hex = hexMap.getHex(q, r);
                if (hex != null) {
                    int distance = HexUtils.distance(center, hex.getCoordinates());
                    if (distance <= radius) {
                        usedCoordinates.add(hex.getCoordinates());
                    }
                }
            }
        }
    }

    /**
     * Создание сущности локации
     */
    private Entity createLocationEntity(Hex hex, String locationType, String locationName, int locationSize) {
        return entityFactory.createLocation(hex, locationType, locationName, locationSize);
    }

    /**
     * Получение имени поселения
     */
    private String getSettlementName(String type) {
        String baseName = switch (type) {
            case "VILLAGE" -> nameService.getRandomName("villages");
            case "TOWN" -> nameService.getRandomName("towns");
            case "OUTPOST" -> nameService.getRandomName("outposts");
            default -> "Поселение";
        };

        // Добавляем географический суффикс для разнообразия
        String[] suffixes = {"Усть-", "Верхне-", "Нижне-", "Ново-", "Старо-", "Бело-", "Красно-"};
        if (random.nextBoolean() && !baseName.startsWith("Застава")) {
            baseName = suffixes[random.nextInt(suffixes.length)] + baseName.toLowerCase();
        }

        return baseName;
    }

    /**
     * Создание специальной локации
     */
    private SpecialLocation createSpecialLocation(SpecialLocationType specialType, Hex hex) {
        String name = switch (specialType) {
            case PORT -> nameService.getRandomName("ports");
            case FORTRESS -> nameService.getRandomName("fortresses");
            case MONASTERY -> nameService.getRandomName("monasteries");
            case MINE -> nameService.getRandomName("mines");
            case WATCHTOWER -> nameService.getRandomName("watchtowers");
            case BRIDGE -> nameService.getRandomName("bridges");
        };

        return new SpecialLocation(specialType.name(), name, getSizeForSpecialType(specialType));
    }

    private int getSizeForSpecialType(SpecialLocationType type) {
        return switch (type) {
            case PORT, FORTRESS -> 2;
            case MONASTERY, MINE, WATCHTOWER, BRIDGE -> 1;
        };
    }

    /**
     * Логирование распределения локаций
     */
    private void logLocationDistribution(List<Entity> locations) {
        Map<String, Long> distribution = locations.stream().collect(
            Collectors.groupingBy(this::getLocationType,
                                  Collectors.counting()));

        // Дополнительная информация о позициях
        Map<String, List<String>> locationDetails = locations.stream().collect(
            Collectors.groupingBy(this::getLocationType,
                                  Collectors.mapping(this::getLocationInfo, Collectors.toList())));

        com.badlogic.gdx.Gdx.app.log("LocationGeneration",
                                     "Total locations: " + locations.size() +
                                     ", Distribution: " + distribution);

        // Детальная информация по типам
        for (Map.Entry<String, List<String>> entry : locationDetails.entrySet()) {
            com.badlogic.gdx.Gdx.app.log("LocationGeneration",
                                         entry.getKey() + " details: " + entry.getValue());
        }
    }

    /**
     * Получение детальной информации о локации
     */
    private String getLocationInfo(Entity entity) {
        GlobalPositionComponent position = entity.getComponent(GlobalPositionComponent.class);
        LocationComponent location = entity.getComponent(LocationComponent.class);

        if (position != null && location != null) {
            return String.format("(%d,%d)-%s",
                                 position.getCoordinates().getQ(),
                                 position.getCoordinates().getR(),
                                 location.getLocationData().getName() != null ? location.getLocationData().getName()
                                     : "NoName");
        } else if (position != null) {
            return String.format("(%d,%d)",
                                 position.getCoordinates().getQ(),
                                 position.getCoordinates().getR());
        }

        return "NoPositionInfo";
    }

    private String getLocationType(Entity entity) {
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);
        if (locationComponent != null) {
            return locationComponent.getLocationData().getType();
        }
        return "UNKNOWN";
    }


    /**
     * Проверка естественной защиты (скалы, вода, ущелья)
     */
    private boolean hasNaturalProtection(Hex hex) {
        int protectiveNeighbors = countNeighbors(hex, neighbor -> neighbor.getType() == HexType.MOUNTAINS ||
                                                                  neighbor.getType() == HexType.OCEAN);
        return protectiveNeighbors >= 3;
    }

    /**
     * Проверка доступа к пресной воде (реки, озера)
     */
    private boolean hasFreshWaterAccess(Hex hex) {
        // Предполагаем, что пресная вода рядом с лесами и равнинами у воды
        return checkNeighbors(hex, neighbor -> neighbor.getType() == HexType.COAST ||
                                               (neighbor.getType() == HexType.PLAINS && hasWaterAccess(neighbor)) ||
                                               neighbor.getType() == HexType.FOREST);
    }

    /**
     * Проверка живописного окружения (разнообразие ландшафта)
     */
    private boolean hasScenicViews(Hex hex) {
        return countDifferentTerrainTypes(hex) >= 4;
    }

    /**
     * Проверка близости к цивилизации (города, поселения)
     */
    private boolean isNearCivilization(Hex hex) {
        // Упрощенная проверка - ищем города в радиусе 10 гексов
        int searchRadius = 10;
        HexCoordinates center = hex.getCoordinates();

        for (int r = Math.max(0, center.getR() - searchRadius);
             r <= Math.min(hexMap.getHeight() - 1, center.getR() + searchRadius); r++) {
            for (int q = Math.max(0, center.getQ() - searchRadius);
                 q <= Math.min(hexMap.getWidth() - 1, center.getQ() + searchRadius); q++) {
                Hex nearbyHex = hexMap.getHex(q, r);
                if (nearbyHex != null && isSuitableForCity(nearbyHex)) {
                    int distance = HexUtils.distance(center, nearbyHex.getCoordinates());
                    if (distance <= searchRadius) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Проверка транспортной доступности
     */
    private boolean hasGoodTransportAccess(Hex hex) {
        int passableNeighbors = countNeighbors(hex, type -> isPassableTerrain(type.getType()));
        return passableNeighbors >= 4;
    }

    /**
     * Проверка близости к существующим поселениям
     */
    private boolean isNearExistingSettlements(Hex hex) {
        // Аналогично isNearCivilization, но с меньшим радиусом
        int searchRadius = 6;
        HexCoordinates center = hex.getCoordinates();

        for (int r = Math.max(0, center.getR() - searchRadius);
             r <= Math.min(hexMap.getHeight() - 1, center.getR() + searchRadius); r++) {
            for (int q = Math.max(0, center.getQ() - searchRadius);
                 q <= Math.min(hexMap.getWidth() - 1, center.getQ() + searchRadius); q++) {
                Hex nearbyHex = hexMap.getHex(q, r);
                if (nearbyHex != null && isSuitableForSettlement(nearbyHex)) {
                    int distance = HexUtils.distance(center, nearbyHex.getCoordinates());
                    if (distance <= searchRadius) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Подсчет количества разных типов terrain среди соседей
     */
    private int countDifferentTerrainTypes(Hex hex) {
        Set<HexType> terrainTypes = new HashSet<>();
        HexCoordinates[] neighbors = HexUtils.getAllNeighborCoordinates(hex.getCoordinates());

        for (HexCoordinates neighborCoord : neighbors) {
            if (isValidCoordinate(neighborCoord)) {
                Hex neighbor = hexMap.getHex(neighborCoord);
                if (neighbor != null) {
                    terrainTypes.add(neighbor.getType());
                }
            }
        }

        return terrainTypes.size();
    }

    /**
     * Проверка удаленности места
     */
    private boolean isTooRemote(Hex hex) {
        int passableNeighbors = countNeighbors(hex, type -> isPassableTerrain(type.getType()));
        return passableNeighbors <= 2;
    }

    /**
     * Проверка наличия естественного укрытия
     */
    private boolean hasNaturalShelter(Hex hex) {
        return checkNeighbors(hex, neighbor -> neighbor.getType() == HexType.MOUNTAINS) ||
               hex.getType() == HexType.MOUNTAINS;
    }

    /**
     * Расчет видимости с башни
     */
    private int calculateVisibility(Hex hex) {
        int visibleHexes = 0;
        int viewDistance = 8; // Дальность обзора

        HexCoordinates center = hex.getCoordinates();

        for (int r = Math.max(0, center.getR() - viewDistance);
             r <= Math.min(hexMap.getHeight() - 1, center.getR() + viewDistance); r++) {
            for (int q = Math.max(0, center.getQ() - viewDistance);
                 q <= Math.min(hexMap.getWidth() - 1, center.getQ() + viewDistance); q++) {
                Hex targetHex = hexMap.getHex(q, r);
                if (targetHex != null && hasLineOfSight(hex, targetHex)) {
                    visibleHexes++;
                }
            }
        }

        return visibleHexes;
    }

    /**
     * Упрощенная проверка прямой видимости
     */
    private boolean hasLineOfSight(Hex from, Hex to) {
        // Упрощенная реализация - считаем, что видимость есть если нет препятствий
        // TODO Можно учитывать высоту и препятствия
        int distance = HexUtils.distance(from.getCoordinates(), to.getCoordinates());
        return distance <= 5 || (distance <= 8 && isOnHighGround(from));
    }

    /**
     * Проверка близости к стратегическим объектам
     */
    private boolean isNearStrategicLocations(Hex hex) {
        return isNearCivilization(hex) || controlsTradeRoute(hex);
    }

    /**
     * Проверка труднодоступности
     */
    private boolean isTooInaccessible(Hex hex) {
        int passableNeighbors = countNeighbors(hex, type -> isPassableTerrain(type.getType()));
        return passableNeighbors <= 1;
    }

    /**
     * Проверка линии видимости с другими башнями
     */
    private boolean hasLineOfSightToOtherTowers(Hex hex) {
        // В реальной реализации нужно проверять существующие башни
        // Здесь упрощенная версия - проверяем наличие высоких точек в радиусе
        int searchRadius = 12;
        HexCoordinates center = hex.getCoordinates();

        for (int r = Math.max(0, center.getR() - searchRadius);
             r <= Math.min(hexMap.getHeight() - 1, center.getR() + searchRadius); r++) {
            for (int q = Math.max(0, center.getQ() - searchRadius);
                 q <= Math.min(hexMap.getWidth() - 1, center.getQ() + searchRadius); q++) {
                Hex nearbyHex = hexMap.getHex(q, r);
                if (nearbyHex != null && nearbyHex != hex && isOnHighGround(nearbyHex)) {
                    int distance = HexUtils.distance(center, nearbyHex.getCoordinates());
                    if (distance <= searchRadius && hasLineOfSight(hex, nearbyHex)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Проверка узкого прохода (идеально для моста)
     */
    private boolean isNarrowPassage(Hex hex) {
        // Узкий проход - когда гекс соединяет только 2 проходимых направления
        int passableNeighbors = countNeighbors(hex, type -> isPassableTerrain(type.getType()));
        return passableNeighbors == 2;
    }

    /**
     * Проверка контроля торгового пути
     */
    private boolean controlsTradeRoute(Hex hex) {
        // Торговый путь - когда гекс находится на прямой между двумя крупными скоплениями цивилизации
        return isOnBorderBetweenRegions(hex) || isOnStraightLine(hex);
    }

    /**
     * Проверка наличия естественных опор
     */
    private boolean hasNaturalFoundations(Hex hex) {
        return checkNeighbors(hex, neighbor -> neighbor.getType() == HexType.MOUNTAINS) &&
               countNeighborsByType(hex, HexType.MOUNTAINS) >= 2;
    }

    /**
     * Проверка стабильности берегов
     */
    private boolean hasStableBanks(Hex hex) {
        return !checkNeighbors(hex, neighbor -> neighbor.getType() == HexType.SWAMP) && hex.getType() != HexType.SWAMP;
    }

    /**
     * Проверка прямого пересечения
     */
    private boolean isStraightCrossing(Hex hex) {
        HexCoordinates[] opposites = getOppositeNeighbors(hex.getCoordinates());

        // Need at least one pair of opposites to check
        if (opposites.length < 2) {
            return false;
        }

        // Check each pair of opposite neighbors
        for (int i = 0; i < opposites.length; i += 2) {
            if (i + 1 < opposites.length) {
                Hex neighbor1 = hexMap.getHex(opposites[i]);
                Hex neighbor2 = hexMap.getHex(opposites[i + 1]);
                if (neighbor1 != null && neighbor2 != null && neighbor1.getType() == neighbor2.getType()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Проверка связи важных регионов
     */
    private boolean connectsImportantRegions(Hex hex) {
        // Важные регионы - большие скопления подходящей для поселений местности
        return connectsLargePlains(hex) || connectsForestToPlains(hex);
    }

    /**
     * Проверка слишком широкого перехода
     */
    private boolean isTooWide(Hex hex) {
        int sameTypeNeighbors = countNeighborsByType(hex, hex.getType());
        return sameTypeNeighbors >= 5;
    }

    /**
     * Проверка нестабильного основания
     */
    private boolean hasUnstableFoundation(Hex hex) {
        return hex.getType() == HexType.SWAMP || checkNeighbors(hex, neighbor -> neighbor.getType() == HexType.SWAMP);
    }

    /**
     * Получение противоположных соседей
     */
    private HexCoordinates[] getOppositeNeighbors(HexCoordinates center) {
        HexCoordinates[] allNeighbors = HexUtils.getAllNeighborCoordinates(center);
        List<HexCoordinates> validOpposites = new ArrayList<>();

        // В гексагональной сетке у каждого гекса есть 3 пары противоположных соседей
        int[][] oppositePairs = {{0, 3}, {1, 4}, {2, 5}};

        for (int[] pair : oppositePairs) {
            if (pair[0] < allNeighbors.length && pair[1] < allNeighbors.length) {
                HexCoordinates coord1 = allNeighbors[pair[0]];
                HexCoordinates coord2 = allNeighbors[pair[1]];

                // Only include valid coordinates
                if (isValidCoordinate(coord1) && isValidCoordinate(coord2)) {
                    validOpposites.add(coord1);
                    validOpposites.add(coord2);
                }
            }
        }

        return validOpposites.toArray(new HexCoordinates[0]);
    }

    /**
     * Проверка нахождения на границе регионов
     */
    private boolean isOnBorderBetweenRegions(Hex hex) {
        return countDifferentTerrainTypes(hex) >= 3;
    }

    /**
     * Проверка нахождения на прямой линии
     */
    private boolean isOnStraightLine(Hex hex) {
        // Упрощенная проверка - гекс имеет двух соседей одинакового типа в противоположных направлениях
        HexCoordinates[] opposites = getOppositeNeighbors(hex.getCoordinates());
        for (int i = 0; i < opposites.length; i += 2) {
            Hex neighbor1 = hexMap.getHex(opposites[i]);
            Hex neighbor2 = hexMap.getHex(opposites[i + 1]);
            if (neighbor1 != null && neighbor2 != null && neighbor1.getType() == neighbor2.getType() &&
                isSuitableForSettlement(neighbor1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка связи больших равнин
     */
    private boolean connectsLargePlains(Hex hex) {
        return countNeighborsByType(hex, HexType.PLAINS) >= 2 && hasLargePlainAreaOnBothSides(hex);
    }

    /**
     * Проверка связи леса и равнин
     */
    private boolean connectsForestToPlains(Hex hex) {
        return countNeighborsByType(hex, HexType.FOREST) >= 1 && countNeighborsByType(hex, HexType.PLAINS) >= 1;
    }

    /**
     * Проверка наличия больших равнинных территорий по обе стороны
     */
    private boolean hasLargePlainAreaOnBothSides(Hex hex) {
        // Упрощенная проверка - ищем скопления равнин в разных направлениях
        int plainsInDirection1 = countPlainsInDirection(hex, 0, 2); // Север
        int plainsInDirection2 = countPlainsInDirection(hex, 3, 2); // Юг
        return plainsInDirection1 >= 2 && plainsInDirection2 >= 2;
    }

    /**
     * Подсчет равнин в направлении
     */
    private int countPlainsInDirection(Hex hex, int startDirection, int steps) {
        int count = 0;
        HexCoordinates current = hex.getCoordinates();

        for (int i = 0; i < steps; i++) {
            HexCoordinates[] neighbors = HexUtils.getAllNeighborCoordinates(current);
            if (startDirection < neighbors.length) {
                HexCoordinates neighborCoord = neighbors[startDirection];

                if (!isValidCoordinate(neighborCoord)) {
                    break;
                }
                Hex neighbor = hexMap.getHex(neighborCoord);
                if (neighbor != null && neighbor.getType() == HexType.PLAINS) {
                    count++;
                    current = neighborCoord;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return count;
    }

    private enum SpecialLocationType {
        PORT, FORTRESS, MONASTERY, MINE, WATCHTOWER, BRIDGE
    }

    private static class Zone {

        final int startQ, endQ, startR, endR;

        Zone(int startQ, int endQ, int startR, int endR) {
            this.startQ = startQ;
            this.endQ = endQ;
            this.startR = startR;
            this.endR = endR;
        }
    }

    private record SpecialLocation(String type, String name, int size) {

    }
}
