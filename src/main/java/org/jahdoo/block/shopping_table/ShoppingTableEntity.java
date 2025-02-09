package org.jahdoo.block.shopping_table;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.challenge.RewardLootTables;
import org.jahdoo.challenge.trading_post.ItemCosts;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.challenge.trading_post.ItemCosts.*;

public class ShoppingTableEntity extends AbstractBEInventory {

    ItemCosts itemCosts;

    public ShoppingTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.SHOPPING_TABLE_BE.get(), pPos, pBlockState, 64);
    }

    public ItemStackHandler getItem(){
        return this.inputItemHandler;
    }

    public void setCost(ItemCosts cost) {
        this.itemCosts = cost;
    }

    public int getCost(){
        return itemCosts.value();
    }

    public ItemStack getCurrencyType() {
        return getItemStack(itemCosts.CurrencyType());
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
        for (var player : serverLevel.players()) {
            if(level instanceof CustomLevel){
                player.addEffect(new JahdooMobEffect(MobEffects.REGENERATION, 5, 5));
                player.addEffect(new JahdooMobEffect(MobEffects.SATURATION, 5, 5));
            }
        }
        if(pState.getValue(ShoppingTableBlock.TEXTURE) == 3){
            if(serverLevel.getGameTime() % 30 != 0) return;
            if(getCurrencyType().isEmpty()) return;
            insertRandomItem();
        }
    }

    public void insertRandomItem() {
        if(!(this.getLevel() instanceof ServerLevel serverLevel)) return;
        var rewards = RewardLootTables.getCompletionLoot(serverLevel, this.worldPosition.getCenter(), 0);
        if(!rewards.isEmpty()){
            var randomListElement = ModHelpers.getRandomListElement(rewards);
            RewardLootTables.attachItemData(serverLevel, JahdooRarity.getRarity(), randomListElement, false, null);
            getItem().setStackInSlot(0, randomListElement);
        }
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
        saveData(pTag, itemCosts);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.itemCosts = loadData(pTag);
    }

}
