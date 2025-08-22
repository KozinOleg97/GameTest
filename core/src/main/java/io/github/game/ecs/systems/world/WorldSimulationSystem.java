package io.github.game.ecs.systems.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexType;
import io.github.game.ecs.components.world.HexComponent;
import javax.inject.Inject;

/**
 * Система, отвечающая за симуляцию игрового мира. Обновляет состояние гексов, регионов и глобальные
 * процессы. Выполняется на каждом шаге игрового цикла.
 */
public class WorldSimulationSystem extends IteratingSystem {

    @Inject
    public WorldSimulationSystem() {
        super(Family.all(HexComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HexComponent hexComp = entity.getComponent(HexComponent.class);

        // Здесь будет логика обновления конкретного гекса
        // Например: рост ресурсов, распространение влияния фракций и т.д.
        simulateHex(hexComp.getHex(), deltaTime);
    }

    private void simulateHex(Hex hex, float deltaTime) {

        // Заготовка для логики в зависимости от типа гекса

        // Например, для леса можно увеличивать количество древесины:
        if (hex.getType() == HexType.FOREST) {
            // hex.setWoodAmount(hex.getWoodAmount() + deltaTime * GROWTH_RATE);
        }
    }
}
