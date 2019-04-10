package Tests;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import net.ddns.logick.input.Input;
import net.ddns.logick.render.Camera;
import net.ddns.logick.render.shaders.Shader;
import net.ddns.logick.render.shaders.ShaderLoader;
import net.ddns.logick.render.textures.Texture;
import net.ddns.logick.windows.GLFWWindow;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import res.ResourseManager;

import java.awt.*;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

public class MainOpenGL {
    public static int SCR_WIDTH = 1300;
    public static int SCR_HEIGHT = 800;
    public static GLFWWindow window;
    private static int a = 1000;
    public static final int MSAA_LEVEL = 4;
    public static final float GAMMA = 2.2f;
    public static final float EXPOSURE = 1.0f;

    public static void main(String[] args) {
        initGLFW();
        Camera cam = Camera.init(45f, ((float) SCR_WIDTH) / ((float) SCR_HEIGHT), 0.1f, 100f);
        Input input = new Input(cam);
        window = new GLFWWindow(SCR_WIDTH, SCR_HEIGHT, "Test");
        window.setCurrient();
        window.setVSync(1);
        window.showWindow();
//        window.grabMouse();
        window.setMousePos(SCR_WIDTH / 2, SCR_HEIGHT / 2);
        window.setMouseCallbacks(input.cursorPosCallback);
        window.setKeyCallbacks(input);
        window.setResizable(false);
        GL.createCapabilities();
        GLUtil.setupDebugMessageCallback();
        glEnable(GL_DEPTH_TEST);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);


        Texture diffuseMap = null;
        Texture specularMap = null;
        Shader shader = null;
        Shader lightShader = null;
        Shader fillingShader = null;
        Shader screenShader = null;
        try {
            diffuseMap = new Texture(ResourseManager.getResourseByPath("textures/container2.png"), GL_RGBA);
            specularMap = new Texture(ResourseManager.getResourseByPath("textures/container2_specular.png"), GL_RGBA);
            lightShader = ShaderLoader.loadShaderFromResourses("lightedShader");
            shader = ShaderLoader.loadShaderFromResourses("lightShader");
            fillingShader = ShaderLoader.loadShaderFromResourses("fillingShader");
            screenShader = ShaderLoader.loadShaderFromResourses("postProcessingShader");
            shader.bindMatricesToGlobalPoint("Matrices");
//            fillingShader.bindMatricesToGlobalPoint("Matrices");
            lightShader.bindMatricesToGlobalPoint("Matrices");
        } catch (Exception e) {
            e.printStackTrace();
            window.shoulClose();
        }

        int blockVBO;
        blockVBO = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, blockVBO);
        glBufferData(GL_UNIFORM_BUFFER, 2L * Mat4.BYTES + 2 * Float.BYTES, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, cam.projectionMatrix.getBuffer());
        glBufferSubData(GL_UNIFORM_BUFFER, 2L * Mat4.BYTES, new float[]{GAMMA});
        glBufferSubData(GL_UNIFORM_BUFFER, 2L * Mat4.BYTES + Float.BYTES, new float[]{EXPOSURE});
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        glBindBufferRange(GL_UNIFORM_BUFFER, Shader.MATRICES_BINDING_POINT, blockVBO, 0, 2 * Mat4.BYTES + 2 * Float.BYTES);

        float[] quadVertices = {
                // positions   // texCoords
                -1.0f, 1.0f, 0.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 1.0f, 0.0f,

                -1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        };
        int quadVAO, quadVBO;
        quadVAO = glGenVertexArrays();
        quadVBO = glGenBuffers();
        glBindVertexArray(quadVAO);
        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        int frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        int textureColorBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, textureColorBuffer);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, MSAA_LEVEL, GL_RGBA32F, SCR_WIDTH, SCR_HEIGHT, true);
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, textureColorBuffer, 0);
        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, MSAA_LEVEL, GL_DEPTH24_STENCIL8, SCR_WIDTH, SCR_HEIGHT);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Logger.getGlobal().throwing("", "", new Exception("FrameBuffer isn't ready"));
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        int intermediateFBO;
        intermediateFBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, intermediateFBO);
        // create a color attachment texture
        int screenTexture;
        screenTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, screenTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(SCR_WIDTH * SCR_HEIGHT * 4));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, screenTexture, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        int vao = initVAO1();
        int vao1 = initVAO1();

        Logger.getGlobal().info("initialised");
        float scale = 1.04f;
        while (!window.isWindowShouldClose()) {

            glBindBuffer(GL_UNIFORM_BUFFER, blockVBO);
            glBufferSubData(GL_UNIFORM_BUFFER, Mat4.BYTES, cam.viewMatrix.getBuffer());
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
            glEnable(GL_STENCIL_TEST);

            glClearColor(0.11f, 0.11f, 0.11f, 1f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            diffuseMap.bindTo(GL_TEXTURE0);
            specularMap.bindTo(GL_TEXTURE1);

            Vec3 lightPos = new Vec3(2 * (float) Math.sqrt(1 - Math.cos(glfwGetTime()) * Math.cos(glfwGetTime())) * (float) Math.signum(Math.sin(glfwGetTime())), 0, (float) Math.cos(glfwGetTime()) * 2);

            input.processInput(window);

            shader.use();
            try {
                shader.setModelMatrix(Mat4.MAT4_IDENTITY.translate(lightPos));
            } catch (Exception e) {
                e.printStackTrace();
            }
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 36);
//            glDrawArraysInstanced(GL11C.GL_TRIANGLES, 0, 36, input.a);

            Vec3 lightColor = new Vec3(1, 1, 1);
            lightShader.use();
            try {
                lightShader.setVec3f("light.position", lightPos);
                lightShader.setVec3f("light.specular", new float[]{1.0f, 1.0f, 1.0f});
                lightShader.setVec3f("light.ambient", lightColor.multiply(1f));
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
            glDepthMask(false);
            fillingShader.use();
            try {
                fillingShader.setModelMatrix(new Mat4(scale, scale, scale));
                fillingShader.setColor("color", new Color(35, 115, 133));
            } catch (Exception e) {
                window.shoulClose();
                e.printStackTrace();
            }
            glDrawArrays(GL_TRIANGLES, 0, 36);

            glStencilMask(0xFF);

            glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, intermediateFBO);
            glBlitFramebuffer(0, 0, SCR_WIDTH, SCR_HEIGHT, 0, 0, SCR_WIDTH, SCR_HEIGHT, GL_COLOR_BUFFER_BIT, GL_NEAREST);

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_STENCIL_TEST);
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            screenShader.use();
            try {
                screenShader.setInt("screenTexture", 0);
                screenShader.setBoolean("isColorsInverted", input.flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
            glActiveTexture(GL_TEXTURE0);
            glBindVertexArray(quadVAO);
            glBindTexture(GL_TEXTURE_2D, screenTexture);
            glDrawArrays(GL_TRIANGLES, 0, 6);
            window.swapBuffers();
            glfwPollEvents();
        }
        Logger.getGlobal().info("game loop was stopped");
        window.destroy();
        lightShader.destroy();
        fillingShader.destroy();
        shader.destroy();
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
        /*float[] arr = new float[a * 3];
        for (int i = 0; i < a; i++) {
            arr[3 * i] = i % 10;
            arr[3 * i + 1] = i / 10 % 10;
            arr[3 * i + 2] = i / 100 % 10;
        }*/
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
//        int instVBO = glGenBuffers();
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
//        glBindBuffer(GL_ARRAY_BUFFER, instVBO);
//        glBufferData(GL_ARRAY_BUFFER, arr, GL_STATIC_DRAW);
//        glVertexAttribPointer(3, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
//        glEnableVertexAttribArray(3);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
//        glVertexAttribDivisor(3, 1);
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
