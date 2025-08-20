package io.github.game.core.world.hex;

import lombok.Getter;
import lombok.Setter;

/**
 * Представляет один гекс на карте мира. Гекс является основной территориальной единицей и может
 * содержать различные локации.
 */
@Getter
public class Hex {

    private final HexCoordinates coordinates;

    @Setter
    private HexType type;

    @Setter
    private int dangerLevel;

    @Setter
    private String locationId;

    @Setter
    private long generationSeed;

    /**
     * Конструктор гекса
     *
     * @param coordinates координаты гекса
     * @param type        тип местности гекса
     */
    public Hex(HexCoordinates coordinates, HexType type) {
        this.coordinates = coordinates;
        this.type = type;
        this.dangerLevel = 0;
        this.locationId = null;
        this.generationSeed = 0L;
    }

    /**
     * Вспомогательный конструктор
     */
    public Hex(int q, int r, HexType type) {
        this(new HexCoordinates(q, r), type);
    }

    public int getQ() {
        return coordinates.getQ();
    }

    public int getR() {
        return coordinates.getR();
    }
}
