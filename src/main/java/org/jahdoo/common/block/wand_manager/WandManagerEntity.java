package org.jahdoo.common.block.wand_manager;

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
import org.jahdoo.common.registers.BlockEntityReg;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

public class WandManagerEntity extends AbstractBEInventory implements MenuProvider {

    public static final int DEFAULT_SLOTS = 5;
    public static final int ADDITIONAL_RUNE_SLOTS = 16;
    public int privateTicks;
    public ItemStack itemStack;

    public WandManagerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.WAND_MANAGER_TABLE_BE.get(), pos, state, 1);
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        this.privateTicks++;
    }

    public ItemStack getWandSlot(){
        return this.getInputItemHandler().getStackInSlot(0);
    }

    @Override
    public int setInputSlots() {
        return DEFAULT_SLOTS + ADDITIONAL_RUNE_SLOTS;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeInput() {
        return 1;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 1;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.jahdoo.wand_manager");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WandManagerMenu(id, inventory, this, this.getData());
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ticks", this.privateTicks);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.privateTicks = tag.getInt("ticks");
    }
}

