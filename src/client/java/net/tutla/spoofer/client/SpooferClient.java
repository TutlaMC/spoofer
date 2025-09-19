package net.tutla.spoofer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.tutla.spoofer.SpooferConfig;
import org.lwjgl.glfw.GLFW;

public class SpooferClient implements ClientModInitializer {

    private boolean wasPressed = false;

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long window = MinecraftClient.getInstance().getWindow().getHandle();
            boolean isPressed = GLFW.glfwGetKey(window, SpooferConfig.getToggleKey()) == GLFW.GLFW_PRESS;

            if (isPressed && !wasPressed) {
                MinecraftClient.getInstance().setScreen(SpooferConfigScreen.create(MinecraftClient.getInstance().currentScreen));
            }
            wasPressed = isPressed;
        });
    }
}
