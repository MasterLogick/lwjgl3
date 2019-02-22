package net.ddns.logick.render.shaders;

import com.hackoeur.jglm.Mat4;

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

    public void setModelMatrix(Mat4 modelMatrix) {
        int loc = glGetUniformLocation(programID, "modelMatrix");
        glUniformMatrix4fv(loc, false, modelMatrix.getBuffer());
    }

    public void setViewMatrix(Mat4 viewMatrix) {
        int loc = glGetUniformLocation(programID, "viewMatrix");
        glUniformMatrix4fv(loc, false, viewMatrix.getBuffer());
    }

    public void setProjectionMatrix(Mat4 projectionMatrix) {
        int loc = glGetUniformLocation(programID, "projectionMatrix");
        glUniformMatrix4fv(loc, false, projectionMatrix.getBuffer());
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

    public void setMat4f(String name, boolean transpose, Mat4 matrix) {
        int loc = glGetUniformLocation(programID, name);
        glUniformMatrix4fv(loc, transpose, matrix.getBuffer());
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
