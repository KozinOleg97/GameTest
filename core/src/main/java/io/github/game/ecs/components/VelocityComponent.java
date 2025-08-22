package io.github.game.ecs.components;

import com.badlogic.ashley.core.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VelocityComponent implements Component {

    private float velocityX = 0;
    private float velocityY = 0;

}
