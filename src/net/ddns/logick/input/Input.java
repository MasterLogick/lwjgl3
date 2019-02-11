package net.ddns.logick.input;

import org.lwjgl.glfw.GLFWKeyCallbackI;

import static org.lwjgl.glfw.GLFW.*;

public class Input implements GLFWKeyCallbackI {
    private boolean[] keys = new boolean[512];

    public Input() {
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
    public void processInput(){
        if(keys[GLFW_KEY_ESCAPE]){
            glfwWindowShouldClose(glfwGetCurrentContext());
        }
    }
}
