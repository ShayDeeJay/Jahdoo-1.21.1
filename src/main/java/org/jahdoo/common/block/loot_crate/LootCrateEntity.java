package org.jahdoo.common.block.loot_crate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.registers.BlockEntityReg;


public class LootCrateEntity extends AbstractBEInventory {

    public LootCrateEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.LOOT_CRATE_BE.get(), pPos, pBlockState, 1);
    }

    @Override
    public int setInputSlots() {
        return 1;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSize() {
        return 1;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }


    public void tick(Level level, BlockPos pos, BlockState state) {}

}

