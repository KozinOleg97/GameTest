package io.github.game.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import java.util.ArrayList;
import java.util.List;
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

    @Inject
    public HexMapRenderer(HexMap hexMap, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.hexMap = hexMap;
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }

    public void render() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Отрисовка заполненных гексов
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Hex hex : hexMap.getHexes().values()) {
            drawHex(hex);
        }

        shapeRenderer.end();

        // Отрисовка контуров гексов
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Hex hex : hexMap.getHexes().values()) {
            drawHexOutline(hex);
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    private void drawHex(Hex hex) {
        // Выбор цвета в зависимости от типа гекса
        switch (hex.getType()) {
            case PLAINS:
                shapeRenderer.setColor(0.4f, 0.8f, 0.2f, 1); // Зеленый
                break;
            case FOREST:
                shapeRenderer.setColor(0.2f, 0.6f, 0.1f, 1); // Темно-зеленый
                break;
            case MOUNTAINS:
                shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1); // Серый
                break;
            case OCEAN:
                shapeRenderer.setColor(0.2f, 0.4f, 0.8f, 1); // Синий
                break;
            default:
                shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1); // Серый по умолчанию
        }

        List<Vector2> vertices = getHexVertices(hex);

        // Рисуем шестиугольник как замкнутую полигональную линию
        for (int i = 0; i < vertices.size(); i++) {
            Vector2 current = vertices.get(i);
            Vector2 next = vertices.get((i + 1) % vertices.size());
            shapeRenderer.line(current.x, current.y, next.x, next.y);
        }

        // Заполняем гекс, рисуя треугольники от центра к вершинам
        Vector2 center = getHexCenter(hex);
        for (int i = 0; i < vertices.size(); i++) {
            Vector2 current = vertices.get(i);
            Vector2 next = vertices.get((i + 1) % vertices.size());
            shapeRenderer.triangle(
                center.x, center.y,
                current.x, current.y,
                next.x, next.y
            );
        }
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

    private List<Vector2> getHexVertices(Hex hex) {
        List<Vector2> vertices = new ArrayList<>();
        Vector2 center = getHexCenter(hex);

        // Вычисляем вершины шестиугольника
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            float x = center.x + HEX_SIZE * (float) Math.cos(angle);
            float y = center.y + HEX_SIZE * (float) Math.sin(angle);
            vertices.add(new Vector2(x, y));
        }

        return vertices;
    }

    private Vector2 getHexCenter(Hex hex) {
        // Конвертируем гексональные координаты в экранные
        float x = hex.getQ() * HEX_WIDTH * 0.75f;

        // Учитываем смещение для четных/нечетных рядов
        float yOffset = (hex.getQ() % 2 == 0) ? 0 : HEX_HEIGHT * 0.5f;
        float y = hex.getR() * HEX_HEIGHT + yOffset;

        return new Vector2(x, y);
    }
}
