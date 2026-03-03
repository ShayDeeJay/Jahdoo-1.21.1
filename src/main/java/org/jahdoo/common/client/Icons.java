package org.jahdoo.common.client;

import net.minecraft.resources.ResourceLocation;

import static org.jahdoo.trial_nexus.utils.JahdooHelpers.res;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.texture;

public class Icons {

    private static final String PREFIX = "gui/";
    private static final String CURIO = "slot/";
    private static final String BUTTON_PREFIX = PREFIX + "gui_buttons/";
    private static final String SLOTS_PREFIX = PREFIX + "slots/";
    private static final String RARITY_TAG_PREFIX = PREFIX + "rarity_tags/";
    private static final String ITEMS_PREFIX = "item/";
    public static final String ABILITY_PREFIX = "ability_icons/";
    private static final String SKILLS = "skill/";


    public static final ResourceLocation CREATOR_TOP =
        texture(PREFIX + "creator_top");

    public static final ResourceLocation CURIO_MAGNET =
        res(CURIO + "magnet");

    public static final ResourceLocation CURIO_PROTECTOR  =
        res(CURIO + "protector");

    public static final ResourceLocation CURIO_SHIELD =
        res(CURIO + "shield");

    public static final ResourceLocation CURIO_RELIC =
        res(CURIO + "relic");

    public static final ResourceLocation ORE_MULTIPLIER =
        texture(PREFIX + "ore_icon");

    public static final ResourceLocation CODEX =
        texture(PREFIX + "codex");

    public static final ResourceLocation LOOT_POT_ICON =
        texture(PREFIX + "loot_pot_icon");

    public static final ResourceLocation HISTORY =
        texture(PREFIX + "history");

    public static final ResourceLocation COMMON_TAG =
        texture(RARITY_TAG_PREFIX + "common");

    public static final ResourceLocation RARE_TAG =
        texture(RARITY_TAG_PREFIX + "rare");

    public static final ResourceLocation EPIC_TAG =
        texture(RARITY_TAG_PREFIX + "epic");

    public static final ResourceLocation LEGENDARY_TAG =
        texture(RARITY_TAG_PREFIX + "legendary");

    public static final ResourceLocation MYTHIC_TAG =
        texture(RARITY_TAG_PREFIX + "mythic");

    public static final ResourceLocation UNIQUE_TAG =
        texture(RARITY_TAG_PREFIX + "unique");

    public static final ResourceLocation HEALTH_HOLDER =
        texture(PREFIX + "health/health_holder");

    public static final ResourceLocation HEALTH_HOLDER_ALLIED =
        texture(PREFIX + "health/health_holder_allied");

    public static final ResourceLocation HEALTH_BAR =
        texture(PREFIX + "health/health_bar");

    public static final ResourceLocation CENTER =
        texture(PREFIX + "center_view");

    public static final ResourceLocation NUMERIC =
        texture(PREFIX + "numeric");

    public static final ResourceLocation FROST_BOLTZ =
        texture(ABILITY_PREFIX + "frostbolts");

    public static final ResourceLocation PERMAFROST =
        texture(ABILITY_PREFIX + "permafrost");

    public static final ResourceLocation ICE_BOMB =
        texture(ABILITY_PREFIX + "ice_bomb");

    public static final ResourceLocation STORM_RUSH =
        texture(ABILITY_PREFIX + "storm_rush");

    public static final ResourceLocation FROST_ICON =
        texture("element_icons/frost_icon");

    public static final ResourceLocation INFERNO_ICON =
        texture("element_icons/inferno_icon");

    public static final ResourceLocation MYSTIC_ICON =
        texture("element_icons/mystic_icon");

    public static final ResourceLocation VITALITY_ICON =
        texture("element_icons/vitality_icon");

    public static final ResourceLocation UTILITY_ICON =
        texture("element_icons/utility_icon");

    public static final ResourceLocation MANA =
        texture("mob_effect/mana_pool");

    public static final ResourceLocation MANA_REGEN =
        texture("mob_effect/mana_regen");

    public static final ResourceLocation REPAIR =
        texture(PREFIX + "repair");

    public static final ResourceLocation RUNE =
        texture(PREFIX + "rune");

    public static final ResourceLocation DATA =
        texture(PREFIX + "data");

    public static final ResourceLocation SAFE =
        texture(PREFIX + "safe");

    public static final ResourceLocation CHEST_COMMON =
        texture(PREFIX + "chest_common");

    public static final ResourceLocation QUEST_CRATE =
        texture(PREFIX + "quest_crate_icon");

    public static final ResourceLocation CHEST_RARE =
        texture(PREFIX + "chest_rare");

    public static final ResourceLocation CHEST_LEGENDARY =
        texture(PREFIX + "chest_legendary");

    public static final ResourceLocation CHEST_MYTHIC =
        texture(PREFIX + "chest_mythic");

    public static final ResourceLocation XP_BAR =
        texture(PREFIX + "xp_bar");

    public static final ResourceLocation XP_BAR_CONTAINER =
        texture(PREFIX + "xp_bar_container");

    public static final ResourceLocation EASY =
        texture(PREFIX + "easy");

    public static final ResourceLocation MEDIUM =
        texture(PREFIX + "medium");

    public static final ResourceLocation HARD =
        texture(PREFIX + "hard");

    public static final ResourceLocation LOCK =
        texture(PREFIX + "lock");

    public static final ResourceLocation STAT =
        texture(PREFIX + "stat");

    public static final ResourceLocation ABILITY =
        texture(PREFIX + "ability");

    public static final ResourceLocation ROOMS_CLEARED =
        texture(PREFIX + "room");

    public static final ResourceLocation ABILITY_BACKGROUND =
        texture(PREFIX + "ability_background");

    public static final ResourceLocation SKILL_POINT =
        texture(ITEMS_PREFIX + "skill_point");

    public static final ResourceLocation BLANK_RUNE =
        texture(ITEMS_PREFIX + "runes/rune");

    public static final ResourceLocation ATTACK_SPEED =
        texture(ABILITY_PREFIX + "attack_speed");

    public static final ResourceLocation CLOCK =
        texture(ABILITY_PREFIX + "clock");

    public static final ResourceLocation TRIAL_EXPERIENCE =
        texture(ABILITY_PREFIX + "experience");

    public static final ResourceLocation ETERNAL_WIZARD =
        texture(ABILITY_PREFIX + "eternal_wizard");

    public static final ResourceLocation SKELETON =
        texture(ABILITY_PREFIX + "skeleton");

    public static final ResourceLocation HORDE =
        texture(ABILITY_PREFIX + "zombie");

    public static final ResourceLocation VOID_SPIDER =
        texture(ABILITY_PREFIX + "void_spider");

    public static final ResourceLocation INFERNO_CREEPER =
        texture(ABILITY_PREFIX + "inferno_creeper");

    public static final ResourceLocation KNOCKBACK_RESISTANCE =
        texture(ABILITY_PREFIX + "knockback_resist");

    public static final ResourceLocation GUI_BUTTON =
        texture(BUTTON_PREFIX + "gui_button");

    public static final ResourceLocation GUI_BUTTON_VITALITY =
        texture(BUTTON_PREFIX + "gui_button_vitality");

    public static final ResourceLocation GUI_BUTTON_INFERNO =
        texture(BUTTON_PREFIX + "gui_button_inferno");

    public static final ResourceLocation GUI_BUTTON_FROST =
        texture(BUTTON_PREFIX + "gui_button_frost");

    public static final ResourceLocation GUI_BUTTON_SKILL =
        texture(BUTTON_PREFIX + "gui_button_skill");

    public static final ResourceLocation TICK =
        texture(BUTTON_PREFIX + "gui_button_tick");

    public static final ResourceLocation MAGE_FLIGHT =
        texture(SKILLS + "mage_flight");

    public static final ResourceLocation BLINK =
        texture(SKILLS + "blink");

    public static final ResourceLocation CLIMBER =
        texture(SKILLS + "climber");

    public static final ResourceLocation DRIP_WALK =
        texture(SKILLS + "drip_walk");

    public static final ResourceLocation REBOUND =
        texture(SKILLS + "rebound");

    public static final ResourceLocation TRIPLE_JUMP =
        texture(SKILLS + "triple_jump");

    public static final ResourceLocation GUI_BUTTON_MYSTIC =
        texture(BUTTON_PREFIX + "gui_button_mystic");

    public static final ResourceLocation GUI_BUTTON_UTILITY =
        texture(BUTTON_PREFIX + "gui_button_utility");

    public static final ResourceLocation GUI_BUTTON_VITALITY_SQUARE =
        texture(BUTTON_PREFIX + "gui_button_vitality_square");

    public static final ResourceLocation GUI_BUTTON_INFERNO_SQUARE =
        texture(BUTTON_PREFIX + "gui_button_inferno_square");

    public static final ResourceLocation GUI_BUTTON_FROST_SQUARE =
        texture(BUTTON_PREFIX + "gui_button_frost_square");

    public static final ResourceLocation GUI_BUTTON_MYSTIC_SQUARE =
        texture(BUTTON_PREFIX + "gui_button_mystic_square");

    public static final ResourceLocation GUI_BUTTON_UTILITY_SQUARE =
        texture(BUTTON_PREFIX + "gui_button_utility_square");

    public static final ResourceLocation LOCKED_ABILITY =
        texture(BUTTON_PREFIX + "locked_ability");

    public static final ResourceLocation LOCKED_SKILL_CENTER =
        texture(BUTTON_PREFIX + "locked_skill_center");

    public static final ResourceLocation LOCKED_ABILITY_CENTER =
        texture(BUTTON_PREFIX + "locked_ability_center");

    public static final ResourceLocation GUI_BUTTON_SELECTED =
        texture(BUTTON_PREFIX + "gui_button_select");

    public static final ResourceLocation TEXT_BACKGROUND =
        texture(BUTTON_PREFIX + "gui_text_background");

    public static final ResourceLocation SELECTED_GUI_BUTTON_OVERLAY =
        texture(BUTTON_PREFIX + "gui_button_selected_overlay");

    public static final ResourceLocation DIRECTION_ARROW_BACK =
        texture(BUTTON_PREFIX + "gui_button_back_dark");

    public static final ResourceLocation DIRECTION_ARROW_FORWARD =
        texture(BUTTON_PREFIX + "gui_button_forward_dark");

    public static final ResourceLocation NORTH =
        texture(PREFIX + "direction/north");

    public static final ResourceLocation SOUTH =
        texture(PREFIX + "direction/south");

    public static final ResourceLocation EAST =
        texture(PREFIX + "direction/east");

    public static final ResourceLocation WEST =
        texture(PREFIX + "direction/west");

    public static final ResourceLocation UP =
        texture(BUTTON_PREFIX + "gui_button_up_green");

    public static final ResourceLocation DOWN =
        texture(BUTTON_PREFIX + "gui_button_down_red");

    public static final ResourceLocation COG =
        texture(BUTTON_PREFIX + "gui_button_cog_dark");

    public static final ResourceLocation POWER_ON =
        texture(BUTTON_PREFIX + "gui_button_power_on");

    public static final ResourceLocation POWER_OFF =
        texture(BUTTON_PREFIX + "gui_button_power_off");

    public static final ResourceLocation GUI_GENERAL_SLOT =
        texture(BUTTON_PREFIX + "gui_general_slot");

    public static final ResourceLocation GUI_RUNE_SLOT =
        texture(BUTTON_PREFIX + "gui_rune_slot");

    public static final ResourceLocation GUI_AUGMENT_SLOT =
        texture(SLOTS_PREFIX + "slot_v2");

    public static final ResourceLocation HOVERED_SLOT_OVERLAY =
        texture(PREFIX + "hovered_slot_overlay");

    public static final ResourceLocation GUI_INVENTORY_OVERLAY =
        texture(BUTTON_PREFIX + "gui_inventory_overlay");

    public static final ResourceLocation BLANK =
        texture(BUTTON_PREFIX + "blank");

    public static final ResourceLocation CHAINED =
        texture(BUTTON_PREFIX + "chained");

    public static final ResourceLocation UNCHAINED =
        texture(BUTTON_PREFIX + "un-chained");

    public static final ResourceLocation UPGRADE =
        texture(BUTTON_PREFIX + "gui_button_upgrade");

    public static final ResourceLocation UPGRADE_DISABLED =
        texture(BUTTON_PREFIX + "gui_button_upgrade_disabled");

    public static final ResourceLocation GUI_ITEM_SLOT =
        texture(SLOTS_PREFIX + "gui_item_slot");

    public static final ResourceLocation AUGMENT_CORE =
        texture(SLOTS_PREFIX + "augment_core_slot");

    public static final ResourceLocation ADVANCED_AUGMENT_CORE =
        texture(SLOTS_PREFIX + "advanced_augment_core_slot");

    public static final ResourceLocation AUGMENT_HYPER_CORE =
        texture(SLOTS_PREFIX + "augment_hyper_core_slot");

    public static final ResourceLocation INFORMATION =
        texture(BUTTON_PREFIX + "gui_button_information");

    public static final ResourceLocation INVENTORY =
        texture(BUTTON_PREFIX + "gui_button_inventory");

    public static final ResourceLocation REFRESH =
        texture(BUTTON_PREFIX + "gui_button_refresh");

    public static final ResourceLocation CLOSE =
        texture(BUTTON_PREFIX + "gui_button_close_dark");

    public static final ResourceLocation BEZEL_1 =
        texture(PREFIX + "bezels/bezel_1");

    public static final ResourceLocation BEZEL_2 =
        texture(PREFIX + "bezels/bezel_2");

    public static final ResourceLocation BEZEL_3 =
        texture(PREFIX + "bezels/bezel_3");

    public static final ResourceLocation BEZEL_4 =
        texture(PREFIX + "bezels/bezel_4");

    public static final ResourceLocation TYPE_OVERLAY =
        texture(PREFIX + "type_overlay");

    public static final ResourceLocation MANA_CONTAINER =
        texture(PREFIX + "mana_bar_overlay");

    public static final ResourceLocation MANA_LEVEL_BAR =
        texture(PREFIX + "mana_bar_type");

    public static final ResourceLocation IN_WAND =
        texture(PREFIX + "in_wand");

    public static final ResourceLocation WAND_GUI =
        texture(PREFIX + "wand_gui");

    public static final ResourceLocation IN_INVENTORY =
        texture(PREFIX + "in_inventory");

    public static final ResourceLocation CHAMPIONS_CROWN =
        texture(PREFIX + "champions_crown");

    public static final ResourceLocation CHALLENGER =
        texture(PREFIX + "challenger");

    public static final ResourceLocation BRONZE_COIN =
        texture(ITEMS_PREFIX + "coins/coin");

    public static final ResourceLocation SILVER_COIN =
        texture(ITEMS_PREFIX + "coins/coin1");

    public static final ResourceLocation GOLD_COIN =
        texture(ITEMS_PREFIX + "coins/coin2");

    public static final ResourceLocation PLATINUM_COIN =
        texture(ITEMS_PREFIX + "coins/coin3");



}

