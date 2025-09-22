package io.github.game.services;

import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexCoordinates;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Сервис для доступа к данным гексов через HexMap. Обеспечивает синхронизацию между ECS-сущностями
 * и данными в HexMap. Является единой точкой доступа к данным гексов для всех систем.
 */
@Singleton
public class HexMapService {

    private final HexMap hexMap;

    @Inject
    public HexMapService(HexMap hexMap) {
        this.hexMap = hexMap;
    }


    /**
     * Получает данные гекса по координатам
     */
    public Hex getHex(HexCoordinates coordinates) {
        return hexMap.getHex(coordinates);
    }


}
