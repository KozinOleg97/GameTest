package io.github.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexCoordinates;
import io.github.game.ecs.components.PositionComponent;
import io.github.game.ecs.components.RenderComponent;
import io.github.game.ecs.components.VelocityComponent;
import io.github.game.ecs.components.tags.NPCComponent;
import io.github.game.ecs.components.tags.PlayerComponent;
import io.github.game.ecs.components.world.GlobalPositionComponent;
import io.github.game.ecs.components.world.LocationComponent;
import io.github.game.ecs.components.world.LocationData;
import io.github.game.utils.ResourceManager;
import java.util.UUID;
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

    public Entity createLocation(Hex hex, String type, String name, int size) {

        Gdx.app.log("EntityFactory", "Creating location at  hex position: " + hex.getCoordinates().getR() + ", " +
                                     hex.getCoordinates().getQ());

        Entity location = engine.createEntity();

        LocationComponent locationComponent = engine.createComponent(LocationComponent.class);
        locationComponent.setLocationId(UUID.randomUUID());
        locationComponent.setLocationData(new LocationData(name, (short) size, type));
        location.add(locationComponent);

        GlobalPositionComponent globalPositionComponent = engine.createComponent(GlobalPositionComponent.class);
        globalPositionComponent.setCoordinates(hex.getCoordinates());
        location.add(globalPositionComponent);

        // Создаем и настраиваем спрайт
        Texture playerTexture = resourceManager.getTexture("textures/ball.png");
        if (playerTexture == null) {
            throw new RuntimeException("Texture not found: textures/ball.png");
        }

        Sprite playerSprite = new Sprite(playerTexture);
        playerSprite.setScale(0.5f * size);

        RenderComponent render = engine.createComponent(RenderComponent.class);
        render.setSprite(playerSprite);
        render.setTitle(locationComponent.getLocationData().getName());
        location.add(render);

        Gdx.app.log("EntityFactory", "Location entity created successfully");

        hex.setLocationId(locationComponent.getLocationId());

        return location;
    }

    public Entity createLocation(HexCoordinates coordinates) {
        Gdx.app.log("EntityFactory",
                    "Creating location at  hex position: " + coordinates.getR() + ", " + coordinates.getQ());
        Entity location = engine.createEntity();

        LocationComponent locationComponent = engine.createComponent(LocationComponent.class);
        locationComponent.setLocationId(UUID.randomUUID());
        location.add(locationComponent);

        GlobalPositionComponent globalPositionComponent = engine.createComponent(GlobalPositionComponent.class);
        globalPositionComponent.setCoordinates(coordinates);
        location.add(globalPositionComponent);

        // Создаем и настраиваем спрайт
        Texture playerTexture = resourceManager.getTexture("textures/ball.png");
        if (playerTexture == null) {
            throw new RuntimeException("Texture not found: textures/ball.png");
        }

        Sprite playerSprite = new Sprite(playerTexture);
        playerSprite.setScale(0.5f);

        RenderComponent render = engine.createComponent(RenderComponent.class);
        render.setSprite(playerSprite);
        location.add(render);

        Gdx.app.log("EntityFactory", "Location entity created successfully");

        return location;
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

}
