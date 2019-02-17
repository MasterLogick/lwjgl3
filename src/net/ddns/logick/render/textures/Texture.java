package net.ddns.logick.render.textures;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;

public class Texture {
    private static int textureId;
    private static int GL_TEXTURE_WRAP_S = GL_REPEAT;
    private static int GL_TEXTURE_WRAP_T = GL_REPEAT;
    private static int GL_TEXTURE_MIN_FILTER = GL11.GL_TEXTURE_MIN_FILTER;
    private static int GL_TEXTURE_MAG_FILTER = GL11.GL_TEXTURE_MAG_FILTER;
    private static float[] GL_TEXTURE_BORDER_COLOR = new float[4];

    public static void preset(int option, int val) {
        switch (option) {
            case GL11.GL_TEXTURE_MIN_FILTER:
                GL_TEXTURE_MIN_FILTER = val;
                break;
            case GL11.GL_TEXTURE_MAG_FILTER:
                GL_TEXTURE_MAG_FILTER = val;
                break;
            case GL11.GL_TEXTURE_WRAP_S:
                GL_TEXTURE_WRAP_S = val;
                break;
            case GL11.GL_TEXTURE_WRAP_T:
                GL_TEXTURE_WRAP_T = val;
                break;
        }
    }

    public static void preset(int option, float[] val) {
        switch (option) {
            case GL11.GL_TEXTURE_BORDER_COLOR:
                GL_TEXTURE_BORDER_COLOR = val;
                break;
        }
    }

    public Texture(String path) {
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_TEXTURE_WRAP_S);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_TEXTURE_WRAP_T);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL_TEXTURE_MIN_FILTER);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL_TEXTURE_MAG_FILTER);
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        ByteBuffer data = TextureLoader.loadTextureData(path, width, height, components, 0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(), height.get(), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);
        stbi_image_free(data);
        glBindTexture(GL_TEXTURE_2D, 0);
        GL_TEXTURE_WRAP_S = GL_REPEAT;
        GL_TEXTURE_WRAP_T = GL_REPEAT;
        GL_TEXTURE_MIN_FILTER = GL11.GL_TEXTURE_MIN_FILTER;
        GL_TEXTURE_MAG_FILTER = GL11.GL_TEXTURE_MAG_FILTER;
    }

    public void use() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }
}
