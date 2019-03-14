package net.ddns.logick.render.shaders;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int programID;

    public void use(Mat4 projectionMatrix, Mat4 viewMatrix) throws Exception {
        glUseProgram(programID);
        setProjectionMatrix(projectionMatrix);
        setViewMatrix(viewMatrix);
    }

    public void use() {
        glUseProgram(programID);
    }

    public Shader(int programID) {
        this.programID = programID;
    }

    public void destroy() {
        glDeleteProgram(programID);
    }

    public void setModelMatrix(Mat4 modelMatrix) throws Exception {
        int loc = glGetUniformLocation(programID, "modelMatrix");
        if (loc == -1) throw new Exception("Invalid uniform variable's name: modelMatrix");
        glUniformMatrix4fv(loc, false, modelMatrix.getBuffer());
    }

    public void setViewMatrix(Mat4 viewMatrix) throws Exception {
        int loc = glGetUniformLocation(programID, "viewMatrix");
        if (loc == -1) throw new Exception("Invalid uniform variable's name: viewMatrix");
        glUniformMatrix4fv(loc, false, viewMatrix.getBuffer());
    }

    public void setProjectionMatrix(Mat4 projectionMatrix) throws Exception {
        int loc = glGetUniformLocation(programID, "projectionMatrix");
        if (loc == -1) throw new Exception("Invalid uniform variable's name: projectionMatrix");
        glUniformMatrix4fv(loc, false, projectionMatrix.getBuffer());
    }

    public void setColor(String name, Color val) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform3fv(loc, new Vec3(val.getRed(), val.getGreen(), val.getBlue()).getUnitVector().getBuffer());
    }

    public void setInt(String name, int val) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform1i(loc, val);
    }

    public void setFloat(String name, float val) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform1f(loc, val);
    }

    public void setVec3f(String name, FloatBuffer fb) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform3fv(loc, fb);
    }

    public void setVec3f(String name, float[] fb) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform3fv(loc, fb);
    }

    public void setVec4f(String name, FloatBuffer fb) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform4fv(loc, fb);
    }

    public void setVec4f(String name, float[] fb) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform4fv(loc, fb);
    }

    public void setMat4f(String name, boolean transpose, Mat4 matrix) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniformMatrix4fv(loc, transpose, matrix.getBuffer());
    }

    public void setMat4f(String name, boolean transpose, FloatBuffer fb) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniformMatrix4fv(loc, transpose, fb);
    }

    public void setMat4f(String name, boolean transpose, float[] fb) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniformMatrix4fv(loc, transpose, fb);
    }

    public void setVec3f(String name, Vec3 vec3) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform3fv(loc, vec3.getBuffer());
    }

    public void setMat3f(String name, boolean transpose, Mat4 mat3) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniformMatrix3fv(programID, transpose, mat3.getBuffer());
    }

    public void setBoolean(String name, boolean val) throws Exception {
        int loc = glGetUniformLocation(programID, name);
        if (loc == -1) throw new Exception("Invalid uniform variable's name: " + name);
        glUniform1i(loc, val ? 1 : 0);
    }
}
