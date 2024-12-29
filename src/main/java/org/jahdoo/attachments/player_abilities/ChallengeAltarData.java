package org.jahdoo.attachments.player_abilities;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jahdoo.registers.AttachmentRegister.*;
import static org.jahdoo.utils.ModHelpers.Random;

public class ChallengeAltarData implements AbstractAttachment {
    public List<UUID> activeMobs;
    public boolean isActive;
    public int killedMobs;
    public int maxMobsOnMap;
    public int maxMobs;
    public int round;

    public ChallengeAltarData() {}

    public ChallengeAltarData(
        List<UUID> activeMobs,
        boolean isActive,
        int killedMobs,
        int maxMobsOnMap,
        int maxMobs,
        int round
    ) {
        this.activeMobs = activeMobs;
        this.isActive = isActive;
        this.killedMobs = killedMobs;
        this.maxMobsOnMap = maxMobsOnMap;
        this.maxMobs = maxMobs;
        this.round = round;
    }

    // Factory method to initialize with default values
    public static ChallengeAltarData DEFAULT = new ChallengeAltarData(new ArrayList<>(), false, 0, 0, 0, 0);

    // Method to update the `isActive` field
    public ChallengeAltarData resetWithNewLevel() {
        return new ChallengeAltarData(new ArrayList<>(), false, 0, 0, 0, this.round + 1);
    }

    // Method to update the `isActive` field
    public ChallengeAltarData updateActive(boolean isActive) {
        return new ChallengeAltarData(this.activeMobs, isActive, this.killedMobs, this.maxMobsOnMap, this.maxMobs, this.round);
    }

    // Method to update the `killedMobs` field
    public ChallengeAltarData updateKilledMobs(int killedMobs) {
        return new ChallengeAltarData(this.activeMobs, this.isActive, killedMobs, this.maxMobsOnMap, this.maxMobs, this.round);
    }

    // Method to update the `maxMobsOnMap` field
    public ChallengeAltarData updateMaxMobsOnMap(int maxMobsOnMap) {
        return new ChallengeAltarData(this.activeMobs, this.isActive, this.killedMobs, maxMobsOnMap, this.maxMobs, this.round);
    }

    // Method to update the `round` field
    public ChallengeAltarData updateRound(int round) {
        return new ChallengeAltarData(this.activeMobs, this.isActive, this.killedMobs, this.maxMobsOnMap, this.maxMobs, round);
    }

    public ChallengeAltarData updateMaxMobs(int maxMobs) {
        return new ChallengeAltarData(this.activeMobs, this.isActive, this.killedMobs, this.maxMobsOnMap, maxMobs, this.round);
    }

    public static ChallengeAltarData updateAll(List<UUID> activeMobs, boolean isActive, int killedMobs, int maxMobsOnMap, int maxMobs, int round) {
        return new ChallengeAltarData(activeMobs, isActive, killedMobs, maxMobsOnMap, maxMobs, round);
    }

    private void serialise(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(activeMobs, UUIDUtil.STREAM_CODEC);
        friendlyByteBuf.writeBoolean(isActive);
        friendlyByteBuf.writeInt(killedMobs);
        friendlyByteBuf.writeInt(maxMobsOnMap);
        friendlyByteBuf.writeInt(maxMobs);
        friendlyByteBuf.writeInt(round);
    }

    private static ChallengeAltarData deserialise(FriendlyByteBuf friendlyByteBuf) {
        var activeMobs = friendlyByteBuf.readCollection(Lists::newArrayListWithCapacity, UUIDUtil.STREAM_CODEC);
        var isActive = friendlyByteBuf.readBoolean();
        var killedMobs = friendlyByteBuf.readInt();
        var maxMobsOnMap = friendlyByteBuf.readInt();
        var maxMobs = friendlyByteBuf.readInt();
        var round = friendlyByteBuf.readInt();
        return new ChallengeAltarData(activeMobs, isActive, killedMobs, maxMobsOnMap, maxMobs, round);
    }

    public static final StreamCodec<FriendlyByteBuf, ChallengeAltarData> STREAM_CODEC = StreamCodec.ofMember(
        ChallengeAltarData::serialise,
        ChallengeAltarData::deserialise
    );

    public void addMob(UUID uuid){
        this.activeMobs().add(uuid);
    }

    public void removeMob(UUID uuid){
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

    public static void resetAltar(BlockEntity entity){
        var current = ChallengeAltarData.getProperties(entity);
        entity.setData(CHALLENGE_ALTAR, current.resetWithNewLevel());
    }

    public int maxSpawnableMobs(){
        return Math.min(maxMobsOnMap, maxMobs - killedMobs);
    }

    public static void incrementKilledMobs(BlockEntity entity){
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var newData = altarData.updateKilledMobs(altarData.killedMobs + 1);
        entity.setData(CHALLENGE_ALTAR, newData);
        entity.setChanged();
    }

    public static void addEntity(BlockEntity entity, UUID uuid){
        var altarData = entity.getData(CHALLENGE_ALTAR);
        altarData.addMob(uuid);
        entity.setChanged();
    }

    public static void removeEntity(BlockEntity entity, UUID uuid){
        var altarData = entity.getData(CHALLENGE_ALTAR);
        altarData.removeMob(uuid);
        entity.setChanged();
    }

    public static void flipActive(BlockEntity entity){
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var newData = altarData.updateActive(!altarData.isActive());
        entity.setData(CHALLENGE_ALTAR, newData);
        entity.setChanged();
    }

    public static void setRound(BlockEntity entity, int round){
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var newRoundData = altarData.updateRound(round);
        entity.setData(CHALLENGE_ALTAR, newRoundData);
        entity.setChanged();
    }

    public static void altarClickToStart(ChallengeAltarBlockEntity altarE) {
        ChallengeAltarData.setMaxMobs(altarE);
        ChallengeAltarData.setMaxMapMobs(altarE);
        ChallengeAltarData.flipActive(altarE);
    }

    public static void setMaxMobs(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var round = altarData.round();
        var newMaxMobData = altarData.updateMaxMobs(round * 3 + Random.nextInt(Math.max(round /5, 3)));
        entity.setData(CHALLENGE_ALTAR, newMaxMobData);
        entity.setChanged();
    }

    public static void setMaxMapMobs(BlockEntity entity) {
        var altarData = entity.getData(CHALLENGE_ALTAR);
        var round = altarData.round();
        var newMaxMobMapData = altarData.updateMaxMobsOnMap(Math.max(2, Math.min(round / 2, 30)));
        entity.setData(CHALLENGE_ALTAR, newMaxMobMapData);
        entity.setChanged();
    }

    public static ChallengeAltarData getProperties(BlockEntity blockEntity){
        return blockEntity.getData(CHALLENGE_ALTAR);
    }

    public static final Codec<ChallengeAltarData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(UUIDUtil.CODEC).fieldOf("active_mobs").forGetter(ChallengeAltarData::activeMobs),
            Codec.BOOL.fieldOf("is_active").forGetter(ChallengeAltarData::isActive),
            Codec.INT.fieldOf("killed_mobs").forGetter(ChallengeAltarData::killedMobs),
            Codec.INT.fieldOf("max_mobs_on_map").forGetter(ChallengeAltarData::maxMobsOnMap),
            Codec.INT.fieldOf("max_mobs").forGetter(ChallengeAltarData::maxMobs),
            Codec.INT.fieldOf("round").forGetter(ChallengeAltarData::round)
        ).apply(instance, ChallengeAltarData::new)
    );

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag mobList = new CompoundTag();
        for (UUID mob : activeMobs) mobList.putUUID(mob.toString(), mob);
        nbt.put("active_mobs", mobList);
        nbt.putBoolean("is_active", isActive);
        nbt.putInt("killed_mobs", killedMobs);
        nbt.putInt("max_mobs_on_map", maxMobsOnMap);
        nbt.putInt("max_mobs", maxMobs);
        nbt.putInt("round", round);
    }

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
    }
}