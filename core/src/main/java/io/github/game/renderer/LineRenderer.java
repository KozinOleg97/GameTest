package io.github.game.renderer;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.nio.FloatBuffer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class LineRenderer {
    private int primitiveType;
    private final int maxVertices;

    private final Mesh mesh;
    private ShaderProgram shader;
    private final int vertexSize;
    private final float[] vertices;
    private final Matrix4 projModelView = new Matrix4();

    public LineRenderer (int maxVertices, Color contourColor) {
        this.maxVertices = maxVertices;
        this.shader = createLineShader(contourColor);

        mesh = new Mesh(false, maxVertices, 0, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));

        vertexSize = mesh.getVertexAttributes().vertexSize / 4;
        vertices = new float[maxVertices * vertexSize];
    }

    public int getVertexSize() {
        return vertexSize;
    }

    public void begin (Matrix4 projModelView, int primitiveType) {
        this.projModelView.set(projModelView);
        this.primitiveType = primitiveType;
    }

    public float[] getVerticesBuffer() {
        return vertices;
    }

    public void end (int bytesWritten) {
        if (bytesWritten == 0) return;
        shader.bind();
        shader.setUniformMatrix("u_projModelView", projModelView);
        mesh.setVertices(vertices, 0, bytesWritten);
        mesh.render(shader, primitiveType);
    }

    public int getMaxVertices () {
        return maxVertices;
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
        String shader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "uniform mat4 u_projModelView;\n"
                        + "void main() {\n   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "   gl_PointSize = 1.0;"
                        + "\n}";
        return shader;
    }

    static private String createLineFragmentShader (Color contourColor) {
        String shader = "#ifdef GL_ES\nprecision mediump float;\n#endif\n"
                        + "void main() {\n"
                        + "   gl_FragColor =  + vec4(" + contourColor.a + "," + contourColor.b + "," + contourColor.g + "," + contourColor.r + ")"
                        + ";\n}";
        return shader;
    }
}
