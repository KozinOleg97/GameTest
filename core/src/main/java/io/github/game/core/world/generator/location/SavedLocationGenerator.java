package io.github.game.core.world.generator.location;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import java.util.List;

/**
 * Location generator that loads saved locations from a file.
 */
public class SavedLocationGenerator implements LocationGenerator {

    private final String filePath;

    public SavedLocationGenerator(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Entity> generateLocations() {
        Gdx.app.log("SavedLocationGenerator", "Loading locations from: " + filePath);
        throw new UnsupportedOperationException("SavedLocationGenerator not yet implemented");
    }
}
