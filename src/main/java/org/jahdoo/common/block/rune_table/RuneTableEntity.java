package org.jahdoo.common.block.rune_table;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.common.block.wand_manager.WandManagerEntity.ADDITIONAL_RUNE_SLOTS;
import static org.jahdoo.common.block.wand_manager.WandManagerEntity.DEFAULT_SLOTS;

public class RuneTableEntity extends AbstractBEInventory implements MenuProvider {

    public RuneTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.RUNE_TABLE_BE.get(), pPos, pBlockState, 64);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {}

    public boolean checkAndChargeCores(Item item, boolean charge){
        var handler = this.inputItemHandler;
        for (int i = 0; i < handler.getSlots(); i++){
            var runeTableItem = handler.getStackInSlot(i);

            if(runeTableItem.getItem().equals(item)){
                if(charge){
                    handler.getStackInSlot(i).shrink(1);
                }
                return true;
            }
        }
        return false;
    }

    public ItemStackHandler getItem(){
        return this.inputItemHandler;
    }

    public ItemStack itemSlot(){
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
    public @NotNull Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RuneTableMenu(i, inventory,this, this.data);
    }

    public void setItem(ItemStack item){
        if(this.getLevel() instanceof ServerLevel){
            getItem().setStackInSlot(0, item);
        }
    }

}
