package org.jahdoo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.tank.TankBlockEntity;
import org.jahdoo.common.registers.BlockReg;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparingInt;


public abstract class AbstractTankUser extends AbstractBEInventory {

    protected BlockPos tankPosition;
    protected int progress;

    public AbstractTankUser(BlockEntityType<?> type, BlockPos pos, BlockState state, int stackSize) {
        super(type, pos, state, stackSize);
    }

    public abstract int setCraftingCost();

    public int getNexiteCount(){
        return this.getTankEntity().inputItemHandler.getStackInSlot(0).getCount();
    }

    protected void chargeTankFuel(int craftingFuelCost){
        this.getTankEntity().chargeTankFuel(craftingFuelCost);
    }

    public TankBlockEntity getTankEntity(){
        if (this.tankPosition != null) {
            var entity = this.getLevel().getBlockEntity(this.tankPosition);
            if (entity instanceof TankBlockEntity tank) {
                return tank;
            }
        }
        return null;
    }

    protected boolean hasTankAndFuel(){
        if(this.level == null || this.tankPosition == null) return false;
        if (!(this.level.getBlockEntity(this.tankPosition) instanceof TankBlockEntity tankBlockEntity)) return false;

        var getNexite = tankBlockEntity.inputItemHandler.getStackInSlot(0).getCount();
        var hasEnoughNexite = getNexite >= this.setCraftingCost();

        return this.tankPosition != null && hasEnoughNexite && this.setCraftingCost() > 0;
    }

    private List<BlockPos> getTankBlockInRange(Level level, BlockPos pos) {
        var allBlocks = new ArrayList<BlockPos>();


        for (BlockPos adjacentPos : findInRange(pos)) {
            var adjacentState = level.getBlockState(adjacentPos);
            if (adjacentState.is(BlockReg.TANK.get())) allBlocks.add(adjacentPos);
        }

        return allBlocks;
    }

    public static BlockPos[] findInRange(BlockPos pos){
        var adjacentPositions = new BlockPos[125]; // 5 * 5 * 5 = 125 positions
        var index = 0;

        for (var dx = -2; dx <= 2; dx++) {
            for (var dy = -3; dy <= 1; dy++) {
                for (var dz = -2; dz <= 2; dz++) {
                    adjacentPositions[index++] = pos.offset(dx, dy, dz);
                }
            }
        }

        return adjacentPositions;
    }

    protected void assignTankBlockInRange(Level level, BlockPos pos, int craftingFuelCost){
        var tank = tankPosition;

        if(tank == null || !(level.getBlockEntity(tank) instanceof TankBlockEntity)){
//            System.out.println(this.tankPosition);
            findTank(level, pos, craftingFuelCost);
        }

        if(tank != null){
            if (level.getBlockEntity(tank) instanceof TankBlockEntity tankBlockEntity) {
                var usedTank = tankBlockEntity.usingThisTank;
                if (progress > 0) {
                    if(!usedTank.contains(this)) usedTank.add(this);
                } else {
                    usedTank.remove(this);
                };
            }
            findTank(level, pos, craftingFuelCost);
        }
    }

    public void findTank(Level level, BlockPos pos, int craftingFuelCost) {
        if(hasTankAndFuel()) return;

        var blockPos = this.getTankBlockInRange(level, pos.above())
            .stream()
            .filter(
                blockPos1 -> {
                    var entity = level.getBlockEntity(blockPos1);
                    return entity instanceof TankBlockEntity tankEntity && tankEntity.getCount() >= craftingFuelCost;
                }
            )
            .sorted(
                comparingInt(
                    blockPos1 -> {
                        var entity = level.getBlockEntity(blockPos1);
                        return entity instanceof TankBlockEntity tankEntity ? tankEntity.getCount() : 0;
                    }
                )
            )
            .toList();

        if (blockPos.isEmpty()) {
            this.progress = 0;
            return;
        }

        if (tankPosition != null) {
            if (level.getBlockEntity(tankPosition) instanceof TankBlockEntity tankEntity) {
                tankEntity.usingThisTank.remove(this);
            }
        }

        this.tankPosition = blockPos.getLast();
    }



}
