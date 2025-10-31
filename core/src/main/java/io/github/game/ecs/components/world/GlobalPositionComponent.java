package io.github.game.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import io.github.game.core.world.hex.HexCoordinates;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalPositionComponent implements Component, Pool.Poolable {

    HexCoordinates coordinates;


    public GlobalPositionComponent() {
    }

    public GlobalPositionComponent(HexCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public void reset() {
        coordinates = null;
    }
}
