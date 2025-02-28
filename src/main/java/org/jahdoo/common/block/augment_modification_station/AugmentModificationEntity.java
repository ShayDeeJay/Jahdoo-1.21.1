package org.jahdoo.common.block.augment_modification_station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.registers.BlockEntityReg;

public class AugmentModificationEntity extends AbstractBEInventory implements MenuProvider {
    
    private int tickCounter;

    public AugmentModificationEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.AUGMENT_MODIFICATION_STATION_BE.get(), pos, state, 1);
    }

    public void tick(Level level, BlockPos blockPos, BlockState state) {
        tickCounter++;
    }

    public ItemStack getInteractionSlot(){
        return this.inputItemHandler.getStackInSlot(0);
    }

    @Override
    public int setInputSlots() {
        return 4;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSize() {
        return 64;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new AugmentModificationMenu(i, inventory,this, this.data);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.tickCounter = tag.getInt("tick_counter");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("tick_counter", this.tickCounter);
    }
    
}
