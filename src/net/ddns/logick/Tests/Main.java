package net.ddns.logick.Tests;

import net.ddns.logick.engene.GLFWwindow;
import net.ddns.logick.input.Input;
import net.ddns.logick.shaders.Shader;
import net.ddns.logick.shaders.ShaderLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWJoystickCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

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
        Shader shader = null;
        try {
            shader = ShaderLoader.loadShaderProgram("shaders/simpleShader/vertex.glsl", "shaders/simpleShader/fragment.glsl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] color = new float[]{0, 1, 0};
        int vao = initVao();
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        while (!window.shouldClose()) {
            input.processInput();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            shader.use();
            System.out.println(color[1] = (float) Math.abs(Math.sin(glfwGetTime())));
            shader.setVec3f("color1", color);
            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            window.swapBuffers();
            glfwPollEvents();
        }
        window.destroy();
        shader.destroy();
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
                0.5f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f
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
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        return vao;
    }
}
