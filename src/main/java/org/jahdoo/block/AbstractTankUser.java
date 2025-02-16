package org.jahdoo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.block.tank.NexiteTankBlockEntity;
import org.jahdoo.registers.BlocksRegister;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public abstract class AbstractTankUser extends AbstractBEInventory {
    protected BlockPos tankPosition;
    protected int progress;

    public AbstractTankUser(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int stackSize) {
        super(pType, pPos, pBlockState, stackSize);
    }

    protected void assignTankBlockInRange(Level level, BlockPos pPos, int craftingFuelCost){
        if(this.tankPosition == null || !(level.getBlockEntity(tankPosition) instanceof NexiteTankBlockEntity)){
            findTank(level, pPos, craftingFuelCost);
        }

        if(tankPosition != null){
            if (level.getBlockEntity(tankPosition) instanceof NexiteTankBlockEntity nexiteTankBlockEntity) {
                if (progress > 0) {
                    if(!nexiteTankBlockEntity.usingThisTank.contains(this)) nexiteTankBlockEntity.usingThisTank.add(this);
                } else {
                    nexiteTankBlockEntity.usingThisTank.remove(this);
                };
            }
            findTank(level, pPos, craftingFuelCost);
        }
    }

    public void findTank(Level level, BlockPos pPos, int craftingFuelCost) {
        if(!hasTankAndFuel()){
            var blockPos = this.getTankBlockInRange(level, pPos.above())
                .stream()
                .filter(
                    blockPos1 -> level.getBlockEntity(blockPos1) instanceof NexiteTankBlockEntity tankBlockEntity &&
                    tankBlockEntity.inputItemHandler.getStackInSlot(0).getCount() >= craftingFuelCost
                )
                .sorted(
                    Comparator.comparingInt(blockPos1 -> level.getBlockEntity(blockPos1) instanceof NexiteTankBlockEntity tankBlockEntity ? tankBlockEntity.inputItemHandler.getStackInSlot(0).getCount() : 0)
                )
                .toList();

            if (!blockPos.isEmpty()) {
                if (tankPosition != null) {
                    if (level.getBlockEntity(tankPosition) instanceof NexiteTankBlockEntity nexiteTankBlockEntity) {
                        nexiteTankBlockEntity.usingThisTank.remove(this);
                    }
                }
                this.tankPosition = blockPos.getLast();
            } else {
                this.progress = 0;
            }
        }
    }

    public static BlockPos[] findInRange(BlockPos pos){
        BlockPos[] adjacentPositions = new BlockPos[125]; // 5 * 5 * 5 = 125 positions
        int index = 0;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -3; dy <= 1; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    adjacentPositions[index++] = pos.offset(dx, dy, dz);
                }
            }
        }

        return adjacentPositions;
    }

    public int getNexiteCount(){
        return this.getTankEntity().inputItemHandler.getStackInSlot(0).getCount();
    }

    public NexiteTankBlockEntity getTankEntity(){
        if (this.tankPosition != null) {
            var bEntity = this.getLevel().getBlockEntity(this.tankPosition);
            if (bEntity instanceof NexiteTankBlockEntity tank) {
                return tank;
            }
        }
        return null;
    }

    public abstract int setCraftingCost();

    protected void chargeTankFuel(int craftingFuelCost){
        this.getTankEntity().chargeTankFuel(craftingFuelCost);
    }

    protected boolean hasTankAndFuel(){
        if(this.level == null || this.tankPosition == null) return false;
        if (!(this.level.getBlockEntity(this.tankPosition) instanceof NexiteTankBlockEntity nexiteTankBlockEntity)) return false;
        var getNexite = nexiteTankBlockEntity.inputItemHandler.getStackInSlot(0).getCount();
        var hasEnoughNexite = getNexite >= this.setCraftingCost();
        return this.tankPosition != null && hasEnoughNexite && this.setCraftingCost() > 0;
    }

    private List<BlockPos> getTankBlockInRange(Level pLevel, BlockPos pos) {
        var allBlocks = new ArrayList<BlockPos>();
        for (BlockPos adjacentPos : findInRange(pos)) {
            var adjacentState = pLevel.getBlockState(adjacentPos);
            if (adjacentState.is(BlocksRegister.TANK.get())) allBlocks.add(adjacentPos);
        }
        return allBlocks;
    }

}
