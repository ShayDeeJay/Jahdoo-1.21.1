package org.jahdoo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.tank.TankBlockEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.GeneralHelpers;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractTankUser extends AbstractBEInventory {

    protected BlockPos tankPosition;
    protected int progress;

    public AbstractTankUser(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int stackSize) {
        super(pType, pPos, pBlockState, stackSize);
    }

    protected void assignTankBlockInRange(Level level, BlockPos pPos, int craftingFuelCost){
        if(this.tankPosition == null || !(level.getBlockEntity(tankPosition) instanceof TankBlockEntity)){
            var blockPos = this.getTankBlockInRange(level, pPos.above())
                .stream()
                .filter(
                    blockPos1 ->
                        level.getBlockEntity(blockPos1) instanceof TankBlockEntity tankBlockEntity &&
                        tankBlockEntity.inputItemHandler.getStackInSlot(0).getCount() >= craftingFuelCost
                ).toList();

            if(!blockPos.isEmpty()) this.tankPosition = blockPos.getFirst(); else this.progress = 0;
        }

        if(tankPosition != null){
            if (level.getBlockEntity(tankPosition) instanceof TankBlockEntity tankBlockEntity) {
                if (progress > 0) {
                    if(!tankBlockEntity.usingThisTank.contains(this)) tankBlockEntity.usingThisTank.add(this);
                } else {
                    tankBlockEntity.usingThisTank.remove(this);
                };
            }
        }
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

    protected void chargeTankFuel(int craftingFuelCost){
        this.getTankEntity().inputItemHandler.getStackInSlot(0).shrink(craftingFuelCost);
    }

    protected void sendProcessingParticle(double fromHeight, double toHeight, int lifetime, float size, double speed){
        if(!(this.level instanceof ServerLevel serverLevel)) return;
        Vec3 direction = this.getBlockPos().getCenter().add(0, toHeight, 0).subtract(this.tankPosition.getCenter()).normalize();
        GeneralHelpers.generalHelpers.sendParticles(
            serverLevel,
            processingParticle(lifetime, size, false,  speed),
            this.tankPosition.getCenter().subtract(0,fromHeight,0), 0, direction.x, direction.y, direction.z, speed
        );
    }

    protected boolean hasTankAndFuel(int craftingFuelCost){
        if(this.level == null || this.tankPosition == null) return false;
        if (!(this.level.getBlockEntity(this.tankPosition) instanceof TankBlockEntity tankBlockEntity)) return false;
        return this.tankPosition != null && tankBlockEntity.inputItemHandler.getStackInSlot(0).getCount() > craftingFuelCost;
    }

    private List<BlockPos> getTankBlockInRange(Level pLevel, BlockPos pos) {
        List<BlockPos> allBlocks = new ArrayList<>();
        BlockPos[] adjacentPositions = new BlockPos[] {
            pos.below(2),
            pos.north(),
            pos.south(),
            pos.east(),
            pos.west(),
            pos.above(),
            pos.north().east(), // North-East
            pos.north().west(), // North-West
            pos.south().east(), // South-East
            pos.south().west(), // South-West
        };

        for (BlockPos adjacentPos : adjacentPositions) {
            BlockState adjacentState = pLevel.getBlockState(adjacentPos);
            if (adjacentState.is(BlocksRegister.TANK.get())) allBlocks.add(adjacentPos);
        }
        return allBlocks;
    }

}
