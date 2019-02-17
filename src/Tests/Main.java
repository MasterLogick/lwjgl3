package Tests;

import com.hackoeur.jglm.Mat4;
import net.ddns.logick.input.Input;
import net.ddns.logick.render.Camera;
import net.ddns.logick.render.shaders.Shader;
import net.ddns.logick.render.shaders.ShaderLoader;
import net.ddns.logick.render.textures.Texture;
import net.ddns.logick.windows.GLFWwindow;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Main {
    private static int SCR_WIDTH = 800;
    private static int SCR_HEIGHT = 600;

    public static void main(String[] args) {
        initGLFW();
        Input input = new Input();
        GLFWwindow window = new GLFWwindow(SCR_WIDTH, SCR_HEIGHT, "Test");
        window.setCurrient();
        window.setVSync(1);
        window.showWindow();
        window.setKeyCallbacks(input);
        GL.createCapabilities();
        Camera.init(45f, ((float) SCR_WIDTH) / ((float) SCR_HEIGHT), 0.1f, 100f);
        FloatBuffer fb = Mat4.MAT4_IDENTITY.getBuffer();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(fb.get() + " ");
            }
            System.out.println();
        }
        glEnable(GL_DEPTH_TEST);
        Texture texture = new Texture("res/textures/brick-texture-png-23887.png");
        Shader newShader = null;
        Shader oldShader = null;
        try {
            newShader = ShaderLoader.loadShaderProgram("res/shaders/newShader/vertex.glsl", "res/shaders/newShader/fragment.glsl");
            oldShader = ShaderLoader.loadShaderProgram("res/shaders/simpleShader/vertex.glsl", "res/shaders/simpleShader/fragment.glsl");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int vao = initVao();
        glClearColor(0.11f, 0.11f, 0.11f, 0.0f);
        while (!window.isWindowShouldClose()) {
            input.processInput(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            oldShader.use();
            newShader.use();
            newShader.setProjectionMatrix(Camera.projectionMatrix);
            newShader.setViewMatrix(Camera.viewMatrix);
            newShader.setModelMatrix(Mat4.MAT4_IDENTITY);
            texture.use();
            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            window.swapBuffers();
            glfwPollEvents();
        }
        window.destroy();
        newShader.destroy();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void initGLFW() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
    }

    public static int initVao() {
        float[] vertices = {
                0.5f, 0.5f,  -1.0f, 1.0f, 1.0f,
                0.5f, -0.5f, -1.0f, 1.0f, 0.0f,
                -0.5f, -0.5f,-1.0f, 0.0f, 0.0f,
                -0.5f, 0.5f, -1.0f, 0.0f, 1.0f
        };
        int[] indices = {
                0, 1, 3,
                1, 2, 3
        };
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        int ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        return vao;
    }
}
