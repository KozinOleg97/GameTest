package io.github.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RenderComponent implements Component, Pool.Poolable {

    private Sprite sprite;

    public RenderComponent() {
    }

    public RenderComponent(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void reset() {
        this.sprite = null;
    }
}
