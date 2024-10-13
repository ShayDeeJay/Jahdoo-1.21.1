package org.jahdoo.block.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;


public class TankBlockEntity extends AbstractBEInventory {

    int counter;
    private static final int INPUT = 0;

    public TankBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.TANK_BE.get(), pPos, pBlockState, 64);
    }

    public ItemStack getRenderer() {
        return this.inputItemHandler.getStackInSlot(INPUT);
    }


    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(!(pLevel instanceof ServerLevel serverLevel)) return;
        int tankSlotSize = this.inputItemHandler.getStackInSlot(0).getCount();

        this.beamParticlesToUser(serverLevel, pPos, tankSlotSize);
        this.harvestOreBelow(serverLevel, pPos, tankSlotSize);
    }

    private void beamParticlesToUser(ServerLevel serverLevel, BlockPos pos, int tankSlotSize){
        if(tankSlotSize < this.getMaxSlotSize() && this.counter > 0){
            counter++;
            GeneralHelpers.getOuterRingOfRadiusRandom(pos.getCenter().subtract(0, 0.5, 0), 0.5, 2,
                positions -> {
                    Vec3 direction = pos.getCenter().add(0, 1, 0).subtract(positions).normalize();
                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel, processingParticle(5,0.1f, true, 0.1),
                        positions, 0,
                        direction.x, direction.y, direction.z,
                        GeneralHelpers.Random.nextDouble(0.08,0.12)
                    );
                }
            );
        }
    }

    private void harvestOreBelow(ServerLevel serverLevel, BlockPos pos, int tankSlotSize){
        BlockState blockState = serverLevel.getBlockState(pos.below());
        var harvestBlock = BlocksRegister.CRYSTAL_ORE.get();

        if(!(blockState.is(harvestBlock))) {
            if(this.counter > 0) this.counter = 0;
            return;
        }

        if(counter >= 200){
            if(tankSlotSize <= 64){
                var powderItem = new ItemStack(ItemsRegister.JIDE_POWDER.get());
                var amountToCopy = Math.min(6 + tankSlotSize, 64);
                serverLevel.destroyBlock(pos.below(), false);
                this.inputItemHandler.setStackInSlot(INPUT, powderItem.copyWithCount(amountToCopy));
            }
            counter = 0;
        }
    }

    @Override
    public int setInputSlots() {
        return 1;
    }

    @Override
    public int setOutputSlots() {
        return 1;
    }

    @Override
    public int getMaxSlotSize() {
        return 64;
    }


}

