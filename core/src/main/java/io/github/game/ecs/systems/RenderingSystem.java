package io.github.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.game.ecs.components.PositionComponent;
import io.github.game.ecs.components.RenderComponent;

public class RenderingSystem extends IteratingSystem {

    private final ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(
        PositionComponent.class);
    private final ComponentMapper<RenderComponent> renderMapper = ComponentMapper.getFor(
        RenderComponent.class);

    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    public RenderingSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(PositionComponent.class, RenderComponent.class).get());
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
//        MemoryUtils.logMemoryUsage("RenderingSystem before update");

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        try {
            super.update(deltaTime);
        } finally {
            batch.end();
        }

//        MemoryUtils.logMemoryUsage("RenderingSystem after update");

        // Логируем для отладки
        if (Gdx.graphics.getFrameId() % 60 == 0) {
            Gdx.app.debug("RenderingSystem", "Rendered entities: " + getEntities().size());
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positionMapper.get(entity);
        RenderComponent render = renderMapper.get(entity);

        // Проверяем видимость перед рендерингом
        if (isInViewport(position, render)) {
            render.getSprite()
                  .setPosition(position.getCoordinates().x, position.getCoordinates().y);

            render.getSprite().draw(batch);
        }
    }

    private boolean isInViewport(PositionComponent position, RenderComponent render) {
        float spriteWidth = render.getSprite().getWidth();
        float spriteHeight = render.getSprite().getHeight();

        float cameraLeft = camera.position.x - camera.viewportWidth / 2 * camera.zoom;
        float cameraRight = camera.position.x + camera.viewportWidth / 2 * camera.zoom;
        float cameraBottom = camera.position.y - camera.viewportHeight / 2 * camera.zoom;
        float cameraTop = camera.position.y + camera.viewportHeight / 2 * camera.zoom;

        float entityLeft = position.getCoordinates().x;
        float entityRight = position.getCoordinates().x + spriteWidth;
        float entityBottom = position.getCoordinates().y;
        float entityTop = position.getCoordinates().y + spriteHeight;

        //        if (Gdx.graphics.getFrameId() % 60 == 0) {
//            Gdx.app.log("RenderingSystem", "Camera at: (" + camera.position.x + "," + camera.position.y +
//                "), viewport: " + camera.viewportWidth + "x" + camera.viewportHeight + ", zoom: " + camera.zoom);
//            Gdx.app.log("RenderingSystem", "Entity at: " + position.getCoordinates() +
//                " is " + (inViewport ? "VISIBLE" : "HIDDEN"));
//        }

        return entityRight >= cameraLeft &&
               entityLeft <= cameraRight &&
               entityTop >= cameraBottom &&
               entityBottom <= cameraTop;
    }
}
