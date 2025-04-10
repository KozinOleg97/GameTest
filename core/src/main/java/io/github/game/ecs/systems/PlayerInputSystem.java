package io.github.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.ecs.components.PlayerComponent;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.services.InputService;
import io.github.game.utils.GameSettings;
import javax.inject.Inject;

public class PlayerInputSystem extends IteratingSystem {
    private final InputService inputService;
    private final GameSettings gameSettings;

    @Inject
    public PlayerInputSystem(InputService inputService, GameSettings gameSettings) {
        super(Family.all(
            PlayerComponent.class,
            VelocityComponent.class
        ).get());
        this.inputService = inputService;
        this.gameSettings = gameSettings;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = VelocityComponent.MAPPER.get(entity);
        velocity.setVelocityX(inputService.getHorizontalAxis() * gameSettings.getSpeedMultiplier());
        velocity.setVelocityY(inputService.getVerticalAxis() * gameSettings.getSpeedMultiplier());
    }
}
