package net.ddns.logick.shaders;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Shader {
    private int programID;

    public void use() {
        glUseProgram(programID);
    }

    public Shader(int programID) {
        this.programID = programID;
    }
    public void destroy(){
        glDeleteProgram(programID);
    }
}
