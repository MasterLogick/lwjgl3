package net.ddns.logick.render.textures;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class TextureLoader {
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
}
