package org.jahdoo.common.block.lock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.registers.BlockEntityReg;

import java.util.Objects;

import static org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon.SyncableData;
import static org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon.SyncableData.*;
import static org.jahdoo.ascension.level_manager.StructureManager.getBattleRooms;
import static org.jahdoo.ascension.level_manager.StructureManager.getRandomRoomId;
import static org.jahdoo.ascension.rarity.JahdooRarity.COMMON;
import static org.jahdoo.common.block.lock.LockBlock.FACING;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.LevelBoonReg.*;


public class LockBlockEntity extends SyncedBlockEntity {

    public Component roomId = Component.empty();
    public SyncableData negativeBoon = EMPTY;
    public SyncableData positiveBoon = EMPTY;
    public String getDifficulty;

    public LockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.LOCK_BE.get(), pPos, pBlockState);
    }

    public boolean isStartingRoom(){
        if(this.getLevel() == null) return false;
        return this.getLevel().getData(INSTANCE_DATA).getClearedRooms() == 0;
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

    public void setRoomData(){
        this.roomId = this.isStartingRoom() ? getBattleRooms() : getRandomRoomId();
        this.negativeBoon = toSyncable(this.isStartingRoom() ? withRarityNegative(COMMON) : randomNegative());
        if(Maths.percentageChance(50) && !this.isStartingRoom()) this.positiveBoon = toSyncable(withRarityPositive(JahdooRarity.getRarity()));
        this.updateBlock();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("dif", getDifficulty);
        tag.putString("component", Component.Serializer.toJson(this.roomId, registries));
        saveSyncable(tag, registries, this.negativeBoon, "negative");
        saveSyncable(tag, registries, this.positiveBoon, "positive");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.getDifficulty = tag.getString("dif");
        this.roomId = Component.Serializer.fromJson(tag.getString("component"), registries);
        this.negativeBoon = loadData(tag, registries, "negative");
        this.positiveBoon = loadData(tag, registries, "positive");
    }

}

