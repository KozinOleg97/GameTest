package io.github.game.ecs.systems.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.core.world.hex.Hex;
import io.github.game.ecs.components.world.HexComponent;
import io.github.game.services.HexMapService;
import javax.inject.Inject;

/**
 * Система, отвечающая за симуляцию игрового мира. Обновляет состояние гексов, регионов и глобальные
 * процессы. Выполняется на каждом шаге игрового цикла.
 */
public class HexSimulationSystem extends IteratingSystem {

    private final HexMapService hexMapService;

    @Inject
    public HexSimulationSystem(HexMapService hexMapService) {
        super(Family.all(HexComponent.class).get());
        this.hexMapService = hexMapService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HexComponent hexComp = entity.getComponent(HexComponent.class);

        // Здесь будет логика обновления конкретного гекса
        // Например: рост ресурсов, распространение влияния фракций и т.д.
        simulateHex(hexMapService.getHex(hexComp.getCoordinates()).get(), deltaTime);
    }

    private void simulateHex(Hex hex, float deltaTime) {

        // Заготовка для логики в зависимости от типа гекса

    }

}
