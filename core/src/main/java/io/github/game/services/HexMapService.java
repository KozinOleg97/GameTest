package io.github.game.services;

import com.badlogic.ashley.core.Entity;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexCoordinates;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Сервис для доступа к данным гексов через HexMap. Обеспечивает синхронизацию между ECS-сущностями
 * и данными в HexMap. Является единой точкой доступа к данным гексов для всех систем.
 */
@Singleton
public class HexMapService {

    private final HexMap hexMap;
    private final Map<HexCoordinates, Entity> entityMap = new HashMap<>();

    @Inject
    public HexMapService(HexMap hexMap) {
        this.hexMap = hexMap;
    }

    /**
     * Регистрирует сущность гекса в сервисе
     */
    public void registerHexEntity(HexCoordinates coordinates, Entity entity) {
        entityMap.put(coordinates, entity);
    }

    /**
     * Удаляет регистрацию сущности гекса
     */
    public void unregisterHexEntity(HexCoordinates coordinates) {
        entityMap.remove(coordinates);
    }

    /**
     * Получает данные гекса по координатам
     */
    public Hex getHex(HexCoordinates coordinates) {
        return hexMap.getHex(coordinates);
    }

    /**
     * Находит ECS-сущность по координатам гекса
     */
    public Optional<Entity> findEntityByCoordinates(HexCoordinates coordinates) {
        return Optional.ofNullable(entityMap.get(coordinates));
    }

    /**
     * Проверяет, существует ли сущность для гекса с указанными координатами
     */
    public boolean hasEntityForCoordinates(HexCoordinates coordinates) {
        return entityMap.containsKey(coordinates);
    }

    /**
     * Обновляет данные гекса в HexMap
     */
    public void updateHex(Hex hex) {
        hexMap.addHex(hex);
    }
}
