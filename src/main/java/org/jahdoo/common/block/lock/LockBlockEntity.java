package org.jahdoo.common.block.lock;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.level_manager.RoomData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.block.SyncedBlockEntity;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;
import java.util.Objects;

import static org.jahdoo.common.block.lock.LockBlock.FACING;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.mod.LevelBoonReg.*;
import static org.jahdoo.trial_nexus.attachments.RunData.setDateAndTime;
import static org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon.SyncableData;
import static org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon.SyncableData.*;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.getFromLevel;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.*;


public class LockBlockEntity extends SyncedBlockEntity {

    public Component roomId = Component.empty();
    public SyncableData negativeBoon = EMPTY;
    public SyncableData positiveBoon = EMPTY;
    public String getDifficulty = "";
    public boolean isStarting;
    public int counter;
    public boolean clicked;

    public LockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.LOCK_BE.get(), pPos, pBlockState);
    }

    public boolean hasDifficulty(){
        var level = getLevel();
        if(level == null) return false;
        return level.getData(INSTANCE_DATA).getDifficulty().isEmpty();
    }

    public boolean isStartingRoom(){
        return isStarting;
    }

    public void setDifficulty(){
        var level = this.getLevel();
        if(level == null) return;
        var instance = level.getData(INSTANCE_DATA);
        var instanceData = switch (getDifficulty){
            case JahdooHelpers.MEDIUM -> InstanceData.setMediumData(instance);
            case JahdooHelpers.HARD -> InstanceData.setHardData(instance);
            default -> InstanceData.setEasyData(instance);
        };

        level.setData(INSTANCE_DATA, instanceData);
        SoundHelpers.getSoundWithPosition(level, this.getBlockPos(), SoundReg.SWORD_THUD.get(), SoundSource.BLOCKS, 1F, 1.4F);
        JahdooHelpers.sendPacketsToPlayerDistance(getBlockPos().getCenter(), 400, level,
            (serverPlayer) -> {
                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(10, 30, 20));
                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(TextHelpers.withStyleComponent("BEGIN", getFromLevel(instanceData).getColor())));
            }
        );

        if(level instanceof ServerLevel serverLevel){
            for (var player : serverLevel.players()) setDateAndTime(player);
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(!(level instanceof ServerLevel)) return;
        if (clicked) this.counter++;
        if (counter > 1) level.removeBlock(pos, false);
    }

    public boolean isInitialized(){
        return !Objects.equals(this.roomId, Component.empty());
    }

    public boolean canPlace(){
        var level = getLevel();
        if(level == null) return false;

        var floorBlock = getBlockPos().relative(getBlockState().getValue(FACING), 1).below(2);
        return level.getBlockState(floorBlock).isAir();
    }

    public void setRoomData(Component roomId){
        this.roomId = this.isStartingRoom() ? RoomData.getRandomBattleRoom() : roomId;
        setDataByDifficulty();
        this.updateBlock();
    }

    public void setDataByDifficulty() {
        if(!this.isStartingRoom()){
            var data = getLevel();
            if(data == null) return;
            var data1 = data.getData(INSTANCE_DATA);
            switch (data1.getDifficulty()){
                case JahdooHelpers.EASY -> {
                    var rarityForNeg = List.of(Pair.of(COMMON, 1), Pair.of(RARE, 5000));
                    if (org.shaydee.shaydeeapi.Maths.percentageChance(30)) this.negativeBoon = toSyncable(withRarityNegative(getRarity(rarityForNeg)), getRarity(rarityForNeg));
                    this.positiveBoon = toSyncable(withRarityPositive(getRarity()), getRarity());
                }
                case JahdooHelpers.MEDIUM -> {
                    this.negativeBoon = toSyncable(randomNegative(), getRarity());
                    if (org.shaydee.shaydeeapi.Maths.percentageChance(70)) this.positiveBoon = toSyncable(withRarityPositive(getRarity()), getRarity());
                }
                case JahdooHelpers.HARD -> {
                    var rarityForNeg = List.of(Pair.of(EPIC, 1), Pair.of(LEGENDARY, 5500), Pair.of(MYTHIC, 6000));
                    var rarityForPos = List.of(Pair.of(RARE, 1), Pair.of(LEGENDARY, 5000), Pair.of(MYTHIC, 6000));
                    this.negativeBoon = toSyncable(withRarityNegative(getRarity()), getRarity(rarityForNeg));
                    if (org.shaydee.shaydeeapi.Maths.percentageChance(50)) this.positiveBoon = toSyncable(withRarityPositive(getRarity(rarityForPos)), getRarity(rarityForNeg));
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("starting", this.isStarting);
        tag.putString("difficulty", this.getDifficulty);
        tag.putString("component", Component.Serializer.toJson(this.roomId, registries));
        tag.putInt("counter", this.counter);
        tag.putBoolean("clicked", this.clicked);
        saveSyncable(tag, registries, this.negativeBoon, "negative");
        saveSyncable(tag, registries, this.positiveBoon, "positive");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.isStarting = tag.getBoolean("starting");
        this.getDifficulty = tag.getString("difficulty");
        this.counter = tag.getInt("counter");
        this.clicked = tag.getBoolean("clicked");
        this.roomId = Component.Serializer.fromJson(tag.getString("component"), registries);
        this.negativeBoon = loadData(tag, registries, "negative");
        this.positiveBoon = loadData(tag, registries, "positive");
    }

}

