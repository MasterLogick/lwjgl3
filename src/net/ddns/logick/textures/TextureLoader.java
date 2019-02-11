package net.ddns.logick.textures;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class TextureLoader {
    public static ByteBuffer loadTextureData(String path, IntBuffer width, IntBuffer heigth, IntBuffer components, int imageChanels) {
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(path, width, heigth, components, imageChanels);
        if (image == null) {
            throw new RuntimeException("Failed to load a texture file!" + stbi_failure_reason());
        }
        return image;
    }
}
