package org.jahdoo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.tank.TankBlockEntity;
import org.jahdoo.particle.ParticleHandlers;
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
        if(this.tankPosition == null || !(level.getBlockEntity(tankPosition) instanceof TankBlockEntity)){
            findTank(level, pPos, craftingFuelCost);
        }

        if(tankPosition != null){
            if (level.getBlockEntity(tankPosition) instanceof TankBlockEntity tankBlockEntity) {
                if (progress > 0) {
                    if(!tankBlockEntity.usingThisTank.contains(this)) tankBlockEntity.usingThisTank.add(this);
                } else {
                    tankBlockEntity.usingThisTank.remove(this);
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
                    blockPos1 -> level.getBlockEntity(blockPos1) instanceof TankBlockEntity tankBlockEntity &&
                            tankBlockEntity.inputItemHandler.getStackInSlot(0).getCount() >= craftingFuelCost
                )
                .sorted(Comparator.comparingInt(blockPos1 -> level.getBlockEntity(blockPos1) instanceof TankBlockEntity tankBlockEntity ? tankBlockEntity.inputItemHandler.getStackInSlot(0).getCount() : 0))
                .toList();


            if (!blockPos.isEmpty()) {
                if (tankPosition != null) {
                    if (level.getBlockEntity(tankPosition) instanceof TankBlockEntity tankBlockEntity) {
                        tankBlockEntity.usingThisTank.remove(this);
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

        for (int dx = -2; dx <= 2; dx++) {       // 5 blocks in x-axis
            for (int dy = -3; dy <= 1; dy++) {   // 5 blocks in y-axis, shifted down by 1
                for (int dz = -2; dz <= 2; dz++) { // 5 blocks in z-axis
                    adjacentPositions[index++] = pos.offset(dx, dy, dz);
                }
            }
        }

        return adjacentPositions;
    }

    protected TankBlockEntity getTankEntity(){
        if (this.level.getBlockEntity(this.tankPosition) instanceof TankBlockEntity tankBlockEntity) {
            return tankBlockEntity;
        }
        return null;
    }

    public int getProgress(){
        return this.progress;
    }

    public abstract int setCraftingCost();

    protected void chargeTankFuel(int craftingFuelCost){
        this.getTankEntity().inputItemHandler.getStackInSlot(0).shrink(craftingFuelCost);
        if(this.getTankEntity().getLevel() == null) return;
        var blockstate = getLevel().getBlockState(this.tankPosition);
        this.getTankEntity().getLevel().sendBlockUpdated(this.tankPosition, blockstate, blockstate,1);
    }

    protected void sendProcessingParticle(double fromHeight, double toHeight, int lifetime, float size, double speed){
        Vec3 direction = this.getBlockPos().getCenter().add(0, toHeight, 0).subtract(this.tankPosition.getCenter()).normalize();
        ParticleHandlers.sendParticles(
            this.level,
            processingParticle(lifetime, size, false,  speed),
            this.tankPosition.getCenter().subtract(0,fromHeight,0), 0, direction.x, direction.y, direction.z, speed
        );
    }

    protected boolean hasTankAndFuel(){
        if(this.level == null || this.tankPosition == null) return false;
        if (!(this.level.getBlockEntity(this.tankPosition) instanceof TankBlockEntity tankBlockEntity)) return false;
        return this.tankPosition != null && tankBlockEntity.inputItemHandler.getStackInSlot(0).getCount() >= this.setCraftingCost();
    }


    private List<BlockPos> getTankBlockInRange(Level pLevel, BlockPos pos) {
        List<BlockPos> allBlocks = new ArrayList<>();
        for (BlockPos adjacentPos : findInRange(pos)) {
            BlockState adjacentState = pLevel.getBlockState(adjacentPos);
            if (adjacentState.is(BlocksRegister.TANK.get())) allBlocks.add(adjacentPos);
        }
        return allBlocks;
    }

}
