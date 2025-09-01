package io.github.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.game.core.world.hex.Hex;
import io.github.game.ecs.components.PositionComponent;
import io.github.game.ecs.components.RenderComponent;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.ecs.components.tags.NPCComponent;
import io.github.game.ecs.components.tags.PlayerComponent;
import io.github.game.ecs.components.world.HexComponent;
import io.github.game.utils.ResourceManager;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EntityFactory {

    private final PooledEngine engine;
    private final ResourceManager resourceManager;

    @Inject
    public EntityFactory(PooledEngine engine, ResourceManager resourceManager) {
        this.engine = engine;
        this.resourceManager = resourceManager;
    }

    public Entity createPlayer(float x, float y) {
        Gdx.app.log("EntityFactory", "Creating player at position: " + x + ", " + y);
        Entity player = engine.createEntity();

        // Добавляем компоненты
        player.add(engine.createComponent(PlayerComponent.class));

        PositionComponent position = engine.createComponent(PositionComponent.class);
        position.getCoordinates().set(x, y);
        player.add(position);

        player.add(engine.createComponent(VelocityComponent.class));

        // Создаем и настраиваем спрайт
        Texture playerTexture = resourceManager.getTexture("textures/ball.png");
        if (playerTexture == null) {
            throw new RuntimeException("Texture not found: textures/ball.png");
        }

        Sprite playerSprite = new Sprite(playerTexture);

        RenderComponent render = engine.createComponent(RenderComponent.class);
        render.setSprite(playerSprite);
        player.add(render);

        Gdx.app.log("EntityFactory", "Player entity created successfully");
        return player;
    }

    public Entity createNPC(float x, float y) {
        Gdx.app.log("EntityFactory", "Creating NPC at position: " + x + ", " + y);
        Entity npc = engine.createEntity();

        npc.add(engine.createComponent(NPCComponent.class));

        PositionComponent position = engine.createComponent(PositionComponent.class);
        position.getCoordinates().set(x, y);
        npc.add(position);

        npc.add(engine.createComponent(VelocityComponent.class));

        // Создаем и настраиваем спрайт
        Texture npcTexture = resourceManager.getTexture("textures/red_ball.png");
        if (npcTexture == null) {
            throw new RuntimeException("Texture not found: textures/red_ball.png");
        }

        Sprite npcSprite = new Sprite(npcTexture);

        RenderComponent render = engine.createComponent(RenderComponent.class);
        render.setSprite(npcSprite);
        npc.add(render);

        Gdx.app.log("EntityFactory", "NPC entity created successfully");
        return npc;
    }

    public Entity createHexEntity(Hex hex) {
        Entity entity = engine.createEntity();

        HexComponent hexComp = engine.createComponent(HexComponent.class);
        hexComp.setCoordinates(hex.getCoordinates());
        entity.add(hexComp);

        return entity;
    }

}
