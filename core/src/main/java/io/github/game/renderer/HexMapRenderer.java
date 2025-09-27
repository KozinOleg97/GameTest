package io.github.game.renderer;

import static io.github.game.core.world.hex.HexType.COAST;
import static io.github.game.core.world.hex.HexType.DESERT;
import static io.github.game.core.world.hex.HexType.FOREST;
import static io.github.game.core.world.hex.HexType.MOUNTAINS;
import static io.github.game.core.world.hex.HexType.OCEAN;
import static io.github.game.core.world.hex.HexType.PLAINS;
import static io.github.game.core.world.hex.HexType.SWAMP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexType;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
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
        for (int i = 0; i < 7; i++) {
            // начинаем с нижнего элемента
            double angle = 2 * Math.PI / 6 * (i + 4.5);
            UNIT_HEX_VERTICES[i * 2] = HEX_SIZE * (float) Math.cos(angle);
            UNIT_HEX_VERTICES[i * 2 + 1] = HEX_SIZE * (float) Math.sin(angle);
        }
        UNIT_HEX_VERTICES[14] = UNIT_HEX_VERTICES[0];
        UNIT_HEX_VERTICES[15] = UNIT_HEX_VERTICES[1] - HEX_SIZE;
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

    private final Color defaultColor = new Color(0.9f, 0.1f, 0.9f, 1);
    static private final Color contourColor = new Color(0, 0, 0, 1);

    // кешированные floatBits для цветов
    private final Map<HexType, Float> hexFloatBits = hexColorsMap
        .entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey(),
            e -> e.getValue().toFloatBits()
        ));

    private final float defaultFloatBits = defaultColor.toFloatBits();

    private final Matrix4 combinedMatrix = new Matrix4();
    private final LineRenderer lineRenderer;


    @Inject
    public HexMapRenderer(HexMap hexMap, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.hexMap = hexMap;
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
        this.lineRenderer = new LineRenderer(10000, contourColor);
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
        int minQ = Math.max(0, (int) (((left - HEX_WIDTH) / (HEX_WIDTH)) + 1.00f));   //лево
        int maxQ = Math.min(hexMap.getWidth() - 1,
                            (int) (((right + HEX_WIDTH) / (HEX_WIDTH)) - 0.5f));        //право

        int minR = Math.max(0, (int) (((bottom - HEX_SIZE) / Y_PITCH) + 1.0f));    //низ
        int maxR = Math.min(hexMap.getHeight() - 1,
                            (int) (((top + HEX_SIZE) / Y_PITCH) - 0.0f));          //верх

        // Счетчики для статистики
        int filledHexCount = 0;
        int lineHexCount = 0;

        combinedMatrix.set(camera.combined);

        var renderer = shapeRenderer.getRenderer();

        float x, y;
        float oddOffset = HALF_WIDTH * (minR & 1);
        renderer.begin(combinedMatrix, GL20.GL_TRIANGLES);

        Hex hex = hexMap.getHex(minQ, minR);
        HexType prevType = hex.getType();
        float colorBits = hexFloatBits.get(prevType);

        // Отрисовка заполненных гексов
        for (int r = minR; r <= maxR; r++) {
            if (renderer.getMaxVertices() - renderer.getNumVertices() < 12 * (maxQ - minQ + 1)) {
                renderer.end();
                renderer.begin(combinedMatrix, GL20.GL_TRIANGLES);
            }
            y = Y_PITCH * r;

            for (int q = minQ; q <= maxQ; q++) {
                x = HEX_WIDTH * q + oddOffset;

                hex = hexMap.getHex(q, r);
                if (prevType != hex.getType()) {
                    prevType = hex.getType();
                    colorBits = hexFloatBits.get(prevType);
                }

                for (int i = 0; i < 12; i += 4) {
                    for (int j = 0; j < 6; j += 2) {
                        renderer.color(colorBits);
                        renderer.vertex(x + UNIT_HEX_VERTICES[i + j], y + UNIT_HEX_VERTICES[i + j + 1], 0);
                    }
                }

                for (int j = 0; j < 12; j += 4) {
                    renderer.color(colorBits);
                    renderer.vertex(x + UNIT_HEX_VERTICES[j], y + UNIT_HEX_VERTICES[j + 1], 0);
                }

                filledHexCount++;
            }

            oddOffset = HALF_WIDTH - oddOffset;
        }

        renderer.end();

        var lineRenderer = this.lineRenderer;
        // Отрисовка контуров гексов
        float[] buffer = lineRenderer.getVerticesBuffer();
        int endIndex = 0;
        lineRenderer.begin(combinedMatrix, GL20.GL_LINE_STRIP);
        int vertSize = lineRenderer.getVertexSize();

        for (int q = minQ; q <= maxQ; q++) {
            if (lineRenderer.getMaxVertices() - endIndex < 8 * (maxR - (minR & ~1) + 1)) {
                lineRenderer.end(endIndex);
                endIndex = 0;
                lineRenderer.begin(combinedMatrix, GL20.GL_LINE_STRIP);
            }
            x = HEX_WIDTH * q;
            y = Y_PITCH * minR;

            for (int r = minR & ~1; r <= maxR; r += 2) {
                y = Y_PITCH * r;

                for (int i = 12; i >= 6; i -= 2) {
                    buffer[endIndex] = x + UNIT_HEX_VERTICES[i];
                    buffer[endIndex + 1] = y + UNIT_HEX_VERTICES[i + 1];
                    endIndex += vertSize;
                }
            }

            if ((maxR & 1) == 1)
            {
                buffer[endIndex] = x + UNIT_HEX_VERTICES[0];
                buffer[endIndex + 1] = y + UNIT_HEX_VERTICES[1] + 2 * Y_PITCH;
                buffer[endIndex + 2] = x + UNIT_HEX_VERTICES[6];
                buffer[endIndex + 3] = y + UNIT_HEX_VERTICES[7];
                endIndex += vertSize * 2;
            }

            for (int i = 4; i >= 0; i -= 2) {
                buffer[endIndex] = x + UNIT_HEX_VERTICES[i];
                buffer[endIndex + 1] = y + UNIT_HEX_VERTICES[i + 1];
                endIndex += vertSize;
            }

            for (int r = (maxR & ~1) - 2; r >= minR - 1; r -= 2) {
                y = Y_PITCH * r;

                for (int i = 6; i >= 0; i -= 2) {
                    buffer[endIndex] = x + UNIT_HEX_VERTICES[i];
                    buffer[endIndex + 1] = y + UNIT_HEX_VERTICES[i + 1];
                    endIndex += vertSize;
                }

                lineHexCount++;
            }

            buffer[endIndex] = x + UNIT_HEX_VERTICES[2];
            buffer[endIndex + 1] = y + UNIT_HEX_VERTICES[3];
            endIndex += vertSize;
        }

        lineRenderer.end(endIndex);

//        // Логируем количество отрисованных гексов
//        Gdx.app.log("Rendered",
//                    "Rendered filled hexes: " + filledHexCount + ", line hexes: " + lineHexCount);

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.rect(left + 1, bottom + 1, right - left - 1, top - bottom - 1);
//        shapeRenderer.end();
    }

    /**
     * Освобождает ресурсы
     */
    public void dispose() {
        shapeRenderer.dispose();
    }

}
