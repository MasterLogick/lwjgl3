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

import java.awt.*;
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
        glEnable(GL_STENCIL_TEST);
//        glEnable(GL_BLEND);
//        glEnable(GL_CULL_FACE);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        Texture diffuseMap = null;
        Texture specularMap = null;
        Shader lightShader = null;
        Shader shader = null;
        Shader fillingShader = null;
        try {
            diffuseMap = new Texture(ResourseManager.getResourseByPath("textures/container2.png"), GL_RGBA);
            specularMap = new Texture(ResourseManager.getResourseByPath("textures/container2_specular.png"), GL_RGBA);
            lightShader = ShaderLoader.loadShaderFromResourses("lightedShader");
            shader = ShaderLoader.loadShaderFromResourses("lightShader");
            fillingShader = ShaderLoader.loadShaderFromResourses("fillingShader");
        } catch (Exception e) {
            e.printStackTrace();
            window.shoulClose();
        }

        int vao = initVAO1();
        int vao1 = initVAO1();
        glClearColor(0.11f, 0.11f, 0.11f, 0.0f);
        diffuseMap.bindTo(GL_TEXTURE0);
        specularMap.bindTo(GL_TEXTURE1);
        Logger.getGlobal().info("initialised");
        float scale = 1.04f;
        while (!window.isWindowShouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
            Vec3 lightPos = new Vec3(2 * (float) Math.sqrt(1 - Math.cos(glfwGetTime()) * Math.cos(glfwGetTime())) * (float) Math.signum(Math.sin(glfwGetTime())), 0, (float) Math.cos(glfwGetTime()) * 2);
            input.processInput(window);
            try {
                shader.use(cam.projectionMatrix, cam.viewMatrix);
                shader.setModelMatrix(Mat4.MAT4_IDENTITY.translate(lightPos));
            } catch (Exception e) {
                e.printStackTrace();
            }
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 36);
            Vec3 lightColor = new Vec3(2.0f, 0.7f, 1.3f);
            try {
                lightShader.use(cam.projectionMatrix, cam.viewMatrix);
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
            glStencilFunc(GL_ALWAYS, 1, 0xFF);
            glStencilMask(0xFF);
            glDrawArrays(GL_TRIANGLES, 0, 36);
            glStencilFunc(GL_NOTEQUAL, 1, 0xff);
            glStencilMask(0x00);
            glDisable(GL_DEPTH_TEST);
            try {
                fillingShader.use(cam.projectionMatrix, cam.viewMatrix);
                fillingShader.setModelMatrix(new Mat4(scale, scale, scale));
                fillingShader.setColor("color", new Color(35, 115, 133));
            } catch (Exception e) {
                window.shoulClose();
                e.printStackTrace();
            }
            glDrawArrays(GL_TRIANGLES, 0, 36);
            glStencilMask(0xFF);
            glEnable(GL_DEPTH_TEST);
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
