package io.github.game.core.world.generator.location;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import io.github.game.core.world.generator.GenerationContext;
import java.util.List;

/**
 * Location generator that loads saved locations from a file.
 */
public class SavedLocationGenerator implements LocationGenerator {

    private final String filePath;
    private final GenerationContext context;

    public SavedLocationGenerator(String filePath, GenerationContext context) {
        this.filePath = filePath;
        this.context = context;
    }

    @Override
    public List<Entity> generateLocations() {
        Gdx.app.log("SavedLocationGenerator", "Loading locations from: " + filePath);
        throw new UnsupportedOperationException("SavedLocationGenerator not yet implemented");
    }
}
