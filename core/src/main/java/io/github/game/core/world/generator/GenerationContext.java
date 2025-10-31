package io.github.game.core.world.generator;

import com.badlogic.ashley.core.Entity;
import io.github.game.core.world.HexMap;
import io.github.game.settings.GameplaySettings;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 * Общий контекст для генераторов
 */
@Setter
@Getter
public class GenerationContext {

    private GameplaySettings settings;
    private HexMap hexMap ;
    private List<Entity> locations = new ArrayList<>();
    private List<Entity> npcs = new ArrayList<>();
    private List<Entity> items = new ArrayList<>();

    @Inject
    public GenerationContext(GameplaySettings gameplaySettings) {
        this.settings = gameplaySettings;
        this.hexMap = new HexMap(settings.getHexSize(), settings.getHexSize());
    }
    // Add more as needed, e.g., quests, events

    // Methods to add individual items
    public void addLocation(Entity location) {
        locations.add(location);
    }

    public void addNpc(Entity npc) {
        npcs.add(npc);
    }

    public void addItem(Entity item) {
        items.add(item);
    }
}
