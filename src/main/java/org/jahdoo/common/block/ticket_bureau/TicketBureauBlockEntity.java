package org.jahdoo.common.block.ticket_bureau;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.components.TicketData;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static org.jahdoo.common.block.divine_forge.DivineForge.setOuterRingPulse;
import static org.jahdoo.common.registers.mod.ElementReg.utility;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class TicketBureauBlockEntity extends AbstractTankUser {

    public ItemStack lastItem;

    public TicketBureauBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.TICKET_BUREAU_BE.get(), pos, state, 1);
    }

    @Override
    public int setCraftingCost() {
        return 10;
    }

    public ItemStack getTicketItem(){
        return this.getInputItemHandler().getStackInSlot(0);
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
        if(getPrivateTicks() > 0) decrementPrivateTicks(); else lastItem = null;

        this.assignTankBlockInRange(level, pos, setCraftingCost());
        var item = getTicketItem();
        if (!item.isEmpty() && this.hasTankAndFuel() && !CoreData.isFull(item)) {
            this.getTankEntity().usingThisTank.add(this);
            if(level instanceof ServerLevel serverLevel){
                if (Random.nextInt(10) == 0) {
                    var success = Random.nextInt(2) == 0;
                    setOuterRingPulse(serverLevel, pos, 0.5, success ? 10 : 2, 1.5, 0.3, success ? utility().textColourB() : ColourHelpers.getNegativeRed(), 40);
                    if(success){
                        SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundReg.LEVEL_UP.get(), SoundSource.BLOCKS, 1, 1.8F);
                        var copy = item.copy();
                        CoreData.increment(copy, 10);
                        if(CoreData.isFull(copy)){
                            SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1, 1.8F);
                            TicketData.initTicket(copy, 1);
                        }
                        this.getInputItemHandler().setStackInSlot(0, copy);
                    } else {
                        SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundReg.REJECT.get(), SoundSource.BLOCKS, 1, 0.5F);
                    }
                    chargeTankFuel(this.setCraftingCost());
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if(this.lastItem != null && !this.lastItem.isEmpty()){
            tag.put("stack", lastItem.save(registries));
        }
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if(tag.contains("stack")){
            this.lastItem = ItemStack.parse(registries, tag.getCompound("stack")).orElse(ItemStack.EMPTY);
        }
        super.loadAdditional(tag, registries);
    }

}
