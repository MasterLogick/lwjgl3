package net.ddns.logick.input;

import net.ddns.logick.render.Camera;
import net.ddns.logick.windows.GLFWwindow;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import static org.lwjgl.glfw.GLFW.*;

public class Input implements GLFWKeyCallbackI {
    private boolean[] keys = new boolean[512];
    private Camera camera;
    public GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            camera.mousePos(xpos, ypos);
        }
    };

    public Input(Camera camera) {
        this.camera = camera;
        for (int i = 0; i < 512; i++) {
            keys[i] = false;
        }
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_RELEASE)
            keys[key] = false;
        else
            keys[key] = true;
    }

    public void processInput(GLFWwindow window) {
        if (keys[GLFW_KEY_ESCAPE]) {
            window.shoulClose();
        }
        if (keys[GLFW_KEY_C]) {
            camera.restoreDefaultPos();
        }
        if (keys[GLFW_KEY_W]) {
            camera.moveForward();
        }
        if (keys[GLFW_KEY_S]) {
            camera.moveBack();
        }
        if (keys[GLFW_KEY_A]) {
            camera.moveLeft();
        }
        if (keys[GLFW_KEY_D]) {
            camera.moveRight();
        }
        if (keys[GLFW_KEY_SPACE]) {
            camera.moveUp();
        }
        if (keys[GLFW_KEY_LEFT_SHIFT]) {
            camera.moveDown();
        }
        camera.update();
    }
}
