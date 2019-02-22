package net.ddns.logick.render;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;

import java.util.logging.Logger;

public class Camera {
    public static Vec3 POSITION = new Vec3();
    public static Vec3 EYE = new Vec3(0, 0, -1);
    public static Vec3 UP = new Vec3(0, 1, 0);
    private boolean isVectorsChanged = false;
    private Vec3 position = new Vec3();
    private Vec3 eye = new Vec3(0, 0, -1);
    private Vec3 up = new Vec3(0, 1, 0);
    public Mat4 viewMatrix;
    public Mat4 projectionMatrix;
    private float speed = 0.5f;
    private double senetivity = 1d;
    private double prevXpos = 0;
    private double prevYpos = 0;
    private double yaw = 0;
    private double pitch = 0;

    public static Camera init(float fov, float aspect, float nearBorder, float farBorder) {
        Mat4 projectionMatrix = Matrices.perspective(fov, aspect, nearBorder, farBorder);
        Mat4 viewMatrix = Matrices.lookAt(POSITION, EYE, UP);
        return new Camera(projectionMatrix, viewMatrix);
    }

    private Camera(Mat4 projectionMatrix, Mat4 viewMatrix) {
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
    }

    public void update() {
        if (isVectorsChanged) {
            Logger.getGlobal().info("update");
            isVectorsChanged = false;
            viewMatrix = Matrices.lookAt(position, position.add(eye), up);
        }
    }

    public void mousePos(double xpos, double ypos) {
        isVectorsChanged = true;
        double deltaX = xpos - prevXpos;
        double deltaY = ypos - prevYpos;
        prevXpos = xpos;
        prevYpos = ypos;

    }

    public void moveForward() {
        isVectorsChanged = true;
        position = position.add(eye.horisontal().multiply(speed));
    }

    public void moveBack() {
        isVectorsChanged = true;
        position = position.add(eye.horisontal().getNegated().multiply(speed));
    }

    public void moveLeft() {
        isVectorsChanged = true;
        position = position.add(eye.cross(up).getUnitVector().horisontal().getNegated());
    }

    public void moveRight() {
        isVectorsChanged = true;
        position = position.add(eye.cross(up).getUnitVector().horisontal());
    }

    public void serPos(Vec3 new_pos) {
        isVectorsChanged = true;
        position = new_pos;
    }
}
