package org.jahdoo.client.gui.block.wand_block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class AugmentSlot extends SlotItemHandler {

    WandBlockMenu wandBlockMenu;

    public AugmentSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        WandBlockMenu wandBlockMenu
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.wandBlockMenu = wandBlockMenu;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return itemStack.is(ItemsRegister.AUGMENT_ITEM.get()) &&
            itemStack.getComponents().has(DataComponentRegistry.WAND_ABILITY_HOLDER.get()) &&
            doesWandHaveAbility(DataComponentHelper.getAbilityTypeItemStack(itemStack)) ||
            canSwapCarried();

    }

    public boolean doesWandHaveAbility(String abilityLocation){
        ItemStack wandItem = wandBlockMenu.getWandBlockEntity().getWandItemFromSlot();
        Map<String, AbilityHolder> abilityHolderMap = wandItem
            .get(DataComponentRegistry.WAND_ABILITY_HOLDER.get())
            .abilityProperties();
        return !abilityHolderMap.containsKey(abilityLocation);
    }

    public boolean canSwapCarried(){
        if(this.wandBlockMenu.getCarried().is(ItemsRegister.AUGMENT_ITEM.get())){
            String storedItemLocationID = DataComponentHelper.getAbilityTypeItemStack(this.getItem());
            String carriedItemLocationID = DataComponentHelper.getAbilityTypeItemStack(wandBlockMenu.getCarried());
            return Objects.equals(storedItemLocationID, carriedItemLocationID) ;
        }
        return false;
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack itemStack) {
        if(!this.hasItem()) return;
        ItemStack wandItem = this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getStackInSlot(0);
        String ability = DataComponentHelper.getAbilityTypeItemStack(itemStack);
        String wandAbility = DataComponentHelper.getAbilityTypeItemStack(wandItem);

        if(Objects.equals(ability, wandAbility)){
            ItemStack copiedStack = wandItem.copy();
            DataComponentHelper.setAbilityTypeItemStack(copiedStack, "");
            this.wandBlockMenu.getWandBlockEntity().inputItemHandler.setStackInSlot(0, copiedStack);
        }

        wandItem.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()).abilityProperties().remove(ability);
    }



    @Override
    public void setChanged() {
        this.wandBlockMenu.getWandBlockEntity().setAllAbilities();
        if(this.wandBlockMenu.getCarried().isEmpty() || this.wandBlockMenu.getCarried().getItem() instanceof Augment){
            ModHelpers.getSoundWithPosition(
                Objects.requireNonNull(this.wandBlockMenu.getWandBlockEntity().getLevel()),
                this.wandBlockMenu.getWandBlockEntity().getBlockPos(),
                SoundEvents.ARMOR_EQUIP_CHAIN.value()
            );
        }
    }

    @Override
    public boolean isHighlightable() {
        return false;
    }
}