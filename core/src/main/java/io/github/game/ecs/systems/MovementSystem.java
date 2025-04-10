package io.github.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.ecs.components.PositionComponent;
import javax.inject.Inject;


public class MovementSystem extends IteratingSystem {


    @Inject
    public MovementSystem() {
        super(Family.all(PositionComponent.class).get());            }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        // Логика движения...
    }
}
