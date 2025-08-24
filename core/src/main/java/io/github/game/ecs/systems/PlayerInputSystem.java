package io.github.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.ecs.components.tags.PlayerComponent;
import io.github.game.services.InputService;
import io.github.game.settings.GameplaySettings;
import javax.inject.Inject;

public class PlayerInputSystem extends IteratingSystem {

    private static final ComponentMapper<VelocityComponent> VELOCITY = ComponentMapper.getFor(
        VelocityComponent.class);

    private final InputService inputService;
    private final GameplaySettings gameplaySettings;

    @Inject
    public PlayerInputSystem(InputService inputService, GameplaySettings gameplaySettings
    ) {
        super(Family.all(
            PlayerComponent.class,
            VelocityComponent.class
        ).get());
        this.inputService = inputService;
        this.gameplaySettings = gameplaySettings;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = VELOCITY.get(entity);
        velocity.setVelocityX(
            inputService.getHorizontalAxis() * gameplaySettings.getSpeedMultiplier());
        velocity.setVelocityY(
            inputService.getVerticalAxis() * gameplaySettings.getSpeedMultiplier());
    }
}
