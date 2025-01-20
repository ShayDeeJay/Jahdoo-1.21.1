package org.jahdoo.block.shopping_table;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.challenge.RewardLootTables;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.utils.ModHelpers;

public class ShoppingTableEntity extends AbstractBEInventory {


    public ShoppingTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.SHOPPING_TABLE_BE.get(), pPos, pBlockState, 64);
    }

    public ItemStackHandler getItem(){
        return this.inputItemHandler;
    }

    public void setCost(ItemStack cost) {
        this.getItem().setStackInSlot(1, cost);
    }

    public ItemStack getCost() {
        return this.getItem().getStackInSlot(1);
    }

    public boolean canPurchase(){
        return !getItem().getStackInSlot(0).isEmpty();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public void setItem(ItemStack randomLootItem){
        var stackInSlot = getItem().getStackInSlot(0);
        if(!stackInSlot.isEmpty()) return;
        if(this.getLevel() instanceof ServerLevel){
            getItem().setStackInSlot(0, randomLootItem);
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(!(pLevel instanceof ServerLevel serverLevel)) return;
        if(pState.getValue(ShoppingTableBlock.TEXTURE) == 3){
            if(serverLevel.getGameTime() % 30 != 0) return;
            if(getCost().isEmpty()) return;
            insertRandomItem();
        }
    }

    public void insertRandomItem() {
        if(!(this.getLevel() instanceof ServerLevel serverLevel)) return;
        var rewards = RewardLootTables.getCompletionLoot(serverLevel, this.worldPosition.getCenter(), 0);
        var randomListElement = ModHelpers.getRandomListElement(rewards);
        RewardLootTables.attachItemData(serverLevel, JahdooRarity.getRarity(), randomListElement, false, null);
        getItem().setStackInSlot(0, randomListElement);
    }

    @Override
    public int setInputSlots() {
        return 2;
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

}
