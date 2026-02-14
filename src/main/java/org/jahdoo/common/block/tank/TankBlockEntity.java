package org.jahdoo.common.block.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.block.ticket_bureau.TicketBureauBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.common.block.AbstractTankUser.findInRange;
import static org.jahdoo.common.block.tank.TankBlock.LIT;
import static org.jahdoo.common.particle.ParticleStore.SOFT_MOVE_PARTICLE;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.processingParticle;


public class TankBlockEntity extends AbstractBEInventory {

    public int glowStrength = 150;
    public List<AbstractTankUser> usingThisTank = new ArrayList<>();
    private static final int INIT_TANK_CAPACITY = 64;
    private int maxTankSize;
    private int counter;
    private static final int INPUT = 0;

    public TankBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.TANK_BE.get(), pPos, pBlockState, 64);
        this.setData(AttachmentReg.BOOL, false);
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
        return INIT_TANK_CAPACITY;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 0;
    }

    public void increaseTankSize(int increment){
        maxTankSize += increment;
    }

    public int getCount(){
        return this.getInputItemHandler().getStackInSlot(0).getCount();
    }

    public ItemStack getRenderer() {
        return this.getInputItemHandler().getStackInSlot(INPUT);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.counter);
        tag.putInt("tankSize", maxTankSize);
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.counter = tag.getInt("counter");
        this.maxTankSize = tag.getInt("tankSize");
        super.loadAdditional(tag, registries);
    }

    private void setState(Level level, BlockPos pos, BlockState state){
        var using = this.usingThisTank.isEmpty();
        level.setBlock(pos, state.setValue(LIT, !using), 2);
    }

    public void chargeTankFuel(int craftingFuelCost){
        if(this.getData(AttachmentReg.BOOL)) return;
        if(this.getLevel() == null) return;
        this.getInputItemHandler().getStackInSlot(0).shrink(craftingFuelCost);
        var blockstate = getLevel().getBlockState(this.getBlockPos());
        this.getLevel().sendBlockUpdated(this.getBlockPos(), blockstate, blockstate,1);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        getTankBlockInRange(level, pos);
        this.setState(level, pos, state);

        if(!this.usingThisTank.isEmpty()){
            for (var abstractTankUser : usingThisTank) {
                var center = pos.getCenter();
                var playerP = abstractTankUser.getBlockPos().getCenter();
                var pPos = new Vec3(center.x - playerP.x, center.y - playerP.y, center.z - playerP.z);
                var isBureau = abstractTankUser instanceof TicketBureauBlockEntity;
                level.addParticle(
                    new GenericParticleOptions(SOFT_MOVE_PARTICLE, ElementReg.utility().partColourA(), 0, Random.nextInt(10, 40), Random.nextFloat(2, 3.5F), false, 0),
                    playerP.x + 0.0,
                    playerP.y + (isBureau ? 1.4 : 2),
                    playerP.z + 0.0,
                    pPos.x + Random.nextFloat(0.3F, 0.7F) - 0.5,
                    pPos.y - Random.nextFloat(0.3F, 0.7F) - (isBureau ? 0.8 : 1.6),
                    pPos.z + Random.nextFloat(0.3F, 0.7F) - 0.5
                );
            }
        }

        if(!(level instanceof ServerLevel serverLevel)) return;
        int tankSlotSize = this.getInputItemHandler().getStackInSlot(INPUT).getCount();
        this.beamParticlesToUser(serverLevel, pos, tankSlotSize);
        this.harvestOreBelow(serverLevel, pos, tankSlotSize);
    }

    private void getTankBlockInRange(Level pLevel, BlockPos pos) {
        var localList = new ArrayList<>();

        for (var adjacentPos : findInRange(pos)) {
            if(pLevel.getBlockEntity(adjacentPos) instanceof AbstractTankUser abstractTankUser){
                localList.add(abstractTankUser);
            }
        }

        this.usingThisTank.removeIf(abstractTankUser -> !localList.contains(abstractTankUser));
    }

    private void beamParticlesToUser(ServerLevel serverLevel, BlockPos pos, int tankSlotSize){
        if(tankSlotSize < this.getMaxSlotSizeInput() && this.counter > 0){
            PositionFinders.getOuterRingOfRadiusRandom(pos.getCenter().subtract(0, 0.5, 0), 0.5, 2,
                positions -> {
                    Vec3 direction = pos.getCenter().add(0, 1, 0).subtract(positions).normalize();
                    ParticleHandlers.sendParticles(
                        serverLevel, processingParticle(5,1.1f, false, 0.2),
                        positions, 0,
                        direction.x, direction.y, direction.z,
                        JahdooHelpers.Random.nextDouble(0.08,0.12)
                    );
                }
            );
        }
    }

    private void harvestOreBelow(ServerLevel serverLevel, BlockPos pos, int tankSlotSize){
        var blockState = serverLevel.getBlockState(pos.below());
        var harvestBlock = BlockReg.NEXITE_ORE.get();

        if(!(blockState.is(harvestBlock))) {
            if(this.counter > 0) this.counter = 0;
            return;
        }

        if(counter >= 200){
            if(tankSlotSize <= 64){
                var powderItem = new ItemStack(ItemReg.NEXITE_POWDER.get());
                var amountToCopy = Math.min(6 + tankSlotSize, 64);
                serverLevel.destroyBlock(pos.below(), false);
                this.getInputItemHandler().setStackInSlot(INPUT, powderItem.copyWithCount(amountToCopy));
            }
            counter = 0;
        }

        if(tankSlotSize < this.getMaxSlotSizeInput()) counter++;
    }



}

