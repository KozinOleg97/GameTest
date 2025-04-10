package io.github.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.game.ecs.components.PlayerComponent;
import io.github.game.ecs.components.PositionComponent;
import io.github.game.ecs.components.RenderComponent;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.utils.ResourceManager;
import javax.inject.Inject;


public class EntityFactory {
    private final PooledEngine engine;
    private final ResourceManager resourceManager;

    @Inject
    public EntityFactory(PooledEngine engine, ResourceManager resourceManager) {
        this.engine = engine;
        this.resourceManager = resourceManager;
    }

    public Entity createPlayer(float x, float y) {
        Entity player = engine.createEntity();

        // Помечаем как Игрока
        player.add(new PlayerComponent());

        // Позиция
        player.add(new PositionComponent(x, y));

        // Скорость
        player.add(new VelocityComponent());

        // Спрайт
        Texture playerTexture = resourceManager.getTexture("textures/ball.png");
        Sprite playerSprite = new Sprite(playerTexture);
        player.add(new RenderComponent(playerSprite));

        engine.addEntity(player);
        return player;
    }
}
