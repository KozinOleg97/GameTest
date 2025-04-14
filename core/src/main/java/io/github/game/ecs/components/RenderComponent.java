package io.github.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;

@Getter
public class RenderComponent implements Component {

    private final Sprite sprite;

    public RenderComponent(Sprite sprite) {
        this.sprite = sprite;
    }
}
