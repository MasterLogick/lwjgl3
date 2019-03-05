package Tests;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import net.ddns.logick.input.Input;
import net.ddns.logick.render.Camera;
import net.ddns.logick.render.shaders.Shader;
import net.ddns.logick.render.shaders.ShaderLoader;
import net.ddns.logick.render.textures.Texture;
import net.ddns.logick.windows.GLFWwindow;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import res.ResourseManager;

import java.io.IOException;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Main {
    public static int SCR_WIDTH = 1300;
    public static int SCR_HEIGHT = 800;
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
        Texture diffuseMap = null;
        Texture specularMap = null;
        try {
            diffuseMap = new Texture(ResourseManager.getResourseByPath("textures/container2.png"), GL_RGBA);
            specularMap = new Texture(ResourseManager.getResourseByPath("textures/container2_specular.png"), GL_RGBA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Shader lightShader = null;
        Shader shader = null;
        try {
            lightShader = ShaderLoader.loadShaderProgram(ResourseManager.getResourseByPath("shaders/lightedShader/vertex.glsl"), ResourseManager.getResourseByPath("shaders/lightedShader/fragment.glsl"));
            shader = ShaderLoader.loadShaderProgram(ResourseManager.getResourseByPath("shaders/newShader1/vertex.glsl"),ResourseManager.getResourseByPath( "shaders/newShader1/fragment.glsl"));
        } catch (Exception e) {
            e.printStackTrace();
            window.shoulClose();
        }

        int vao = initVAO1();
        int vao1 = initVAO1();
        glClearColor(0.11f, 0.11f, 0.11f, 0.0f);
        glActiveTexture(GL_TEXTURE0);
        diffuseMap.use();
        glActiveTexture(GL_TEXTURE1);
        specularMap.use();
        Logger.getGlobal().info("initialised");
        while (!window.isWindowShouldClose()) {
            Vec3 lightPos = new Vec3(2 * (float) Math.sqrt(1 - Math.cos(glfwGetTime()) * Math.cos(glfwGetTime())) * (float) Math.signum(Math.sin(glfwGetTime())), 0, (float) Math.cos(glfwGetTime()) * 2);
            input.processInput(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            try {
                shader.use(cam.projectionMatrix, cam.viewMatrix);
                shader.setModelMatrix(Mat4.MAT4_IDENTITY.translate(lightPos));
            } catch (Exception e) {
                e.printStackTrace();
            }
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 36);
            glBindVertexArray(0);
            try {
                lightShader.use(cam.projectionMatrix, cam.viewMatrix);
            } catch (Exception e) {
                e.printStackTrace();
                window.shoulClose();
            }
            Vec3 lightColor = new Vec3(2.0f, 0.7f, 1.3f);

            try {
                lightShader.setVec3f("light.position", lightPos);
                lightShader.setVec3f("light.specular", new float[]{1.0f, 1.0f, 1.0f});
                lightShader.setVec3f("light.ambient", lightColor.multiply(0.1f));
                lightShader.setVec3f("light.diffuse", lightColor.multiply(0.5f));
                lightShader.setInt("material.diffuse", 0);
                lightShader.setInt("material.specular", 1);
                lightShader.setFloat("material.shininess", 32);
                lightShader.setModelMatrix(Mat4.MAT4_IDENTITY.translate(new Vec3(0f, 0f, 0f)));
            } catch (Exception e) {
                window.shoulClose();
                e.printStackTrace();
            }

            glBindVertexArray(vao1);
            glDrawArrays(GL_TRIANGLES, 0, 36);
            window.swapBuffers();
            glfwPollEvents();
        }
        Logger.getGlobal().info("game loop was stopped");
        window.destroy();
        lightShader.destroy();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        Logger.getGlobal().info("exit");
    }

    private static int initVAO1() {
        float[] vertices = {
                // positions          // normals           // texture coords
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,

                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

                -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

                0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,

                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
        };
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
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
