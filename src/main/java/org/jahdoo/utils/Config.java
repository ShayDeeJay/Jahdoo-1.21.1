package org.jahdoo.utils;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config  {

    public static ModConfigSpec SERVER_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;
    public static ModConfigSpec.BooleanValue CUSTOM_UI;
    public static ModConfigSpec.BooleanValue QUICK_SELECT;
    public static ModConfigSpec.BooleanValue LOCK_ON_TARGET;

    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
        CUSTOM_UI = CLIENT_BUILDER.comment("Use custom mod UI or Minecraft's UI").define("customUI", false);
        QUICK_SELECT = CLIENT_BUILDER.comment("Hold to keep open Quick Select menu").define("quickSelect", false);
        LOCK_ON_TARGET = CLIENT_BUILDER.comment("Allow player to lock on to nearest entity").define("lockOn", false);
        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

}
