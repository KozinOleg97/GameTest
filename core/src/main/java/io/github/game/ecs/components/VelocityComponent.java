package io.github.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VelocityComponent implements Component {
    public static final ComponentMapper<VelocityComponent> MAPPER = ComponentMapper.getFor(VelocityComponent.class);

    private float velocityX = 0;
    private float velocityY = 0;
}
