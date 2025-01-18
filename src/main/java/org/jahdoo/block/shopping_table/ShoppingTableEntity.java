package org.jahdoo.block.shopping_table;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.registers.BlockEntitiesRegister;

import static org.jahdoo.block.shopping_table.ShoppingTableBlock.TEXTURE;
import static org.jahdoo.challenge.RewardLootTables.*;
import static org.jahdoo.challenge.RewardLootTables.attachItemData;
import static org.jahdoo.client.block_renderer.ShoppingTableRenderer.*;
import static org.jahdoo.client.block_renderer.ShoppingTableRenderer.DisplayDirection.*;
import static org.jahdoo.utils.ModHelpers.*;

public class ShoppingTableEntity extends AbstractBEInventory {

    public DisplayDirection direction;

    public ShoppingTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.SHOPPING_TABLE_BE.get(), pPos, pBlockState, 64);
        if(this.getLevel() instanceof ServerLevel serverLevel){
            this.inputItemHandler.setStackInSlot(0, getRandomListElement(getCompletionLoot(serverLevel, this.worldPosition.getCenter(), 100)));
        }
    }

    public ItemStackHandler getItem(){
        return this.inputItemHandler;
    }

    public void setItem(){
        var stackInSlot = getItem().getStackInSlot(0);
        if(!stackInSlot.isEmpty()) return;
        if(this.getLevel() instanceof ServerLevel serverLevel){
            var rarity = JahdooRarity.getRarity();
            var loot = getCompletionLoot(serverLevel, this.worldPosition.getCenter(), 1);
            var randomLootItem = getRandomListElement(loot);
            attachItemData(serverLevel, rarity, randomLootItem);
            getItem().setStackInSlot(0, randomLootItem);
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        setItem();
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
