package io.github.game.core.world.generator.location;

import io.github.game.core.world.generator.GenerationContext;
import io.github.game.core.world.generator.GeneratorStep;

/**
 * Step for generating locations.
 */
public class LocationGenerationStep implements GeneratorStep {

    private final LocationGenerator locationGenerator;

    public LocationGenerationStep(LocationGenerator locationGenerator) {
        this.locationGenerator = locationGenerator;
    }

    @Override
    public void execute(GenerationContext context) {
        context.setLocations(locationGenerator.generateLocations());
    }
}
