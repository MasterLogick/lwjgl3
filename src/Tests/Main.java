package Tests;

import com.hackoeur.jglm.Mat4;
import net.ddns.logick.input.Input;
import net.ddns.logick.render.Camera;
import net.ddns.logick.render.shaders.Shader;
import net.ddns.logick.render.shaders.ShaderLoader;
import net.ddns.logick.render.textures.Texture;
import net.ddns.logick.windows.GLFWwindow;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Main {
    public static int SCR_WIDTH = 800;
    public static int SCR_HEIGHT = 600;
    public static GLFWwindow window;
    public static void main(String[] args) {
        initGLFW();
        Camera cam = Camera.init(45f, ((float) SCR_WIDTH) / ((float) SCR_HEIGHT), 0.1f, 100f);
        Input input = new Input(cam);
        window = new GLFWwindow(SCR_WIDTH, SCR_HEIGHT, "Test");
        window.setCurrient();
        window.setVSync(1);
        window.showWindow();
        window.grabMouse();
        window.setMousePos(SCR_WIDTH / 2, SCR_HEIGHT / 2);
        window.setMouseCallbacks(input.cursorPosCallback);
        window.setKeyCallbacks(input);
        GL.createCapabilities();
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
        int vao1 = initVAO1();
        glClearColor(0.11f, 0.11f, 0.11f, 0.0f);
        Logger.getGlobal().info("initiated");
        while (!window.isWindowShouldClose()) {
            input.processInput(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            newShader.use();
            newShader.setProjectionMatrix(cam.projectionMatrix);
            newShader.setViewMatrix(cam.viewMatrix);
            newShader.setModelMatrix(Mat4.MAT4_IDENTITY);
            texture.use();
            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            window.swapBuffers();
            glfwPollEvents();
        }
        Logger.getGlobal().info("game loop was stopped");
        window.destroy();
        newShader.destroy();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        Logger.getGlobal().info("exit");
    }

    private static int initVAO1() {
        float[] vertices = {
                0.5f, 0.5f, -1.0f, 1.0f, 1.0f,
                0.5f, -0.5f, -1.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
                -0.5f, 0.5f, -1.0f, 0.0f, 1.0f
        };
        int[] indices = {
                0, 1, 2
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

    public static void initGLFW() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
    }

    public static int initVao() {
        float[] vertices = {
                0.5f, 0.5f, -1.0f, 1.0f, 1.0f,
                0.5f, -0.5f, -1.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
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
