package io.github.game.renderer;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Matrix4;
import io.github.game.core.world.HexMap;
import io.github.game.core.world.hex.Hex;
import io.github.game.core.world.hex.HexType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class HexGridTextureRenderer {
    private final int maxVertices;
    private final float[] contourVertices;

    // кешированные floatBits для цветов
    private final Map<HexType, Float> hexFloatBits;

    private final Mesh mesh;
    private final ShaderProgram shader;
    private final int vertexSize;
    private final float[] vertices;

    // Константы для размера гекса
    private static final float HEX_SIZE = 25f;
    private static final float HEX_WIDTH = (float) (Math.sqrt(3) * HEX_SIZE);
    private static final float HALF_WIDTH = HEX_WIDTH / 2;
    private static final float HEX_HEIGHT = 2 * HEX_SIZE;
    private static final float Y_PITCH = 1.5f * HEX_SIZE;

    public HexGridTextureRenderer (int maxVertices, Map<HexType, Color> hexColorsMap, float[] contourVertices) {
        this.maxVertices = maxVertices;
        this.contourVertices = contourVertices;
        this.hexFloatBits = hexColorsMap
            .entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey(),
                e -> e.getValue().toFloatBits()
            ));
        shader = createHexShader();

        mesh = new Mesh(false, maxVertices, 0,
                        new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                        new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE));

        vertexSize = mesh.getVertexAttributes().vertexSize / 4;
        vertices = new float[maxVertices * vertexSize];
    }

    public void render(HexMap map, Matrix4 viewProjection, int minQ, int maxQ, int minR, int maxR) {
        float[] contourVertices = this.contourVertices;
        float[] vertices = this.vertices;
        int vertexSize = this.vertexSize;
        Map<HexType, Float> hexFloatBits = this.hexFloatBits;
        int endIndex = 0;
        int vertexOverflowLimit = maxVertices - (5 + 4 * (maxQ - minQ + 1));

        float x, y;
        float oddOffset = HALF_WIDTH * (minR & 1);

        Hex hex = map.getHex(minQ, minR);
        HexType prevType = hex.getType();
        float colorBits = hexFloatBits.get(prevType);

        // Отрисовка заполненных гексов
        for (int r = minR; r <= maxR; r++) {
            if ( vertexOverflowLimit < endIndex ) {
                flush(endIndex, viewProjection, GL20.GL_TRIANGLE_STRIP);
                endIndex = 0;
            }
            y = Y_PITCH * r;
            x = HEX_WIDTH * minQ + oddOffset;

            vertices[endIndex] = x + contourVertices[10];
            vertices[endIndex + 1] = y + contourVertices[11];
            vertices[endIndex + 2] = colorBits;
            vertices[endIndex + 3] = x + contourVertices[10];
            vertices[endIndex + 4] = y + contourVertices[11];
            vertices[endIndex + 5] = colorBits;
            vertices[endIndex + 6] = x + contourVertices[8];
            vertices[endIndex + 7] = y + contourVertices[9];
            vertices[endIndex + 8] = colorBits;
            endIndex += vertexSize * 3;


            Hex[] curRow = map.getRowDirectAccess(r);
//            Hex[][] curGrid = map.getHexGrid();

//            Iterator<Hex> rowIteratorUnsafe = map.getRowIteratorUnsafe(r, minQ);

            for (int q = minQ; q <= maxQ; q++) {
                x = HEX_WIDTH * q + oddOffset;

//                hex = map.getHex(q, r);

                hex = curRow[q];

//                hex = curGrid[r][q];

//                hex = rowIteratorUnsafe.next();



                if (prevType != hex.getType()) {
                    prevType = hex.getType();
                    colorBits = hexFloatBits.get(prevType);
                }

                vertices[endIndex] = x + contourVertices[0];
                vertices[endIndex + 1] = y + contourVertices[1];
                vertices[endIndex + 2] = colorBits;
                vertices[endIndex + 3] = x + contourVertices[6];
                vertices[endIndex + 4] = y + contourVertices[7];
                vertices[endIndex + 5] = colorBits;
                vertices[endIndex + 6] = x + contourVertices[2];
                vertices[endIndex + 7] = y + contourVertices[3];
                vertices[endIndex + 8] = colorBits;
                vertices[endIndex + 9] = x + contourVertices[4];
                vertices[endIndex + 10] = y + contourVertices[5];
                vertices[endIndex + 11] = colorBits;
                endIndex += vertexSize * 4;
            }

            vertices[endIndex + 0] = x + contourVertices[6];
            vertices[endIndex + 1] = y + contourVertices[7];
            vertices[endIndex + 2] = colorBits;
            vertices[endIndex + 3] = x + contourVertices[6];
            vertices[endIndex + 4] = y + contourVertices[7];
            vertices[endIndex + 5] = colorBits;
            endIndex += vertexSize * 2;

            oddOffset = HALF_WIDTH - oddOffset;
        }

        flush(endIndex, viewProjection, GL20.GL_TRIANGLE_STRIP);
    }

    public void flush (int bytesWritten, Matrix4 projModelView, int primitiveType) {
        if (bytesWritten == 0) return;
        shader.bind();
        shader.setUniformMatrix("u_projModelView", projModelView);
        mesh.setVertices(vertices, 0, bytesWritten);
        mesh.render(shader, primitiveType);
    }

    public void dispose () {
        shader.dispose();
        mesh.dispose();
    }

    /** Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified. */
    static public ShaderProgram createHexShader () {
        String vertexShader = createHexVertexShader();
        String fragmentShader = createHexFragmentShader();
        ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
        if (!program.isCompiled()) throw new GdxRuntimeException("Error compiling shader: " + program.getLog());
        return program;
    }

    static private String createHexVertexShader () {
        String shader = "#version 150\n"
                        + "in vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                        + "uniform mat4 u_projModelView;\n"
                        + "flat out vec4 v_col;\n"
                        + "void main() {\n"
                        + "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                        + "   gl_PointSize = 1.0;\n"
                        + "}";
        return shader;
    }

    static private String createHexFragmentShader () {
        String shader = "#version 150\n"
                        + "#ifdef GL_ES\nprecision mediump float;\n#endif\n"
                        + "flat in vec4 v_col;\n"
                        + "void main() {\n"
                        + "   gl_FragColor = v_col;\n"
                        + "}";
        return shader;
    }
}
