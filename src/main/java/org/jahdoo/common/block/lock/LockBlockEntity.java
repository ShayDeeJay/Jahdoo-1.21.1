package org.jahdoo.common.block.lock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.registers.BlockEntityReg;

import static org.jahdoo.ascension.level_manager.StructureManager.getBattleRooms;
import static org.jahdoo.ascension.level_manager.StructureManager.getRandomRoomId;
import static org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon.SyncableData;
import static org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon.SyncableData.*;
import static org.jahdoo.common.block.lock.LockBlock.FACING;
import static org.jahdoo.common.registers.LevelBoonReg.randomNegative;
import static org.jahdoo.common.registers.LevelBoonReg.randomPositive;


public class LockBlockEntity extends SyncedBlockEntity {

    public Component roomId = Component.literal("");
    public SyncableData negativeBoon = EMPTY;
    public SyncableData positiveBoon = EMPTY;
    public boolean isStartingBlock;

    public LockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.LOCK_BE.get(), pPos, pBlockState);
    }

    public void setStartingBlock(boolean startingBlock) {
        isStartingBlock = startingBlock;
    }

    public boolean isInitialized(){
        return !this.roomId.getString().isEmpty()/* && this.negativeBoon != EMPTY*/;
    }

    public boolean canPlace(){
        var level = getLevel();
        if(level == null) return false;

        var floorBlock = getBlockPos().relative(getBlockState().getValue(FACING), 1).below(2);
        return level.getBlockState(floorBlock).isAir();
    }

    public void setRoomData(){
        this.roomId = this.isStartingBlock ? getBattleRooms() : getRandomRoomId();
        this.negativeBoon = toSyncable(randomNegative());
        if(Maths.percentageChance(30) && !this.isStartingBlock) this.positiveBoon = toSyncable(randomPositive());
        this.updateBlock();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("component", Component.Serializer.toJson(this.roomId, registries));
        saveSyncable(tag, registries, this.negativeBoon, "negative");
        saveSyncable(tag, registries, this.positiveBoon, "positive");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.roomId = Component.Serializer.fromJson(tag.getString("component"), registries);
        this.negativeBoon = loadData(tag, registries, "negative");
        this.positiveBoon = loadData(tag, registries, "positive");
    }

}

