package io.github.game.core.world.generator.location;

import com.badlogic.ashley.core.Entity;
import io.github.game.core.world.HexMap;
import java.util.List;

public class RandomLocationGenerator implements LocationGenerator{

    HexMap hexMap;

    public RandomLocationGenerator(HexMap hexMap, int count) {
        this.hexMap = hexMap;
    }

    @Override
    public List<Entity> generateLocations() {


        return null;
    }
}
