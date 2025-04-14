package io.github.game.ecs.components;

import com.badlogic.ashley.core.Component;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VelocityComponent implements Component {

    private float velocityX = 0;
    private float velocityY = 0;
}
