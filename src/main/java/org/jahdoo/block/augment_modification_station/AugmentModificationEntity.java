package org.jahdoo.block.augment_modification_station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationMenu;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AugmentModificationEntity extends AbstractTankUser implements MenuProvider {

    int tickCounter;

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        this.tickCounter = pTag.getInt("tick_counter");
        super.loadAdditional(pTag, pRegistries);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("tick_counter", this.tickCounter);
        super.saveAdditional(pTag, pRegistries);
    }

    public AugmentModificationEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.AUGMENT_MODIFICATION_STATION_BE.get(), pPos, pBlockState, 1);
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        tickCounter++;
        this.assignTankBlockInRange(level, blockPos, this.setCraftingCost());
    }

    public ItemStack getInteractionSlot(){
        return this.inputItemHandler.getStackInSlot(0);
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
    public int getMaxSlotSize() {
        return 1;
    }

    @Override
    public int setCraftingCost() {
        return 20;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new AugmentModificationMenu(i, inventory,this, this.data);
    }
}
