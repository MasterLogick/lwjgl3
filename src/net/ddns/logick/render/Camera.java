package net.ddns.logick.render;

import Tests.Main;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;

public class Camera {
    public static Vec3 POSITION = new Vec3();
    public static Vec3 EYE = new Vec3(0, 0, -1);
    public static Vec3 UP = new Vec3(0, 1, 0);
    public static double PITCH = -90d;
    public static double YAW = 0d;
    private boolean isVectorsChanged = false;
    private Vec3 position = new Vec3();
    private Vec3 eye = new Vec3(0, 0, -1);
    private Vec3 up = new Vec3(0, 1, 0);
    public Mat4 viewMatrix;
    public Mat4 projectionMatrix;
    private float speed = 0.15f;
    private double senetivity = 0.25;
    private double prevXpos = Main.SCR_WIDTH / 2;
    private double prevYpos = Main.SCR_HEIGHT / 2;
    private double yaw = YAW;
    private double pitch = PITCH;

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
            isVectorsChanged = false;
//            up = position.add(UP);
            viewMatrix = Matrices.lookAt(position, eye, up);
        }
    }

    public void mousePos(double xpos, double ypos) {
        isVectorsChanged = true;
        double deltaX = xpos - prevXpos;
        double deltaY = ypos - prevYpos;
        prevXpos = xpos;
        prevYpos = ypos;
        yaw -= deltaX * senetivity;
        pitch -= deltaY * senetivity;
        if (pitch > 0f)
            pitch = 1f;
        if (pitch < -180.0f)
            pitch = -179f;
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);
        eye = new Vec3((float) (Math.sin(radYaw) * Math.sin(radPitch)), (float) (Math.cos(radPitch)),
                (float) (Math.cos(radYaw) * Math.sin(radPitch))).getUnitVector();
    }

    public void moveForward() {
        isVectorsChanged = true;
        position = position.add(eye.horisontal().getUnitVector().multiply(speed));
    }

    public void moveBack() {
        isVectorsChanged = true;
        position = position.subtract(eye.horisontal().getUnitVector().multiply(speed));
    }

    public void moveLeft() {
        isVectorsChanged = true;
        position = position.subtract(eye.cross(up).getUnitVector().horisontal().multiply(speed));
    }

    public void moveRight() {
        isVectorsChanged = true;
        position = position.add(eye.cross(up).getUnitVector().horisontal().multiply(speed));
    }

    public void setPos(Vec3 new_pos) {
        isVectorsChanged = true;
        position = new_pos;
    }

    public void moveUp() {
        isVectorsChanged = true;
        position = position.add(UP.multiply(speed));
    }

    public void moveDown() {
        isVectorsChanged = true;
        position = position.subtract(UP.multiply(speed));
    }

    public void restoreDefaultPos() {
        position = POSITION;
        eye = EYE;
        up = UP;
        pitch = PITCH;
        yaw = YAW;
        Main.window.setMousePos(Main.SCR_WIDTH / 2, Main.SCR_HEIGHT / 2);
        prevXpos = Main.SCR_WIDTH / 2;
        prevYpos = Main.SCR_HEIGHT / 2;
        isVectorsChanged = true;
    }
}
