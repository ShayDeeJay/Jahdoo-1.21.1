package org.jahdoo.ascension.utils;

import net.neoforged.neoforge.common.ModConfigSpec;


public class Configuration {

    public static ModConfigSpec CLIENT_CONFIG;
    public static ModConfigSpec.BooleanValue CUSTOM_UI;
    public static ModConfigSpec.BooleanValue CUSTOM_UI_SHOW_MANA;
    public static ModConfigSpec.BooleanValue CUSTOM_UI_ALWAYS_SHOW_HUNGER;
    public static ModConfigSpec.BooleanValue CUSTOM_UI_ALWAYS_SHOW_XP;
    public static ModConfigSpec.DoubleValue CUSTOM_UI_HEIGHT;
    public static ModConfigSpec.DoubleValue CUSTOM_UI_SCALE;

    public static ModConfigSpec.BooleanValue QUICK_SELECT;
    public static ModConfigSpec.BooleanValue LOCK_ON_TARGET;

    
    static {
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        CUSTOM_UI = CLIENT_BUILDER.comment("Use custom mod UI or Minecraft's UI").define("customUI", false);
        CUSTOM_UI_HEIGHT = CLIENT_BUILDER.comment("Adjust custom UI height").defineInRange("height", -10.0, -1000, 1000);
        CUSTOM_UI_SCALE = CLIENT_BUILDER.comment("Adjust custom UI scale").defineInRange("scale", 1.0, 0, 5);
        CUSTOM_UI_ALWAYS_SHOW_HUNGER = CLIENT_BUILDER.comment("Always show hunger bar").define("show_hunger", true);
        CUSTOM_UI_ALWAYS_SHOW_XP = CLIENT_BUILDER.comment("Always show XP bar").define("show_xp", true);
        CUSTOM_UI_SHOW_MANA = CLIENT_BUILDER.comment("Always show stand alone mana bar").define("mana_bar", false);

        QUICK_SELECT = CLIENT_BUILDER.comment("Hold to keep open Quick Select menu").define("quickSelect", false);
        LOCK_ON_TARGET = CLIENT_BUILDER.comment("Allow player to lock on to nearest entity").define("lockOn", false);

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

}
