package org.jahdoo.block.rune_table;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.client.gui.block.rune_table.RuneTableMenu;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.block.wand_block_manager.WandManagerEntity.ADDITIONAL_RUNE_SLOTS;
import static org.jahdoo.block.wand_block_manager.WandManagerEntity.DEFAULT_SLOTS;

public class RuneTableEntity extends AbstractBEInventory implements MenuProvider {

    public RuneTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.RUNE_TABLE_BE.get(), pPos, pBlockState, 64);
    }

    public ItemStackHandler getItem(){
        return this.inputItemHandler;
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public void setItem(ItemStack item){
        if(this.getLevel() instanceof ServerLevel){
            getItem().setStackInSlot(0, item);
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(!(pLevel instanceof ServerLevel serverLevel)) return;
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
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RuneTableMenu(i, inventory,this, this.data);
    }
}
