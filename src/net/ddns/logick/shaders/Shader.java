package net.ddns.logick.shaders;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int programID;

    public void use() {
        glUseProgram(programID);
    }

    public Shader(int programID) {
        this.programID = programID;
    }

    public void destroy() {
        glDeleteProgram(programID);
    }

    public void setInt(String name, int val) {
        int loc = glGetUniformLocation(programID, name);
        glUniform1i(loc, val);
    }

    public void setFloat(String name, float val) {
        int loc = glGetUniformLocation(programID, name);
        glUniform1f(loc, val);
    }

    public void setVec3f(String name, FloatBuffer fb) {
        int loc = glGetUniformLocation(programID, name);
        glUniform3fv(loc, fb);
    }

    public void setVec3f(String name, float[] fb) {
        int loc = glGetUniformLocation(programID, name);
        glUniform3fv(loc, fb);
    }

    public void setVec4f(String name, FloatBuffer fb) {
        int loc = glGetUniformLocation(programID, name);
        glUniform4fv(loc, fb);
    }

    public void setVec4f(String name, float[] fb) {
        int loc = glGetUniformLocation(programID, name);
        glUniform4fv(loc, fb);
    }

    public void setMat4f(String name, boolean transpose, FloatBuffer fb) {
        int loc = glGetUniformLocation(programID, name);
        glUniformMatrix4fv(loc, transpose, fb);
    }
    public void setMat4f(String name, boolean transpose, float[] fb) {
        int loc = glGetUniformLocation(programID, name);
        glUniformMatrix4fv(loc, transpose, fb);
    }
}
