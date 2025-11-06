package io.github.game.core.world.generator;

import com.badlogic.ashley.core.Entity;
import io.github.game.core.world.HexMap;
import io.github.game.services.NameService;
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
    private final NameService nameService;
    private HexMap hexMap ;
    private List<Entity> locations = new ArrayList<>();
    private List<Entity> npcs = new ArrayList<>();
    private List<Entity> items = new ArrayList<>();

    @Inject
    public GenerationContext(GameplaySettings gameplaySettings, NameService nameService) {
        this.settings = gameplaySettings;
        this.nameService = nameService;
        this.hexMap = new HexMap(settings.getHexSize(), settings.getHexSize());
    }

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
