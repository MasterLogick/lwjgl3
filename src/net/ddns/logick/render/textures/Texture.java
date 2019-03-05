package net.ddns.logick.render.textures;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private int textureId;
    private static int GL_TEXTURE_WRAP_S = GL_REPEAT;
    private static int GL_TEXTURE_WRAP_T = GL_REPEAT;
    private static int GL_TEXTURE_MIN_FILTER = GL_NEAREST;
    private static int GL_TEXTURE_MAG_FILTER = GL_NEAREST;
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

    public Texture(InputStream inputStream, int imageType) throws IOException {
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_TEXTURE_WRAP_S);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_TEXTURE_WRAP_T);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL_TEXTURE_MIN_FILTER);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL_TEXTURE_MAG_FILTER);
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        ByteBuffer data = loadTextureData(inputStream, width, height, components, 0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width.get(), height.get(), 0, imageType, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);
        stbi_image_free(data);
        glBindTexture(GL_TEXTURE_2D, 0);
        GL_TEXTURE_WRAP_S = GL_REPEAT;
        GL_TEXTURE_WRAP_T = GL_REPEAT;
        GL_TEXTURE_MIN_FILTER = GL_NEAREST;
        GL_TEXTURE_MAG_FILTER = GL_NEAREST;
    }

    public static ByteBuffer loadTextureData(InputStream inputStream, IntBuffer width, IntBuffer heigth, IntBuffer components, int imageChanels) throws IOException {
        stbi_set_flip_vertically_on_load(true);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        ByteBuffer bb = BufferUtils.createByteBuffer(bytes.length);
        bb.put(bytes);
        bb.flip();
        ByteBuffer image = stbi_load_from_memory(bb, width, heigth, components, imageChanels);
        if (image == null) {
            throw new RuntimeException("Failed to load a texture file!" + stbi_failure_reason());
        }
        return image;
    }

    public void use() {
        glBindTexture(GL_TEXTURE_2D, textureId);
        Logger.getGlobal().info("Used texture with id: " + textureId);
    }
}
