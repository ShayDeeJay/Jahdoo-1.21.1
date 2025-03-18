package org.jahdoo.common.block.shopping_table;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.ascension.trading_post.RewardLootTables;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.ascension.attachments.PlayerWallet.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.*;
import static org.jahdoo.common.registers.BlockEntityReg.*;

public class ShoppingTableEntity extends AbstractBEInventory {

    public CurrencyConverter itemCosts = EMPTY;
    public int ticks;

    public ShoppingTableEntity(BlockPos pos, BlockState state) {
        super(SHOPPING_TABLE_BE.get(), pos, state, 64);
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

    public void setCost(CurrencyConverter cost) {
        this.itemCosts = cost;
    }

    public ItemStackHandler getItem(){
        return this.inputItemHandler;
    }

    public boolean canPurchase(){
        return !getItem().getStackInSlot(0).isEmpty();
    }

    public ItemStack getCurrencyType() {
        return CurrencyConverter.getItemStack(this.itemCosts);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveData(tag, itemCosts);
        tag.putInt("ticks", ticks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.itemCosts = loadData(tag);
        this.ticks = tag.getInt("ticks");
    }

    public void setItem(ItemStack randomLootItem){
        var stackInSlot = getItem().getStackInSlot(0);
        if(!stackInSlot.isEmpty()) return;

        if(this.getLevel() instanceof ServerLevel){
            getItem().setStackInSlot(0, randomLootItem);
        }
    }

    public void insertRandomItem() {
        if(!(this.getLevel() instanceof ServerLevel serverLevel)) return;
        var rewards = RewardLootTables.getCompletionLoot(serverLevel, this.worldPosition.getCenter(), 0);

        if(!rewards.isEmpty()){
            var randomListElement = Helpers.listRandom(rewards);
            RewardLootTables.attachItemData(serverLevel, JahdooRarity.getRarity(), randomListElement, false, null);
            getItem().setStackInSlot(0, randomListElement);
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(!(level instanceof ServerLevel serverLevel)) return;
        ticks++;
        this.updateBlock();

        if(state.getValue(ShoppingTableBlock.TEXTURE) == 3){
            if(serverLevel.getGameTime() % 30 != 0) return;
            if(itemCosts == EMPTY) return;
            insertRandomItem();
        }
    }

}
