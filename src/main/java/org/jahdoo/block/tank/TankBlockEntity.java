package org.jahdoo.block.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.block.AbstractTankUser.findInRange;
import static org.jahdoo.block.tank.TankBlock.LIT;


public class TankBlockEntity extends AbstractBEInventory {
    int counter;
    public int glowStrength = 150;
    private static final int INPUT = 0;
    public List<AbstractTankUser> usingThisTank = new ArrayList<>();

    public TankBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.TANK_BE.get(), pPos, pBlockState, 64);
        this.setData(AttachmentRegister.BOOL, false);
    }

    public ItemStack getRenderer() {
        return this.inputItemHandler.getStackInSlot(INPUT);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("counter", this.counter);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        this.counter = pTag.getInt("counter");
        super.loadAdditional(pTag, pRegistries);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        getTankBlockInRange(pLevel, pPos);
        this.setState(pLevel, pPos, pState);

        if(!(pLevel instanceof ServerLevel serverLevel)) return;
        int tankSlotSize = this.inputItemHandler.getStackInSlot(INPUT).getCount();
        this.beamParticlesToUser(serverLevel, pPos, tankSlotSize);
        this.harvestOreBelow(serverLevel, pPos, tankSlotSize);
    }

    private void setState(Level level, BlockPos pos, BlockState state){
        var using = this.usingThisTank.isEmpty();
        level.setBlock(pos, state.setValue(LIT, !using), 2);
    }

    private List<BlockPos> getTankBlockInRange(Level pLevel, BlockPos pos) {
        List<BlockPos> allBlocks = new ArrayList<>();
        List<AbstractTankUser> localList = new ArrayList<>();

        for (BlockPos adjacentPos : findInRange(pos)) {
            if(pLevel.getBlockEntity(adjacentPos) instanceof AbstractTankUser abstractTankUser){
                localList.add(abstractTankUser);
            }
        }

        this.usingThisTank.removeIf(abstractTankUser -> !localList.contains(abstractTankUser));
        return allBlocks;
    }

    public void chargeTankFuel(int craftingFuelCost){
        if(this.getData(AttachmentRegister.BOOL)) return;
        if(this.getLevel() == null) return;
        this.inputItemHandler.getStackInSlot(0).shrink(craftingFuelCost);
        var blockstate = getLevel().getBlockState(this.getBlockPos());
        this.getLevel().sendBlockUpdated(this.getBlockPos(), blockstate, blockstate,1);
    }

    private void beamParticlesToUser(ServerLevel serverLevel, BlockPos pos, int tankSlotSize){
        if(tankSlotSize < this.getMaxSlotSize() && this.counter > 0){
            PositionGetters.getOuterRingOfRadiusRandom(pos.getCenter().subtract(0, 0.5, 0), 0.5, 2,
                positions -> {
                    Vec3 direction = pos.getCenter().add(0, 1, 0).subtract(positions).normalize();
                    ParticleHandlers.sendParticles(
                        serverLevel, processingParticle(5,1.1f, false, 0.2),
                        positions, 0,
                        direction.x, direction.y, direction.z,
                        ModHelpers.Random.nextDouble(0.08,0.12)
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

        if(tankSlotSize < this.getMaxSlotSize()) counter++;
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

