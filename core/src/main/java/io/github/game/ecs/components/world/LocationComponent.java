package io.github.game.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Компонент-ссылка на локацию. Содержит только идентификатор локации.
 */
@Getter
@Setter
public class LocationComponent implements Component, Pool.Poolable {

    private UUID locationId;
    private LocationData locationData;


    public LocationComponent() {
    }

    public LocationComponent(UUID locationId) {
        this.locationId = locationId;
    }


    @Override
    public void reset() {
        locationId = null;
    }
}
