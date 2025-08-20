package io.github.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.game.ecs.components.PositionComponent;
import io.github.game.ecs.components.VelocityComponent;
import javax.inject.Inject;

public class MovementSystem extends IteratingSystem {

    private static final ComponentMapper<PositionComponent> POSITION = ComponentMapper.getFor(
        PositionComponent.class);
    private static final ComponentMapper<VelocityComponent> VELOCITY = ComponentMapper.getFor(
        VelocityComponent.class);

    @Inject
    public MovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = POSITION.get(entity);
        VelocityComponent velocity = VELOCITY.get(entity);

        position.getCoordinates().x += velocity.getVelocityX() * deltaTime;
        position.getCoordinates().y += velocity.getVelocityY() * deltaTime;
    }
}
