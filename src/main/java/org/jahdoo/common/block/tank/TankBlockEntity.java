package org.jahdoo.common.block.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttachmentRegister;
import org.jahdoo.common.registers.BlockEntitiesRegister;
import org.jahdoo.common.registers.BlocksRegister;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.common.block.AbstractTankUser.findInRange;
import static org.jahdoo.common.block.tank.TankBlock.LIT;


public class TankBlockEntity extends AbstractBEInventory {

    public int glowStrength = 150;
    public List<AbstractTankUser> usingThisTank = new ArrayList<>();

    private int counter;
    private static final int INPUT = 0;

    public TankBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.TANK_BE.get(), pPos, pBlockState, 64);
        this.setData(AttachmentRegister.BOOL, false);
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

    public int getCount(){
        return this.inputItemHandler.getStackInSlot(0).getCount();
    }

    public ItemStack getRenderer() {
        return this.inputItemHandler.getStackInSlot(INPUT);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.counter);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.counter = tag.getInt("counter");
        super.loadAdditional(tag, registries);
    }

    private void setState(Level level, BlockPos pos, BlockState state){
        var using = this.usingThisTank.isEmpty();
        level.setBlock(pos, state.setValue(LIT, !using), 2);
    }

    public void chargeTankFuel(int craftingFuelCost){
        if(this.getData(AttachmentRegister.BOOL)) return;
        if(this.getLevel() == null) return;
        this.inputItemHandler.getStackInSlot(0).shrink(craftingFuelCost);
        var blockstate = getLevel().getBlockState(this.getBlockPos());
        this.getLevel().sendBlockUpdated(this.getBlockPos(), blockstate, blockstate,1);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        getTankBlockInRange(level, pos);
        this.setState(level, pos, state);

        if(!(level instanceof ServerLevel serverLevel)) return;
        int tankSlotSize = this.inputItemHandler.getStackInSlot(INPUT).getCount();
        this.beamParticlesToUser(serverLevel, pos, tankSlotSize);
        this.harvestOreBelow(serverLevel, pos, tankSlotSize);
    }

    private List<BlockPos> getTankBlockInRange(Level pLevel, BlockPos pos) {
        var allBlocks = new ArrayList<BlockPos>();
        var localList = new ArrayList<>();

        for (var adjacentPos : findInRange(pos)) {
            if(pLevel.getBlockEntity(adjacentPos) instanceof AbstractTankUser abstractTankUser){
                localList.add(abstractTankUser);
            }
        }

        this.usingThisTank.removeIf(abstractTankUser -> !localList.contains(abstractTankUser));
        return allBlocks;
    }

    private void beamParticlesToUser(ServerLevel serverLevel, BlockPos pos, int tankSlotSize){
        if(tankSlotSize < this.getMaxSlotSize() && this.counter > 0){
            PositionFinders.getOuterRingOfRadiusRandom(pos.getCenter().subtract(0, 0.5, 0), 0.5, 2,
                positions -> {
                    Vec3 direction = pos.getCenter().add(0, 1, 0).subtract(positions).normalize();
                    ParticleHandlers.sendParticles(
                        serverLevel, processingParticle(5,1.1f, false, 0.2),
                        positions, 0,
                        direction.x, direction.y, direction.z,
                        Helpers.Random.nextDouble(0.08,0.12)
                    );
                }
            );
        }
    }

    private void harvestOreBelow(ServerLevel serverLevel, BlockPos pos, int tankSlotSize){
        var blockState = serverLevel.getBlockState(pos.below());
        var harvestBlock = BlocksRegister.NEXITE_ORE.get();

        if(!(blockState.is(harvestBlock))) {
            if(this.counter > 0) this.counter = 0;
            return;
        }

        if(counter >= 200){
            if(tankSlotSize <= 64){
                var powderItem = new ItemStack(ItemsRegister.NEXITE_POWDER.get());
                var amountToCopy = Math.min(6 + tankSlotSize, 64);
                serverLevel.destroyBlock(pos.below(), false);
                this.inputItemHandler.setStackInSlot(INPUT, powderItem.copyWithCount(amountToCopy));
            }
            counter = 0;
        }

        if(tankSlotSize < this.getMaxSlotSize()) counter++;
    }

}

