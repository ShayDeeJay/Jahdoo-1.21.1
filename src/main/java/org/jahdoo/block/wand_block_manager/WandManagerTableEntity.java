package org.jahdoo.block.wand_block_manager;

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
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.client.gui.block.wand_manager_table.WandManagerMenu;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.Nullable;

public class WandManagerTableEntity extends AbstractBEInventory implements MenuProvider {
    public static final int DEFAULT_SLOTS = 4;
    public static final int ADDITIONAL_RUNE_SLOTS = 12;
    public int privateTicks;
    public ItemStack itemStack;

    public WandManagerTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.WAND_MANAGER_TABLE_BE.get(), pPos, pBlockState, 1);
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        this.privateTicks++;
    }

    public ItemStack getWandSlot(){
        return this.inputItemHandler.getStackInSlot(0);
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
    public int getMaxSlotSize() {
        return 64;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.jahdoo.wand_manager");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("ticks", this.privateTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.privateTicks = pTag.getInt("ticks");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WandManagerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

}

