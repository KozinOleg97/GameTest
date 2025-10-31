package io.github.game.ecs.systems.world;


import static io.github.game.core.world.hex.HexUtils.HEX_SIZE;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.game.core.world.hex.HexCoordinates;
import io.github.game.ecs.components.RenderComponent;
import io.github.game.ecs.components.world.GlobalPositionComponent;


public class LocationRenderSystem extends IteratingSystem {


    private final ComponentMapper<GlobalPositionComponent> globalPositionMapper = ComponentMapper.getFor(
        GlobalPositionComponent.class);
    private final ComponentMapper<RenderComponent> renderMapper = ComponentMapper.getFor(
        RenderComponent.class);


    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    // Переиспользуемые объекты
    private final Vector2 tempVector = new Vector2();
    private final Rectangle cameraBounds = new Rectangle();
    private float cameraZoom;

    public LocationRenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(GlobalPositionComponent.class, RenderComponent.class).get());
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        updateCameraBounds();

        batch.begin();
        try {
            super.update(deltaTime);
        } finally {
            batch.end();
        }

//        MemoryUtils.logMemoryUsage("RenderingSystem after update");

        // Логируем для отладки
        if (Gdx.graphics.getFrameId() % 6000 == 0) {
            Gdx.app.debug("LocationRenderSystem", "Rendered entities: " + getEntities().size());
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GlobalPositionComponent position = globalPositionMapper.get(entity);
        RenderComponent render = renderMapper.get(entity);

        int qq = 0;

        getHexCenter(position.getCoordinates(), tempVector);

        // Быстрая проверка с кэшированными размерами
        if (isInViewport(tempVector, render.getSprite().getWidth(),  render.getSprite().getHeight())) {
            float halfWidth = render.getSprite().getWidth() * 0.5f;
            float halfHeight = render.getSprite().getHeight() * 0.5f;
            render.getSprite().setPosition(tempVector.x - halfWidth, tempVector.y - halfHeight);
            render.getSprite().draw(batch);
        }

//        Gdx.app.log("LocationRenderSystem", "Rendered entities: " + qq);
    }

    private void updateCameraBounds() {
        float halfViewportWidth = camera.viewportWidth * 0.5f * camera.zoom;
        float halfViewportHeight = camera.viewportHeight * 0.5f * camera.zoom;

        cameraBounds.set(
                camera.position.x - halfViewportWidth,
                camera.position.y - halfViewportHeight,
                halfViewportWidth * 2,
                halfViewportHeight * 2
        );
        cameraZoom = camera.zoom;
    }

    private boolean isInViewport(Vector2 position, float width, float height) {
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;

        return !(position.x + halfWidth < cameraBounds.x ||
                 position.x - halfWidth > cameraBounds.x + cameraBounds.width ||
                 position.y + halfHeight < cameraBounds.y ||
                 position.y - halfHeight > cameraBounds.y + cameraBounds.height);
    }

    private void getHexCenter(HexCoordinates hex, Vector2 out) {
        out.x = HEX_SIZE * (float) (Math.sqrt(3) * (hex.getQ() + 0.5 * (hex.getR() & 1)));
        out.y = HEX_SIZE * 1.5f * hex.getR();
    }
}
