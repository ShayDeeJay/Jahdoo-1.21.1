package org.jahdoo.common.block.lock;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.List;
import java.util.Objects;

import static org.jahdoo.ascension.attachments.RunData.setDateAndTime;
import static org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon.SyncableData;
import static org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon.SyncableData.*;
import static org.jahdoo.ascension.level_manager.InstanceDifficulty.getFromLevel;
import static org.jahdoo.ascension.level_manager.StructureManager.getBattleRoom;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;
import static org.jahdoo.common.block.lock.LockBlock.FACING;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.mod.LevelBoonReg.*;


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

        var instanceData = switch (getDifficulty){
            case Helpers.MEDIUM -> InstanceData.setMediumData();
            case Helpers.HARD -> InstanceData.setHardData();
            default -> InstanceData.setEasyData();
        };

        level.setData(INSTANCE_DATA, instanceData);
        Helpers.getSoundWithPosition(level, this.getBlockPos(), SoundReg.SWORD_THUD.get(), 1, 1.4F);
        Helpers.sendPacketsToPlayerDistance(getBlockPos().getCenter(), 400, level,
            (serverPlayer) -> {
                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(10, 30, 20));
                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Helpers.withStyleComponent("BEGIN", getFromLevel(level).getColor())));
            }
        );

        if(level instanceof ServerLevel serverLevel){
            for (var player : serverLevel.players()) setDateAndTime(player);
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(!(level instanceof ServerLevel)) return;
        if(clicked) this.counter++;
        if(counter > 1) level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
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
        this.roomId = this.isStartingRoom() ? getBattleRoom() : roomId;
        setDataByDifficulty();
        this.updateBlock();

    }

    private void setDataByDifficulty() {
        if(!this.isStartingRoom()){
            var data = getLevel();
            if(data == null) return;
            var data1 = data.getData(INSTANCE_DATA);
            switch (data1.getDifficulty()){
                case Helpers.EASY -> {
                    var rarityForNeg = List.of(Pair.of(COMMON, 1), Pair.of(RARE, 5000));
                    if (Maths.percentageChance(30)) this.negativeBoon = toSyncable(withRarityNegative(getRarity(rarityForNeg)), getRarity(rarityForNeg));
                    this.positiveBoon = toSyncable(withRarityPositive(getRarity()), getRarity());
                }
                case Helpers.MEDIUM -> {
                    this.negativeBoon = toSyncable(randomNegative(), getRarity());
                    if (Maths.percentageChance(70)) this.positiveBoon = toSyncable(withRarityPositive(getRarity()), getRarity());
                }
                case Helpers.HARD -> {
                    var rarityForNeg = List.of(Pair.of(EPIC, 1), Pair.of(LEGENDARY, 5500), Pair.of(ETERNAL, 6000));
                    var rarityForPos = List.of(Pair.of(RARE, 1), Pair.of(LEGENDARY, 5000), Pair.of(ETERNAL, 6000));
                    this.negativeBoon = toSyncable(withRarityNegative(getRarity()), getRarity(rarityForNeg));
                    if (Maths.percentageChance(50)) this.positiveBoon = toSyncable(withRarityPositive(getRarity(rarityForPos)), getRarity(rarityForNeg));
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

