package net.prizowo.examplemod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping DESCEND_KEY = new KeyMapping(
            "key.examplemod.descend",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.categories.examplemod"
    );

    public static final KeyMapping SPEED_DOWN = new KeyMapping(
            "key.orbital.speed_down",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,

            "key.categories.orbital"
    );

    public static final KeyMapping SPEED_UP = new KeyMapping(
            "key.orbital.speed_up",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "key.categories.orbital"
    );
} 