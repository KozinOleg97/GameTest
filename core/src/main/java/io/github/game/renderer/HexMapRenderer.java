package io.github.game.renderer;

import static io.github.game.core.world.hex.HexType.COAST;
import static io.github.game.core.world.hex.HexType.DESERT;
import static io.github.game.core.world.hex.HexType.FOREST;
import static io.github.game.core.world.hex.HexType.MOUNTAINS;
import static io.github.game.core.world.hex.HexType.OCEAN;
import static io.github.game.core.world.hex.HexType.PLAINS;
import static io.github.game.core.world.hex.HexType.SWAMP;

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
    private static final float HEX_SIZE = 25f;
    private static final float HEX_WIDTH = (float) (Math.sqrt(3) * HEX_SIZE);
    private static final float HALF_WIDTH = HEX_WIDTH / 2;
    private static final float HEX_HEIGHT = 2 * HEX_SIZE;
    private static final float Y_PITCH = 1.5f * HEX_SIZE;

    // Статические данные для вершин единичного гекса (предварительно вычисленные)
    private static final float[] UNIT_HEX_VERTICES = new float[2 * 8];

    static {
        // Вычисляем вершины единичного гекса один раз при загрузке класса
        for (int i = 0; i < 6; i++) {
            // начинаем с нижнего элемента
            double angle = 2 * Math.PI / 6 * (i + 4.5);
            UNIT_HEX_VERTICES[i * 2] = HEX_SIZE * (float) Math.cos(angle);
            UNIT_HEX_VERTICES[i * 2 + 1] = HEX_SIZE * (float) Math.sin(angle);
        }
        UNIT_HEX_VERTICES[12] = UNIT_HEX_VERTICES[0];
        UNIT_HEX_VERTICES[13] = UNIT_HEX_VERTICES[1];
        UNIT_HEX_VERTICES[14] = UNIT_HEX_VERTICES[0];
        UNIT_HEX_VERTICES[15] = UNIT_HEX_VERTICES[1] - HEX_SIZE;
    }

    private final HexMap hexMap;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private final Map<HexType, Color> hexColorsMap = new EnumMap<>(
        Map.of(PLAINS, new Color(0.4f, 0.8f, 0.2f, 1),    // Зеленый для равнин
               FOREST, new Color(0.2f, 0.6f, 0.1f, 1),     // Темно-зеленый для леса
               MOUNTAINS, new Color(0.5f, 0.5f, 0.5f, 1),  // Серый для гор
               OCEAN, new Color(0.2f, 0.4f, 0.8f, 1),      // Синий для океана
               COAST, new Color(0.8f, 0.8f, 0.6f, 1),      // Бежевый для побережья
               DESERT, new Color(0.95f, 0.9f, 0.11f, 1),   // Желтый для пустыни
               SWAMP, new Color(0.1f, 0.2f, 0.1f, 1)      // Темно-зеленый для болота
        ));

    private final Color defaultColor = new Color(0.9f, 0.1f, 0.9f, 1);
    private boolean needsRebuild = true;

    // Массивы для быстрого доступа
    private float[] vertices = new float[8 * 2];


    @Inject
    public HexMapRenderer(HexMap hexMap, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.hexMap = hexMap;
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }

    /**
     * Основной метод рендеринга гексовой карты
     */
    public void render() {

        shapeRenderer.setProjectionMatrix(camera.combined);

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
        int minQ = Math.max(0, (int) (((left - HEX_WIDTH) / (HEX_WIDTH)) + 1.00f));   //лево
        int maxQ = Math.min(hexMap.getWidth() - 1,
                            (int) (((right + HEX_WIDTH) / (HEX_WIDTH)) - 0.5f));        //право

        int minR = Math.max(0, (int) (((bottom - HEX_SIZE) / Y_PITCH) + 1.0f));    //низ
        int maxR = Math.min(hexMap.getHeight() - 1,
                            (int) (((top + HEX_SIZE) / Y_PITCH) - 0.0f));          //верх

        // Счетчики для статистики
        int filledHexCount = 0;
        int lineHexCount = 0;

        float initialX = HEX_WIDTH * minQ;
        float x;
        float y = Y_PITCH * minR;
        float oddOffset = HALF_WIDTH * (minR & 1);
        // Отрисовка заполненных гексов
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int r = minR; r <= maxR; r++) {
            x = initialX + oddOffset;
            oddOffset = HALF_WIDTH - oddOffset;
            for (int q = minQ; q <= maxQ; q++) {
                for (int i = 0; i < 6; i++) {
                    vertices[i * 2] = x + UNIT_HEX_VERTICES[i * 2];
                    vertices[i * 2 + 1] = y + UNIT_HEX_VERTICES[i * 2 + 1];
                }
                x += HEX_WIDTH;

                filledHexCount++;
                var hex = hexMap.getHex(q, r);
                shapeRenderer.setColor(hexColorsMap.getOrDefault(hex.getType(), defaultColor));

                // Отрисовываем гекс четырьмя треугольниками
                shapeRenderer.triangle(vertices[0], vertices[1], vertices[2], vertices[3],
                                       vertices[4], vertices[5]);
                shapeRenderer.triangle(vertices[0], vertices[1], vertices[4], vertices[5],
                                       vertices[8], vertices[9]);
                shapeRenderer.triangle(vertices[4], vertices[5], vertices[6], vertices[7],
                                       vertices[8], vertices[9]);
                shapeRenderer.triangle(vertices[8], vertices[9], vertices[10], vertices[11],
                                       vertices[0], vertices[1]);

            }
            y += Y_PITCH;
        }
        shapeRenderer.end();

        // Отрисовка контуров гексов
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);

        maxR++;
        minR &= ~1;
        y = Y_PITCH * minR;
        for (int r = minR; r <= maxR; r += 2) {
            x = initialX;
            for (int q = minQ; q <= maxQ; q++) {
                for (int i = 0; i < 8; i++) {
                    vertices[i * 2] = x + UNIT_HEX_VERTICES[i * 2];
                    vertices[i * 2 + 1] = y + UNIT_HEX_VERTICES[i * 2 + 1];
                }
                x += HEX_WIDTH;

                lineHexCount++;
                shapeRenderer.polyline(vertices, 0, 16);

            }
            y += Y_PITCH * 2;
        }
        shapeRenderer.end();

//        // Логируем количество отрисованных гексов
//        Gdx.app.log("Rendered",
//                    "Rendered filled hexes: " + filledHexCount + ", line hexes: " + lineHexCount);

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.rect(left + 1, bottom + 1, right - left - 1, top - bottom - 1);
//        shapeRenderer.end();
    }


    /**
     * Помечает, что геометрию нужно перестроить
     */
    public void markDirty() {
        needsRebuild = true;
    }

    /**
     * Освобождает ресурсы
     */
    public void dispose() {
        shapeRenderer.dispose();
    }

}
