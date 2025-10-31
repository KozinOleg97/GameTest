package io.github.game.renderer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import io.github.game.core.world.HexMap;
import java.nio.FloatBuffer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class HexGridOutlineRenderer {
    private final int maxVertices;
    private final float[] contourVertices;

    private final Mesh mesh;
    private ShaderProgram shader;
    private final int vertexSize;
    private final float[] vertices;

    // Константы для размера гекса
    private static final float HEX_SIZE = 25f;
    private static final float HEX_WIDTH = (float) (Math.sqrt(3) * HEX_SIZE);
    private static final float HALF_WIDTH = HEX_WIDTH / 2;
    private static final float HEX_HEIGHT = 2 * HEX_SIZE;
    private static final float Y_PITCH = 1.5f * HEX_SIZE;

    public HexGridOutlineRenderer (int maxVertices, Color contourColor, float[] contourVertices) {
        this.maxVertices = maxVertices;
        this.contourVertices = contourVertices;
        this.shader = createLineShader(contourColor);

        mesh = new Mesh(false, maxVertices, 0, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));

        vertexSize = mesh.getVertexAttributes().vertexSize / 4;
        vertices = new float[maxVertices * vertexSize];
    }

    public void render(HexMap map, Matrix4 viewProjection, int minQ, int maxQ, int minR, int maxR) {
        float[] contourVertices = this.contourVertices;
        float[] vertices = this.vertices;
        int vertexSize = this.vertexSize;
        int endIndex = 0;
        int verticeOverflowLimit = maxVertices - (8 * (maxR - (minR & ~1) + 1));

        float x, y;

        for (int q = minQ; q <= maxQ; q++) {
            if ( verticeOverflowLimit < endIndex ) {
                flush(endIndex, viewProjection, GL20.GL_LINE_STRIP);
                endIndex = 0;
            }
            x = HEX_WIDTH * q;
            y = Y_PITCH * minR;

            for (int r = minR & ~1; r <= maxR; r += 2) {
                y = Y_PITCH * r;

                for (int i = 12; i >= 6; i -= 2) {
                    vertices[endIndex] = x + contourVertices[i];
                    vertices[endIndex + 1] = y + contourVertices[i + 1];
                    endIndex += vertexSize;
                }
            }

            if ((maxR & 1) == 1)
            {
                vertices[endIndex] = x + contourVertices[0];
                vertices[endIndex + 1] = y + contourVertices[1] + 2 * Y_PITCH;
                endIndex += vertexSize;
            }

            for (int r = maxR & ~1; r >= minR - 1; r -= 2) {
                y = Y_PITCH * r;

                for (int i = 6; i >= 0; i -= 2) {
                    vertices[endIndex] = x + contourVertices[i];
                    vertices[endIndex + 1] = y + contourVertices[i + 1];
                    endIndex += vertexSize;
                }

            }

            vertices[endIndex] = x + contourVertices[2];
            vertices[endIndex + 1] = y + contourVertices[3];
            endIndex += vertexSize;
        }

        flush(endIndex, viewProjection, GL20.GL_LINE_STRIP);
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
    static public ShaderProgram createLineShader (Color contourColor) {
        String vertexShader = createLineVertexShader();
        String fragmentShader = createLineFragmentShader(contourColor);
        ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
        if (!program.isCompiled()) throw new GdxRuntimeException("Error compiling shader: " + program.getLog());
        return program;
    }

    static private String createLineVertexShader () {
        String shader = "in vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "uniform mat4 u_projModelView;\n"
                        + "void main() {\n"
                        + "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "   gl_PointSize = 1.0;\n"
                        + "}";
        return shader;
    }

    static private String createLineFragmentShader (Color contourColor) {
        String shader = "#ifdef GL_ES\nprecision mediump float;\n#endif\n"
                        + "void main() {\n"
                        + "   gl_FragColor =  + vec4(" + contourColor.a + "," + contourColor.b + "," + contourColor.g + "," + contourColor.r + ");\n"
                        + "}";
        return shader;
    }
}
