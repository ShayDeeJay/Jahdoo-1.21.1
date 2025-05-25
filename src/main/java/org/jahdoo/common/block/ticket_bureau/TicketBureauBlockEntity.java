package org.jahdoo.common.block.ticket_bureau;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.registers.BlockEntityReg;

public class TicketBureauBlockEntity extends AbstractBEInventory {

    public TicketBureauBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.TICKET_BUREAU_BE.get(), pos, state, 1);
    }

    public TicketBureauBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int stackSize) {
        super(type, pos, state, stackSize);
    }

    public ItemStack getTicketItem(){
        return this.inputItemHandler.getStackInSlot(0);
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
    public int getMaxSlotSizeInput() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 0;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

}
