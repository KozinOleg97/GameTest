package io.github.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.ecs.components.PositionComponent;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.ecs.components.tags.NPCComponent;
import io.github.game.utils.GameSettings;
import java.util.Random;
import javax.inject.Inject;

public class NPCMovementSystem extends IteratingSystem {

    private final Random random = new Random();
    private final GameSettings gameSettings;

    @Inject
    public NPCMovementSystem(GameSettings gameSettings) {
        super(
            Family.all(NPCComponent.class, PositionComponent.class, VelocityComponent.class).get());
        this.gameSettings = gameSettings;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = entity.getComponent(PositionComponent.class);
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);

        // Randomly change direction
        if (random.nextFloat() < 0.05) { // 5% chance to change direction every frame
            velocity.setVelocityX((random.nextFloat() * 2 - 1)  // Random value between -1 and 1
                * gameSettings.getSpeedMultiplier());
            velocity.setVelocityY((random.nextFloat() * 2 - 1)  // Random value between -1 and 1
                * gameSettings.getSpeedMultiplier());
        }

        // Update position based on velocity
        position.getCoordinates().x += velocity.getVelocityX() * deltaTime;
        position.getCoordinates().y += velocity.getVelocityY() * deltaTime;
    }
}
