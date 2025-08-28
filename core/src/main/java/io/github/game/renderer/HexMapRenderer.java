package io.github.game.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HexMapRenderer {

    // Константы для размера гекса
    private static final float HEX_SIZE = 25f;
    private static final float HEX_WIDTH = (float) (Math.sqrt(3) * HEX_SIZE);
    private static final float HEX_HEIGHT = 2 * HEX_SIZE;
    private final HexMap hexMap;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    // Кэш для отрисовки вершин гексов //TODO проверить
    private final Map<Hex, List<Vector2>> hexVerticesCache = new HashMap<>();

    @Inject
    public HexMapRenderer(HexMap hexMap, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.hexMap = hexMap;
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }

    public void render() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Получаем видимую область камеры
        float camX = camera.position.x;
        float camY = camera.position.y;
        float camWidth = camera.viewportWidth * camera.zoom;
        float camHeight = camera.viewportHeight * camera.zoom;

        // Отрисовка заполненных гексов
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Hex hex : hexMap.getHexes().values()) {
            Vector2 center = getHexCenter(hex);
            if (isHexVisible(center, camX, camY, camWidth, camHeight)) {
                drawFilledHex(hex);
            }
        }
        shapeRenderer.end();

        // Отрисовка контуров
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Hex hex : hexMap.getHexes().values()) {
            Vector2 center = getHexCenter(hex);
            if (isHexVisible(center, camX, camY, camWidth, camHeight)) {
                drawHexOutline(hex);
            }

        }
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }


    private void drawHexOutline(Hex hex) {
        shapeRenderer.setColor(0, 0, 0, 1); // Черный контур

        List<Vector2> vertices = getHexVertices(hex);

        // Рисуем контур шестиугольника
        for (int i = 0; i < vertices.size(); i++) {
            Vector2 current = vertices.get(i);
            Vector2 next = vertices.get((i + 1) % vertices.size());
            shapeRenderer.line(current.x, current.y, next.x, next.y);
        }
    }

    private void drawFilledHex(Hex hex) {

        switch (hex.getType()) {
            case PLAINS -> shapeRenderer.setColor(0.4f, 0.8f, 0.2f, 1);
            case FOREST -> shapeRenderer.setColor(0.2f, 0.6f, 0.1f, 1);
            case MOUNTAINS -> shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
            case OCEAN -> shapeRenderer.setColor(0.2f, 0.4f, 0.8f, 1);
            case COAST -> shapeRenderer.setColor(0.6f, 0.4f, 0.2f, 1);
            case DESERT -> shapeRenderer.setColor(0.95f, 0.9f, 0.11f, 1);
            case SWAMP -> shapeRenderer.setColor(0.1f, 0.2f, 0.1f, 1);
            default -> shapeRenderer.setColor(0.9f, 0.1f, 0.9f, 1); // Розовый по умолчанию
        }

        List<Vector2> vertices = getHexVertices(hex);

        // Рисуем заполненный полигон
        for (int i = 0; i < vertices.size() - 1; i++) {
            Vector2 v1 = vertices.get(i);
            Vector2 v2 = vertices.get(i + 1);
            Vector2 v3 = getHexCenter(hex);
            shapeRenderer.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
        }

        // Замыкаем полигон
        Vector2 first = vertices.get(0);
        Vector2 last = vertices.get(vertices.size() - 1);
        Vector2 center = getHexCenter(hex);
        shapeRenderer.triangle(first.x, first.y, last.x, last.y, center.x, center.y);
    }

    private List<Vector2> getHexVertices(Hex hex) {
        return hexVerticesCache.computeIfAbsent(hex, k -> {
            List<Vector2> vertices = new ArrayList<>();
            Vector2 center = getHexCenter(hex);
            for (int i = 0; i < 6; i++) {
                double angle = 2 * Math.PI / 6 * (i + 0.5);
                float x = center.x + HEX_SIZE * (float) Math.cos(angle);
                float y = center.y + HEX_SIZE * (float) Math.sin(angle);
                vertices.add(new Vector2(x, y));
            }
            return vertices;
        });
    }

    private Vector2 getHexCenter(Hex hex) {
        float x = HEX_SIZE * (float) (Math.sqrt(3) * (hex.getQ() + 0.5 * (hex.getR() & 1)));
        float y = HEX_SIZE * 1.5f * hex.getR();
        return new Vector2(x, y);
    }

    private boolean isHexVisible(Vector2 center, float camX, float camY, float camWidth,
                                 float camHeight) {
        return center.x + HEX_WIDTH > camX - camWidth / 2 &&
               center.x - HEX_WIDTH < camX + camWidth / 2 &&
               center.y + HEX_HEIGHT > camY - camHeight / 2 &&
               center.y - HEX_HEIGHT < camY + camHeight / 2;
    }
}
