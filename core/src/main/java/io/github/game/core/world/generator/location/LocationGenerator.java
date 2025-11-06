package io.github.game.core.world.generator.location;

import com.badlogic.ashley.core.Entity;
import io.github.game.core.world.generator.GenerationContext;
import java.util.List;

public interface LocationGenerator {

    List<Entity> generateLocations();

}
