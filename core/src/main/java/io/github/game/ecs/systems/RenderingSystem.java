package io.github.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.game.ecs.components.PositionComponent;
import io.github.game.ecs.components.RenderComponent;

public class RenderingSystem extends IteratingSystem {

  private static final ComponentMapper<PositionComponent> POSITION = ComponentMapper.getFor(
      PositionComponent.class);
  private final ComponentMapper<RenderComponent> RENDER = ComponentMapper.getFor(
      RenderComponent.class);

  private final SpriteBatch batch;

  public RenderingSystem(SpriteBatch batch) {
    super(Family.all(PositionComponent.class, RenderComponent.class).get());
    this.batch = batch;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    // Получаем компоненты позиции и отрисовки
    PositionComponent position = POSITION.get(entity);
    RenderComponent render = RENDER.get(entity);

    // Обновляем позицию спрайта
    render.getSprite().setPosition(position.getCoordinates().x, position.getCoordinates().y);

    // Отрисовываем спрайт
    render.getSprite().draw(batch);
  }

  @Override
  public void update(float deltaTime) {
    batch.begin();
    super.update(deltaTime);
    batch.end();
  }
}
