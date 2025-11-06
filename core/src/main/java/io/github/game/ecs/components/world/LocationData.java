package io.github.game.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationData implements Component, Pool.Poolable{


    private String name;
    private short size;
    private String type;


    @Override
    public void reset() {

    }
}
