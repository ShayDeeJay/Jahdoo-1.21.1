package org.jahdoo.block.shopping_table;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.registers.BlockEntitiesRegister;

public class ShoppingTableEntity extends AbstractBEInventory {

    public ShoppingTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.SHOPPING_TABLE_BE.get(), pPos, pBlockState, 64);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {

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
        return 64;
    }
}
