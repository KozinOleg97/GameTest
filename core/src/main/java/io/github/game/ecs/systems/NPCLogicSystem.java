package io.github.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.ecs.components.tags.NPCComponent;
import io.github.game.utils.GameSettings;
import java.util.Random;
import javax.inject.Inject;

public class NPCLogicSystem extends IteratingSystem {

    private final Random random = new Random();
    private final GameSettings gameSettings;

    @Inject
    public NPCLogicSystem(GameSettings gameSettings) {
        super(Family.all(NPCComponent.class, VelocityComponent.class).get());
        this.gameSettings = gameSettings;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);

        if (random.nextFloat() < 0.05) {
            velocity.setVelocityX((random.nextFloat() * 2 - 1) * gameSettings.getSpeedMultiplier());
            velocity.setVelocityY((random.nextFloat() * 2 - 1) * gameSettings.getSpeedMultiplier());
        }
    }
}
