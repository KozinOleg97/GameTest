package io.github.game.renderer;

import static io.github.game.core.world.hex.HexType.COAST;
import static io.github.game.core.world.hex.HexType.DESERT;
import static io.github.game.core.world.hex.HexType.FOREST;
import static io.github.game.core.world.hex.HexType.MOUNTAINS;
import static io.github.game.core.world.hex.HexType.OCEAN;
import static io.github.game.core.world.hex.HexType.PLAINS;
import static io.github.game.core.world.hex.HexType.SWAMP;
import static io.github.game.core.world.hex.HexUtils.HEX_SIZE;
import static io.github.game.core.world.hex.HexUtils.HEX_WIDTH;
import static io.github.game.core.world.hex.HexUtils.Y_PITCH;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.HexType;
import java.util.EnumMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class HexMapRenderer {

    // Константы для размера гекса


    // Статические данные для вершин единичного гекса (предварительно вычисленные)
    private static final float[] UNIT_HEX_VERTICES = new float[2 * 7];
    static private final Color contourColor = new Color(0, 0, 0, 1);

    static {
        // Вычисляем вершины единичного гекса один раз при загрузке класса
        for (int i = 0; i < 7; i++) {
            // начинаем с нижнего элемента
            double angle = 2 * Math.PI / 6 * (i + 4.5);
            UNIT_HEX_VERTICES[i * 2] = HEX_SIZE * (float) Math.cos(angle);
            UNIT_HEX_VERTICES[i * 2 + 1] = HEX_SIZE * (float) Math.sin(angle);
        }
    }

    private final HexMap hexMap;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    // цвета гексов
    private final Map<HexType, Color> hexColorsMap = new EnumMap<>(
        Map.of(PLAINS, new Color(0.4f, 0.8f, 0.2f, 1),    // Зеленый для равнин
               FOREST, new Color(0.2f, 0.6f, 0.1f, 1),     // Темно-зеленый для леса
               MOUNTAINS, new Color(0.5f, 0.5f, 0.5f, 1),  // Серый для гор
               OCEAN, new Color(0.2f, 0.4f, 0.8f, 1),      // Синий для океана
               COAST, new Color(0.8f, 0.8f, 0.6f, 1),      // Бежевый для побережья
               DESERT, new Color(0.95f, 0.9f, 0.11f, 1),   // Желтый для пустыни
               SWAMP, new Color(0.1f, 0.2f, 0.1f, 1)      // Темно-зеленый для болота
        ));
    private final HexGridOutlineRenderer lineRenderer;
    private final HexGridTextureRenderer hexRenderer;


    @Inject
    public HexMapRenderer(HexMap hexMap, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.hexMap = hexMap;
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
        this.lineRenderer = new HexGridOutlineRenderer(10000, contourColor, UNIT_HEX_VERTICES);
        this.hexRenderer = new HexGridTextureRenderer(10000, hexColorsMap, UNIT_HEX_VERTICES);
    }

    /**
     * Основной метод рендеринга гексовой карты
     */
    public void render() {

        // Получаем видимую область камеры
        float camX = camera.position.x;
        float camY = camera.position.y;
        float camWidth = camera.viewportWidth * camera.zoom;
        float camHeight = camera.viewportHeight * camera.zoom;

        // Вычисляем границы видимой области в мировых координатах
        float left = camX - camWidth / 2;
        float right = camX + camWidth / 2;
        float bottom = camY - camHeight / 2;
        float top = camY + camHeight / 2;

        // Вычисляем диапазон индексов гексов, которые могут быть видны
        // Более точный расчет с учетом смещения гексов
        int minQ = Math.max(0, (int) ((left - HEX_WIDTH) / HEX_WIDTH + 1.0f));   //лево
        int maxQ = Math.min(hexMap.getWidth() - 1,
                            (int) ((right + HEX_WIDTH) / HEX_WIDTH - 0.5f)); //право

        int minR = Math.max(0, (int) ((bottom - HEX_SIZE) / Y_PITCH + 1.0f));    //низ
        int maxR = Math.min(hexMap.getHeight() - 1, (int) ((top + HEX_SIZE) / Y_PITCH)); //верх

        hexRenderer.render(hexMap, camera.combined, minQ, maxQ, minR, maxR);
        lineRenderer.render(hexMap, camera.combined, minQ, maxQ, minR, maxR);
    }

    /**
     * Освобождает ресурсы
     */
    public void dispose() {
        shapeRenderer.dispose();
        lineRenderer.dispose();
        hexRenderer.dispose();
    }

}
