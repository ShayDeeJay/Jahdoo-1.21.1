package org.jahdoo.trial_nexus.level_manager;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.Arrays;
import java.util.List;

import static org.jahdoo.trial_nexus.level_manager.RoomData.RoomType.*;

public enum RoomData implements StringRepresentable {

    // ─── Rest Rooms ─────────────────────────────────────────────
    BAZAAR("bazaar", ColourHelpers.getAetherBlue(), REST_ROOM, true),
    CRYPT("crypt", ColourHelpers.getCooldownGreen(), REST_ROOM , true),
    SANCTUARY("sanctuary", ColourHelpers.getCosmicPurple(), REST_ROOM, true),
    EXIT("exit", ColourHelpers.getAbsorptionYellow(), REST_ROOM, true),

    // ─── Easy Rooms ───────────────────────────────────────────────────────────
    OAKVALE("oakvale", ColourHelpers.getRating5Green(), EASY_ROOM, false),
    LOGYARD("logyard", ColourHelpers.getRating5Green(), EASY_ROOM, false),
    ROTGROVE("rotgrove", ColourHelpers.getRating5Green(), EASY_ROOM, false),
    PASTURE("pasture", ColourHelpers.getRating5Green(), EASY_ROOM, false),

    // ─── Medium Rooms ─────────────────────────────────────────────────────────
    SUNDOWN("sundown", ColourHelpers.getRating4Yellow(), MEDIUM_ROOM, false),
    DEADWOOD("deadwood", ColourHelpers.getRating4Yellow(), MEDIUM_ROOM, false),
    OASIS("oasis", ColourHelpers.getRating4Yellow(), MEDIUM_ROOM, false),
    DUSTCAMP("dustcamp", ColourHelpers.getRating4Yellow(), MEDIUM_ROOM, false),

    // ─── Hard Rooms ───────────────────────────────────────────────────────────
    BLACKSTONE("blackstone", ColourHelpers.getRating3Orange(), HARD_ROOM, false),
    FROSTHOLD("frosthold", ColourHelpers.getRating3Orange(), HARD_ROOM, false),
    MINES("mines", ColourHelpers.getRating3Orange(), HARD_ROOM, false),
    RUINS("ruins", ColourHelpers.getRating3Orange(), HARD_ROOM, false),

    // ─── Extreme Rooms ────────────────────────────────────────────────────────
    HELLGATE("hellgate", ColourHelpers.getRating2Red(), EXTREME_ROOM, false),
    PYREWALK("pyrewalk", ColourHelpers.getRating2Red(), EXTREME_ROOM, false),
    ASHHAVEN("ashhaven", ColourHelpers.getRating2Red(), EXTREME_ROOM, false),
    SPOREFIRE("sporefire", ColourHelpers.getRating2Red(), EXTREME_ROOM, false),

    // ─── Boss Rooms ─────────────────────────────────────────────
    BOSS_CRUCIBLE("boss", ColourHelpers.getNegativeRed(), BOSS_ROOM, true),
    CHALLENGER_DOME("challenger", ColourHelpers.getPerkGreen(), MINI_BOSS_ROOM, true),

    // ─── Other ─────────────────────────────────────────────
    STARTER_ROOM("starting_room", ColourHelpers.getSubHeaderColour(), NON, false),
    BRIDGE("bridge", ColourHelpers.getSubHeaderColour(), NON, false),
    DEFAULT("empty", ColourHelpers.getSubHeaderColour(), NON, false);


    private final String roomId;
    private final int color;
    private final RoomType roomType;
    private final boolean hasKey;

    RoomData(
        String roomId,
        int color,
        RoomType RoomType,
        boolean hasKey
    ) {
        this.roomId = roomId;
        this.color = color;
        this.roomType = RoomType;
        this.hasKey = hasKey;
    }

    public static final List<RoomData> COMBAT_ROOMS = filterByRoom(EASY_ROOM, MEDIUM_ROOM, HARD_ROOM, EXTREME_ROOM);

    public RoomType getRoomType() {
        return roomType;
    }

    public Component getComponent() {
        return TextHelpers.withStyleComponent(TextHelpers.stringIdToName(this.roomId), this.color);
    }

    public int getColor() {
        return color;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public boolean isRoom(String roomId) {
        return this.roomId.equals(roomId);
    }

    public static boolean isEasy(String roomId) {
        return isType(roomId, EASY_ROOM);
    }

    public static boolean isType(String roomId, RoomType roomType) {
        return getById(roomId).getRoomType().equals(roomType);
    }

    public static boolean isCombatRoom(String roomId) {
        return COMBAT_ROOMS.stream().anyMatch(s -> s.roomId.equals(roomId));
    }

    public static Component getRandomBattleRoom(){
        return Helpers.listRandom(COMBAT_ROOMS).getComponent();
    }

    public static List<RoomData> getRestRooms(){
        return filterByRoom(REST_ROOM);
    }

    public static RoomData getById(String id) {
        for (var value : values()) {
            if(id.contains(value.roomId)) return value;
        }
        return DEFAULT;
    }

    public static RoomData getByItem(ItemStack stack){
        for (var value : values()) {
            if(stack.getDescriptionId().contains(value.roomId)) return value;
        }
        return DEFAULT;
    }

    public static boolean isLockKey(ItemStack stack){
        for (var value : values()) {
            var descriptionId = stack.getDescriptionId();
            if(descriptionId.contains(value.roomId) && value.hasKey) return true;
        }
        return false;
    }

    public static List<RoomData> filterByRoom(RoomType... roomTypes) {
        var allowed = Arrays.stream(roomTypes).toList();

        return Arrays.stream(values())
            .filter(data -> allowed.contains(data.getRoomType()))
            .toList();
    }

    public static String roomIcon(String id){
        var boss = "☠";
        var sanctuary = "\uD83E\uDDEA";
        var exit = "⚠";
        var bazaar = "⇵";
        var combat = "⚔";

        if(CHALLENGER_DOME.isRoom(id)) return boss;
        if(SANCTUARY.isRoom(id)) return sanctuary;
        if(EXIT.isRoom(id)) return exit;
        if(BAZAAR.isRoom(id)) return bazaar;

        return combat;
    }

    @Override
    public String getSerializedName() {
        return "jahdoo.room_data";
    }

    public enum RoomType {
        EASY_ROOM,
        MEDIUM_ROOM,
        HARD_ROOM,
        EXTREME_ROOM,
        REST_ROOM,
        MINI_BOSS_ROOM,
        BOSS_ROOM,
        NON
    }

}
