package org.jahdoo.common.client;

import net.minecraft.client.KeyMapping;

import static com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM;
import static net.neoforged.neoforge.client.settings.KeyConflictContext.IN_GAME;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

public class KeyBinding {

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
    public static final String JAHDOO_CUSTOM_BIND = "key.assets.jahdoo.customBind";

    public static final String BLINK_KEY = "key.assets.jahdoo.blink";
    public static final String MAGNET_STATE = "key.assets.jahdoo.magnet_state";
    public static final String QUICK_SELECT_MENU = "key.assets.jahdoo.quick_select_menu";
    public static final String STAT_SCREEN_KEY = "key.assets.jahdoo.stat_screen";
    public static final String ABILITY_SCREEN_KEY = "key.assets.jahdoo.ability_screen";
    public static final String RUN_SCREEN_KEY = "key.assets.jahdoo.run_screen";
    public static final String ABILITY_MODIFICATION_KEY = "key.assets.jahdoo.ability_modification_screen";


    public static final KeyMapping WAND_SLOT_1A = map(WAND_SLOT_1);
    public static final KeyMapping WAND_SLOT_2A = map(WAND_SLOT_2);
    public static final KeyMapping WAND_SLOT_3A = map(WAND_SLOT_3);
    public static final KeyMapping WAND_SLOT_4A = map(WAND_SLOT_4);
    public static final KeyMapping WAND_SLOT_5A = map(WAND_SLOT_5);
    public static final KeyMapping WAND_SLOT_6A = map(WAND_SLOT_6);
    public static final KeyMapping WAND_SLOT_7A = map(WAND_SLOT_7);
    public static final KeyMapping WAND_SLOT_8A = map(WAND_SLOT_8);
    public static final KeyMapping WAND_SLOT_9A = map(WAND_SLOT_9);
    public static final KeyMapping WAND_SLOT_10A = map(WAND_SLOT_10);

    public static final KeyMapping BLINK = map(BLINK_KEY);
    public static final KeyMapping QUICK_SELECT = map(QUICK_SELECT_MENU);
    public static final KeyMapping STAT_SCREEN = map(STAT_SCREEN_KEY);
    public static final KeyMapping ABILITY_SCREEN = map(ABILITY_SCREEN_KEY);
    public static final KeyMapping RUN_SCREEN = map(RUN_SCREEN_KEY);
    public static final KeyMapping ABILITY_MODIFICATION_SCREEN = map(ABILITY_MODIFICATION_KEY);
    public static final KeyMapping MAGNET = map(MAGNET_STATE);

    public static KeyMapping map(String description){
        return new KeyMapping(description, IN_GAME, KEYSYM, GLFW_KEY_UNKNOWN, JAHDOO_CUSTOM_BIND);
    }
}
