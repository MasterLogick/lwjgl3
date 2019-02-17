package net.ddns.logick.render;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;

public class Camera {
    private static boolean isPositionChanged = false;
    private static Vec3 position = new Vec3();
    private static Vec3 eye = new Vec3(0, 0, -1);
    private static Vec3 up = new Vec3(0, 1, 0);
    public static Mat4 viewMatrix = null;
    public static Mat4 projectionMatrix = null;

    public static void init(float fov, float aspect, float nearBorder, float farBorder) {
        projectionMatrix = Matrices.perspective(fov, aspect, nearBorder, farBorder);
        viewMatrix = Matrices.lookAt(position, eye, up);
    }

}
