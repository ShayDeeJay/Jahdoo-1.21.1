package org.jahdoo.common.block.perk_table;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;

import static net.minecraft.sounds.SoundEvents.*;
import static org.jahdoo.common.registers.SoundReg.*;


public class PerkTableEntity extends SyncedBlockEntity {

    public int counter;
    private boolean hasUsed;

    public PerkTableEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.PERK_TABLE_BE.get(), pos, state);
    }

    public boolean getUsed(){
        return this.hasUsed;
    }

    public void setUsed(){
        if(!getUsed() && level != null){
            this.hasUsed = true;
            Helpers.getSoundWithPosition(level, getBlockPos(), PLAYER_LEVELUP, 1, 0.8F);
            this.updateBlock();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.counter);
        tag.putBoolean("used", this.hasUsed);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.counter = tag.getInt("counter");
        this.hasUsed = tag.getBoolean("used");
        super.loadAdditional(tag, registries);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        counter++;
    }

}

