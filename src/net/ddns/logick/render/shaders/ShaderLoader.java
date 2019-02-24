package net.ddns.logick.render.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL20.*;

public class ShaderLoader {
    public static int loadShader(String path, int shaderType) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(path));
        String shader = "";
        String tmp;
        while ((tmp = bf.readLine()) != null) {
            shader += tmp + "\n";
        }
        bf.close();
        return linkShader(shader, shaderType);
    }

    public static int linkShader(String shaderSource, int shaderType) {
        if (shaderType != GL_VERTEX_SHADER && shaderType != GL_FRAGMENT_SHADER)
            throw new IllegalArgumentException("shaderType can be only GL_VERTEX_SHADER or GL_FRAGMENT_SHADER");
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        checkShaderCompilation(shader);
        return shader;
    }

    public static Shader loadShaderProgram(String pathToVertexShader, String pathToFragmentShader) throws IOException {
        return linkShaderProgram(loadShader(pathToVertexShader, GL_VERTEX_SHADER), loadShader(pathToFragmentShader, GL_FRAGMENT_SHADER));
    }

    public static Shader linkShaderProgram(int vectorShader, int fragmentShader) {
        int program = glCreateProgram();
        glAttachShader(program, vectorShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        checkProgramLinking(program);
        glDeleteShader(vectorShader);
        glDeleteShader(fragmentShader);
        return new Shader(program);
    }

    public static void checkProgramLinking(int program) {
        String s = glGetProgramInfoLog(program);
        if (!s.isEmpty()) {
            Logger.getGlobal().severe("Program compilation: " + s);
        } else {
            Logger.getGlobal().info("Program compiled successfully");
        }
    }

    public static void checkShaderCompilation(int shader) {
        String s = glGetShaderInfoLog(shader);
        if (!s.isEmpty()) {
            Logger.getGlobal().severe("Shader compilation: " + s);
        } else {
            Logger.getGlobal().info("Shader compiled successfully");
        }
    }
}
