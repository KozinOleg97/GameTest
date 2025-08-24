package io.github.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.ecs.components.tags.NPCComponent;
import io.github.game.settings.GameplaySettings;
import java.util.Random;
import javax.inject.Inject;

public class NPCLogicSystem extends IteratingSystem {

    private final Random random = new Random();
    private final GameplaySettings gameplaySettings;

    @Inject
    public NPCLogicSystem(GameplaySettings gameplaySettings) {
        super(Family.all(NPCComponent.class, VelocityComponent.class).get());
        this.gameplaySettings = gameplaySettings;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);

        if (random.nextFloat() < 0.05) {
            velocity.setVelocityX(
                (random.nextFloat() * 2 - 1) * gameplaySettings.getSpeedMultiplier());
            velocity.setVelocityY(
                (random.nextFloat() * 2 - 1) * gameplaySettings.getSpeedMultiplier());
        }
    }
}
