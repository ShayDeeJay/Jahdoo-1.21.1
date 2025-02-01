package org.jahdoo.attachments.player_abilities;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.challenge.LevelGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jahdoo.registers.AttachmentRegister.*;
import static org.jahdoo.utils.ModHelpers.Random;

public class ChallengeLevelData implements AbstractAttachment {
    public List<UUID> activeMobs;
    public boolean isActive;
    public int killedMobs;
    public int maxMobsOnMap;
    public int maxMobs;
    public int round;
    public int maxRound;
    public String dimType; // New field

    public ChallengeLevelData() {}

    public ChallengeLevelData(
            List<UUID> activeMobs,
            boolean isActive,
            int killedMobs,
            int maxMobsOnMap,
            int maxMobs,
            int round,
            int maxRound,
            String dimType // New field in constructor
    ) {
        this.activeMobs = activeMobs;
        this.isActive = isActive;
        this.killedMobs = killedMobs;
        this.maxMobsOnMap = maxMobsOnMap;
        this.maxMobs = maxMobs;
        this.round = round;
        this.maxRound = maxRound;
        this.dimType = dimType; // Initialize new field
    }

    // Factory method to initialize with default values
    public static ChallengeLevelData DEFAULT = new ChallengeLevelData(new ArrayList<>(), false, 0, 0, 0, 0, 0, LevelGenerator.DimHandler.TRIAL); // Default dimension

    // Method to update the `isActive` field
    public ChallengeLevelData resetWithNewLevel() {
        return new ChallengeLevelData(new ArrayList<>(), this.isActive, 0, 0, 0, this.round + 1, this.maxRound, this.dimType);
    }

    // Method to update the `isActive` field
    public ChallengeLevelData updateActive(boolean isActive) {
        return new ChallengeLevelData(this.activeMobs, isActive, this.killedMobs, this.maxMobsOnMap, this.maxMobs, this.round, this.maxRound, this.dimType);
    }

    // Method to update the `killedMobs` field
    public ChallengeLevelData updateKilledMobs(int killedMobs) {
        return new ChallengeLevelData(this.activeMobs, this.isActive, killedMobs, this.maxMobsOnMap, this.maxMobs, this.round, this.maxRound, this.dimType);
    }

    // Method to update the `maxMobsOnMap` field
    public ChallengeLevelData updateMaxMobsOnMap(int maxMobsOnMap) {
        return new ChallengeLevelData(this.activeMobs, this.isActive, this.killedMobs, maxMobsOnMap, this.maxMobs, this.round, this.maxRound, this.dimType);
    }

    // Method to update the `round` field
    public ChallengeLevelData updateRound(int round) {
        return new ChallengeLevelData(this.activeMobs, this.isActive, this.killedMobs, this.maxMobsOnMap, this.maxMobs, round, this.maxRound, this.dimType);
    }

    // Method to update the `maxMobs` field
    public ChallengeLevelData updateMaxMobs(int maxMobs) {
        return new ChallengeLevelData(this.activeMobs, this.isActive, this.killedMobs, this.maxMobsOnMap, maxMobs, this.round, this.maxRound, this.dimType);
    }

    // Method to update the `maxRounds` field
    public ChallengeLevelData updateMaxRounds(int maxRounds) {
        return new ChallengeLevelData(this.activeMobs, this.isActive, this.killedMobs, this.maxMobsOnMap, this.maxMobs, this.round, maxRounds, this.dimType);
    }

    // Method to update the `dimension` field
    public ChallengeLevelData updateDimension(String dimension) {
        return new ChallengeLevelData(this.activeMobs, this.isActive, this.killedMobs, this.maxMobsOnMap, this.maxMobs, this.round, this.maxRound, dimension);
    }

    // Method to reset the round
    public ChallengeLevelData resetRound() {
        return new ChallengeLevelData(this.activeMobs, false, this.killedMobs, this.maxMobsOnMap, this.maxMobs, 0, this.maxRound, this.dimType);
    }

    // Method to update all fields
    public static ChallengeLevelData updateAll(List<UUID> activeMobs, boolean isActive, int killedMobs, int maxMobsOnMap, int maxMobs, int round, int maxRound, String dimension) {
        return new ChallengeLevelData(activeMobs, isActive, killedMobs, maxMobsOnMap, maxMobs, round, maxRound, dimension);
    }

    // Serialization method
    private void serialise(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(activeMobs, UUIDUtil.STREAM_CODEC);
        friendlyByteBuf.writeBoolean(isActive);
        friendlyByteBuf.writeInt(killedMobs);
        friendlyByteBuf.writeInt(maxMobsOnMap);
        friendlyByteBuf.writeInt(maxMobs);
        friendlyByteBuf.writeInt(round);
        friendlyByteBuf.writeInt(maxRound);
        friendlyByteBuf.writeUtf(dimType); // Serialize dimension
    }

    // Deserialization method
    private static ChallengeLevelData deserialise(FriendlyByteBuf friendlyByteBuf) {
        var activeMobs = friendlyByteBuf.readCollection(Lists::newArrayListWithCapacity, UUIDUtil.STREAM_CODEC);
        var isActive = friendlyByteBuf.readBoolean();
        var killedMobs = friendlyByteBuf.readInt();
        var maxMobsOnMap = friendlyByteBuf.readInt();
        var maxMobs = friendlyByteBuf.readInt();
        var round = friendlyByteBuf.readInt();
        var maxRound = friendlyByteBuf.readInt();
        var dimension = friendlyByteBuf.readUtf(); // Deserialize dimension
        return new ChallengeLevelData(activeMobs, isActive, killedMobs, maxMobsOnMap, maxMobs, round, maxRound, dimension);
    }

    // CODEC for serialization/deserialization
    public static final Codec<ChallengeLevelData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(UUIDUtil.CODEC).fieldOf("active_mobs").forGetter(ChallengeLevelData::activeMobs),
            Codec.BOOL.fieldOf("is_active").forGetter(ChallengeLevelData::isActive),
            Codec.INT.fieldOf("killed_mobs").forGetter(ChallengeLevelData::killedMobs),
            Codec.INT.fieldOf("max_mobs_on_map").forGetter(ChallengeLevelData::maxMobsOnMap),
            Codec.INT.fieldOf("max_mobs").forGetter(ChallengeLevelData::maxMobs),
            Codec.INT.fieldOf("round").forGetter(ChallengeLevelData::round),
            Codec.INT.fieldOf("maxRound").forGetter(ChallengeLevelData::maxRound),
            Codec.STRING.fieldOf("dimension").forGetter(ChallengeLevelData::dimension) // Add dimension to CODEC
        ).apply(instance, ChallengeLevelData::new)
    );

    // Save NBT data
    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag mobList = new CompoundTag();
        if (this.activeMobs != null) for (UUID mob : activeMobs) mobList.putUUID(mob.toString(), mob);
        nbt.put("active_mobs", mobList);
        nbt.putBoolean("is_active", isActive);
        nbt.putInt("killed_mobs", killedMobs);
        nbt.putInt("max_mobs_on_map", maxMobsOnMap);
        nbt.putInt("max_mobs", maxMobs);
        nbt.putInt("round", round);
        nbt.putInt("maxRound", maxRound);
        if(dimType != null) nbt.putString("dimension", dimType); // Save dimension
    }

    // Load NBT data
    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag mobList = nbt.getCompound("active_mobs");
        activeMobs = new ArrayList<>();
        for (var allKey : mobList.getAllKeys()) {
            var UUID = mobList.getUUID(allKey);
            activeMobs.add(UUID);
        }
        isActive = nbt.getBoolean("is_active");
        killedMobs = nbt.getInt("killed_mobs");
        maxMobsOnMap = nbt.getInt("max_mobs_on_map");
        maxMobs = nbt.getInt("max_mobs");
        round = nbt.getInt("round");
        maxRound = nbt.getInt("maxRound");
        dimType = nbt.getString("dimension"); // Load dimension
    }

    // Stream CODEC
    public static final StreamCodec<FriendlyByteBuf, ChallengeLevelData> STREAM_CODEC = StreamCodec.ofMember(
            ChallengeLevelData::serialise,
            ChallengeLevelData::deserialise
    );

    // Helper methods for active mobs
    public void addMob(UUID uuid) {
        this.activeMobs().add(uuid);
    }

    public void removeMob(UUID uuid) {
        this.activeMobs().remove(uuid);
    }

    public List<UUID> activeMobs() {
        return this.activeMobs;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public int killedMobs() {
        return this.killedMobs;
    }

    public int maxMobsOnMap() {
        return this.maxMobsOnMap;
    }

    public int maxMobs() {
        return this.maxMobs;
    }

    public int round() {
        return this.round;
    }

    public int maxRound() {
        return this.maxRound;
    }

    public String dimension() {
        return this.dimType; // Getter for dimension
    }

    // Static helper methods
    public static void resetSubRoundAltar(BlockEntity entity) {
        var current = ChallengeLevelData.getProperties(entity);
        entity.setData(CHALLENGE_ALTAR, current.resetWithNewLevel());
    }

    public static void resetAltar(BlockEntity entity) {
        var current = ChallengeLevelData.getProperties(entity);
        entity.setData(CHALLENGE_ALTAR, current.resetRound());
    }

    public int maxSpawnableMobs() {
        return Math.min(maxMobsOnMap, maxMobs - killedMobs);
    }

    public boolean isSubRoundActive(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        return altarData.maxMobs() != altarData.killedMobs();
    }

    public static void incrementKilledMobs(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var newData = altarData.updateKilledMobs(altarData.killedMobs + 1);
        entity.setData(CHALLENGE_ALTAR, newData);
        entity.setChanged();
    }

    public static void addEntity(BlockEntity entity, UUID uuid) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        altarData.addMob(uuid);
        entity.setChanged();
    }

    public static boolean isCompleted(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var maxRound = altarData.maxRound();
        var round = altarData.round();
        return maxRound > 0 && round == 0;
    }

    public static boolean isActive(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        return altarData.isActive();
    }

    public static void removeEntity(BlockEntity entity, UUID uuid) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        altarData.removeMob(uuid);
        entity.setChanged();
    }

    public static void flipActive(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var newData = altarData.updateActive(!altarData.isActive());
        entity.setData(CHALLENGE_ALTAR, newData);
        entity.setChanged();
    }

    public static void setRound(BlockEntity entity, int round) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var newRoundData = altarData.updateRound(round);
        entity.setData(CHALLENGE_ALTAR, newRoundData);
        entity.setChanged();
    }

    public static void maxRounds(BlockEntity entity, int maxRound) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var newData = altarData.updateMaxRounds(maxRound);
        entity.setData(CHALLENGE_ALTAR, newData);
        entity.setChanged();
    }

    public static void setDimension(ServerLevel entity, String dimension) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var newData = altarData.updateDimension(dimension);
        entity.setData(CHALLENGE_ALTAR, newData);
    }

    public static int getRound(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        return altarData.round();
    }

    public static int getMaxRounds(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        return altarData.maxRound();
    }

    public static String getDimension(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        return altarData.dimension();
    }

    public static void altarClickToStart(ChallengeAltarBlockEntity altarE) {
        ChallengeLevelData.setMaxMobs(altarE);
        ChallengeLevelData.setMaxMapMobs(altarE);
        ChallengeLevelData.flipActive(altarE);
    }

    public static void resetWithRound(ChallengeAltarBlockEntity altarE, int round, int maxRound) {
        ChallengeLevelData.setMaxMobs(altarE);
        ChallengeLevelData.setMaxMapMobs(altarE);
        ChallengeLevelData.flipActive(altarE);
        ChallengeLevelData.setRound(altarE, round);
        ChallengeLevelData.maxRounds(altarE, maxRound);
    }

    public static void nextSubRound(ChallengeAltarBlockEntity altarE, int round) {
        ChallengeLevelData.setMaxMobs(altarE);
        ChallengeLevelData.setMaxMapMobs(altarE);
        ChallengeLevelData.setRound(altarE, round);
    }

    public static void setMaxMobs(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var round = Math.max(2, altarData.round());
        var newMaxMobData = altarData.updateMaxMobs(round * 3 + Random.nextInt(Math.max(round / 5, 3)));
        entity.setData(CHALLENGE_ALTAR, newMaxMobData);
        entity.setChanged();
    }

    public static ChallengeLevelData newRound(int round, String dimType) {
        return new ChallengeLevelData(new ArrayList<>(), false, 0, 0, 0, round, round + 5, dimType);
    }

    public static void setMaxMapMobs(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var round = altarData.round();
        var newMaxMobMapData = altarData.updateMaxMobsOnMap(Math.max(4, Math.min(round / 2, 30)));
        entity.setData(CHALLENGE_ALTAR, newMaxMobMapData);
        entity.setChanged();
    }

    public static ChallengeLevelData getProperties(BlockEntity blockEntity) {
        return blockEntity.getData(CHALLENGE_ALTAR);
    }

    public static ChallengeLevelData getProperties(Level level) {
        return level.getData(CHALLENGE_ALTAR);
    }

    @Override
    public String toString() {
        return "ChallengeAltarData{" +
                "activeMobs=" + activeMobs +
                ", isActive=" + isActive +
                ", killedMobs=" + killedMobs +
                ", maxMobsOnMap=" + maxMobsOnMap +
                ", maxMobs=" + maxMobs +
                ", round=" + round +
                ", maxRound=" + maxRound +
                ", dimension=" + dimType + // Include dimension in toString
                '}';
    }
}
