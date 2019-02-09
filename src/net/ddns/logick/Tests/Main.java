package net.ddns.logick.Tests;

import net.ddns.logick.engene.GLFWwindow;
import net.ddns.logick.shaders.Shader;
import net.ddns.logick.shaders.ShaderLoader;
import org.lwjgl.BufferUtils;
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
        GLFWwindow window = new GLFWwindow(SCR_WIDTH,SCR_HEIGHT,"Test");
        window.setCurrient();
        window.setVSync(1);
        window.showWindow();
        GL.createCapabilities();
        Shader shader = null;
        try {
            shader = ShaderLoader.loadShaderProgram("shaders/simpleShader/vertex.glsl","shaders/simpleShader/fragment.glsl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int vao = initVao();
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        while ( !window.shouldClose() ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            shader.use();
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES,0,3);
            window.swapBuffers();
            glfwPollEvents();
        }
        window.destroy();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void initGLFW(){
        GLFWErrorCallback.createPrint(System.err).set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
    }
    public static int initVao(){
       float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f
        };
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vbo);
        glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,3*Float.BYTES,0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        return vao;
    }
}
