package io.github.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;

@Getter
public class RenderComponent implements Component {

    public static final ComponentMapper<RenderComponent> MAPPER =
        ComponentMapper.getFor(RenderComponent.class);

    private final Sprite sprite;

    public RenderComponent(Sprite sprite) {
        this.sprite = sprite;
    }
}
