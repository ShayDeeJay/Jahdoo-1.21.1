package org.jahdoo.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {

    public static final String JAHDOO_CUSTOM_BIND = "key.assets.jahdoo.customBind";
    public static final String QUICK_SELECT_MENU = "key.assets.jahdoo.quick_select_menu";
    public static final String MAGNET_STATE = "key.assets.jahdoo.magnet_state";
    public static final String WAND_SLOT_1 = "key.assets.jahdoo.slot_1";
    public static final String WAND_SLOT_2 = "key.assets.jahdoo.slot_2";
    public static final String WAND_SLOT_3 = "key.assets.jahdoo.slot_3";
    public static final String WAND_SLOT_4 = "key.assets.jahdoo.slot_4";
    public static final String WAND_SLOT_5 = "key.assets.jahdoo.slot_5";
    public static final String WAND_SLOT_6 = "key.assets.jahdoo.slot_6";
    public static final String WAND_SLOT_7 = "key.assets.jahdoo.slot_7";
    public static final String WAND_SLOT_8 = "key.assets.jahdoo.slot_8";
    public static final String WAND_SLOT_9 = "key.assets.jahdoo.slot_9";
    public static final String WAND_SLOT_10 = "key.assets.jahdoo.slot_10";

    public static final KeyMapping QUICK_SELECT = new KeyMapping(QUICK_SELECT_MENU, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping MAGNET = new KeyMapping(MAGNET_STATE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_1A = new KeyMapping(WAND_SLOT_1, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_2A = new KeyMapping(WAND_SLOT_2, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_3A = new KeyMapping(WAND_SLOT_3, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_4A = new KeyMapping(WAND_SLOT_4, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_5A = new KeyMapping(WAND_SLOT_5, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_6A = new KeyMapping(WAND_SLOT_6, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_7A = new KeyMapping(WAND_SLOT_7, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_8A = new KeyMapping(WAND_SLOT_8, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_9A = new KeyMapping(WAND_SLOT_9, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    public static final KeyMapping WAND_SLOT_10A = new KeyMapping(WAND_SLOT_10, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);

}
