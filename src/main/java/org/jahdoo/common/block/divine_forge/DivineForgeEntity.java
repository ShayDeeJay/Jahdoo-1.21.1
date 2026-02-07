package org.jahdoo.common.block.divine_forge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

import static org.jahdoo.common.block.wand_manager.WandManagerEntity.ADDITIONAL_RUNE_SLOTS;
import static org.jahdoo.common.block.wand_manager.WandManagerEntity.DEFAULT_SLOTS;

public class DivineForgeEntity extends AbstractBEInventory implements MenuProvider {

    public ArmorStand stand;
    public static final int MODIFICATION_SLOT = 16;

    public DivineForgeEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.RUNE_TABLE_BE.get(), pPos, pBlockState, 64);
    }

    public void tick(Level pLevel, BlockPos pos, BlockState pState) {

        if(itemSlot().isEmpty()) setPrivateTicks(0); else {
            if(getLevel() instanceof ServerLevel){
                incrementPrivateTicks();
                updateBlock();
            }
        }
    }

    public boolean checkAndChargeCores(Item item, boolean charge){
        var handler = this.getInputItemHandler();
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

    public ArmorStand getStand(Level level){
        if(this.stand == null){
            stand = EntityType.ARMOR_STAND.create(level);
        }
        return stand;
    }

    public ItemStackHandler getItem(){
        return this.getInputItemHandler();
    }

    public ItemStack itemSlot(){
        return this.getInputItemHandler().getStackInSlot(0);
    }

    public ItemStack getModificationSlot(){
        return getInputItemHandler().getStackInSlot(MODIFICATION_SLOT);
    };

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
        return 64;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RuneTableMenu(i, inventory,this, this.getData());
    }

    public void setItem(ItemStack item){
        if(this.getLevel() instanceof ServerLevel){
            getItem().setStackInSlot(0, item);
        }
    }

}
