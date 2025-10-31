package io.github.game.core.world.generator.world;

import com.badlogic.gdx.Gdx;
import io.github.game.core.world.HexMap;

/**
 * World generator that loads a saved world from a file.
 * Assumes JSON format for simplicity; can be extended to binary or other formats.
 */
public class SavedWorldGenerator implements WorldGenerator {

    private final String filePath;

    public SavedWorldGenerator(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public HexMap generateWorld() {
        // Load from file
        // For now, placeholder - implement JSON deserialization
        Gdx.app.log("SavedWorldGenerator", "Loading world from: " + filePath);
        // Example: return HexMap.fromJson(Gdx.files.internal(filePath).readString());
        throw new UnsupportedOperationException("SavedWorldGenerator not yet implemented");
    }
}
