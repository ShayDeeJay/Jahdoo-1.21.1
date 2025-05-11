package org.jahdoo.ascension.utils;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;


public class Configuration {

    public static ModConfigSpec CLIENT_CONFIG;
    public static ModConfigSpec.BooleanValue CUSTOM_UI;
    public static ModConfigSpec.BooleanValue CUSTOM_UI_SHOW_MANA;
    public static ModConfigSpec.BooleanValue CUSTOM_UI_ALWAYS_SHOW_HUNGER;
    public static ModConfigSpec.BooleanValue CUSTOM_UI_ALWAYS_SHOW_XP;
    public static ModConfigSpec.BooleanValue CUSTOM_UI_ALWAYS_SHOW_ABILITY_BAR;
    public static ModConfigSpec.DoubleValue CUSTOM_UI_HEIGHT;
    public static ModConfigSpec.DoubleValue CUSTOM_UI_SCALE;
    public static ModConfigSpec.ConfigValue<List<? extends String>> SHOW_HOSTILE_ONLY;
    public static ModConfigSpec.BooleanValue BRIGHT_PARTICLE;
    public static ModConfigSpec.BooleanValue DISPLAY_DURABILITY_OVERLAY;
    public static ModConfigSpec.BooleanValue QUICK_SELECT;
    public static ModConfigSpec.BooleanValue LOCK_ON_TARGET;

    public static ModConfigSpec.DoubleValue HOTBAR_SCALED;
    public static ModConfigSpec.BooleanValue AUTO_HIDE_HOTBAR;
    
    static {
        var CLIENT_BUILDER = new ModConfigSpec.Builder();

        CUSTOM_UI = CLIENT_BUILDER.comment("Use custom mod UI or Minecraft's UI").define("customUI", false);
        CUSTOM_UI_HEIGHT = CLIENT_BUILDER.comment("Adjust custom UI height").defineInRange("height", -10.0, -1000, 1000);
        CUSTOM_UI_SCALE = CLIENT_BUILDER.comment("Adjust custom UI scale").defineInRange("scale", 1.0, 0, 5);
        CUSTOM_UI_ALWAYS_SHOW_HUNGER = CLIENT_BUILDER.comment("Always show hunger bar").define("show_hunger", true);
        CUSTOM_UI_ALWAYS_SHOW_XP = CLIENT_BUILDER.comment("Always show XP bar").define("show_xp", true);
        CUSTOM_UI_ALWAYS_SHOW_ABILITY_BAR = CLIENT_BUILDER.comment("Always show ability bar").define("show_ability", true);
        CUSTOM_UI_SHOW_MANA = CLIENT_BUILDER.comment("Always show stand alone mana bar").define("mana_bar", false);
        BRIGHT_PARTICLE = CLIENT_BUILDER.comment("Alternate particle render").define("particle_render", true);
        QUICK_SELECT = CLIENT_BUILDER.comment("Hold to keep open Quick Select menu").define("quickSelect", false);
        LOCK_ON_TARGET = CLIENT_BUILDER.comment("Allow player to lock on to nearest entity").define("lockOn", false);
        DISPLAY_DURABILITY_OVERLAY = CLIENT_BUILDER.comment("Overlay equipped item durability").define("durability", false);
        SHOW_HOSTILE_ONLY = CLIENT_BUILDER.comment("Type health bar visibility type", "Options: All, Hostile, Non, Instance Only")
            .defineList("entity_lights", writeConfig(), Configuration::validateMap);

        HOTBAR_SCALED = CLIENT_BUILDER.comment("Adjust hotbar UI scale").defineInRange("scale_hotbar", 1.0, 0, 2);
        AUTO_HIDE_HOTBAR = CLIENT_BUILDER.comment("Hide hotbar while not used").define("auto_hide_hotbar", false);

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }


    /** Ensure glyph_limits matches the expected regex pattern. */
    public static boolean validateMap(Object rawConfig) {
        return writeConfig().contains(rawConfig.toString());
    }

    /** Produces a list of tag=limit strings suitable for saving to the configuration. */
    public static List<String> writeConfig() {
        return List.of("All", "Hostile", "Non", "Instance Only");
    }

}
