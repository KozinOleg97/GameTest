package io.github.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import io.github.game.ecs.components.PositionComponent;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.ecs.components.tags.PlayerComponent;
import java.text.MessageFormat;

public class PlayerMovementSystem extends IteratingSystem {


    public PlayerMovementSystem() {
        super(Family.all(
            PlayerComponent.class,
            VelocityComponent.class,
            PositionComponent.class
        ).get());

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = PositionComponent.MAPPER.get(entity);
        VelocityComponent velocity = VelocityComponent.MAPPER.get(entity);

        // Обновляем позицию на основе скорости
        position.getCoordinates().x += velocity.getVelocityX() * deltaTime;
        position.getCoordinates().y += velocity.getVelocityY() * deltaTime;

        Gdx.app.log("MOVEMENT",
            MessageFormat.format("Player position: {0}, {1}", position.getCoordinates().x,
                position.getCoordinates().y));
    }
}
