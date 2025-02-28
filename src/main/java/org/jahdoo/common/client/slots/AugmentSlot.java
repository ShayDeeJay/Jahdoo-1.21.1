package org.jahdoo.common.client.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.common.block.wand.WandBlockMenu;
import org.jahdoo.common.items.augments.Augment;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.minecraft.sounds.SoundEvents.*;
import static org.jahdoo.common.components.DataComponentHelper.*;
import static org.jahdoo.common.registers.ComponentReg.*;

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
    public boolean isHighlightable() {
        return false;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return itemStack.is(ItemReg.AUGMENT.get()) &&
            itemStack.getComponents().has(WAND_ABILITY_HOLDER.get()) &&
            doesWandHaveAbility(getAbilityTypeItemStack(itemStack)) ||
            canSwapCarried();
    }

    public boolean doesWandHaveAbility(String abilityLocation){
        var wandItem = wandBlockMenu.getWandBlockEntity().getWandItemFromSlot();
        var abilityHolderMap = wandItem
            .get(WAND_ABILITY_HOLDER.get())
            .abilityProperties();
        return !abilityHolderMap.containsKey(abilityLocation);
    }

    public boolean canSwapCarried(){
        if(this.wandBlockMenu.getCarried().is(ItemReg.AUGMENT.get())){
            var storedID = getAbilityTypeItemStack(this.getItem());
            var carriedID = getAbilityTypeItemStack(wandBlockMenu.getCarried());
            return Objects.equals(storedID, carriedID) ;
        }
        return false;
    }

    @Override
    public void setChanged() {
        var menu = this.wandBlockMenu;
        var entity = menu.getWandBlockEntity();

        entity.setAllAbilities();

        if(menu.getCarried().isEmpty() || menu.getCarried().getItem() instanceof Augment){
            Helpers.getSoundWithPosition(
                Objects.requireNonNull(entity.getLevel()),
                entity.getBlockPos(), VAULT_EJECT_ITEM, 1, 1.2F
            );
        }
    }

    @Override
    public void onTake(@NotNull Player player, ItemStack itemStack) {
        if(!this.hasItem()) return;

        var handler = this.wandBlockMenu.getWandBlockEntity().inputItemHandler;
        var wandItem = handler.getStackInSlot(0);
        var ability = getAbilityTypeItemStack(itemStack);
        var wandAbility = getAbilityTypeItemStack(wandItem);

        if(Objects.equals(ability, wandAbility)){
            ItemStack copiedStack = wandItem.copy();
            setAbilityTypeItemStack(copiedStack, "");
            handler.setStackInSlot(0, copiedStack);
        }

        wandItem.get(WAND_ABILITY_HOLDER.get()).abilityProperties().remove(ability);
    }
}