package io.github.game.core.world.generator.location;

import com.badlogic.ashley.core.Entity;
import java.util.List;

public interface LocationGenerator {

    List<Entity> generateLocations();

}
