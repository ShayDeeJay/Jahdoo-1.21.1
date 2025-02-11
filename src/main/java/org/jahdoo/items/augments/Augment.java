package org.jahdoo.items.augments;

import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.items.augments.AugmentItemHelper.setAugmentModificationScreen;
import static org.jahdoo.registers.DataComponentRegistry.NUMBER;


public class Augment extends Item implements MenuAccess {

    public Augment() {
        super(new Item.Properties());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.SPYGLASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        var itemStack = player.getItemInHand(interactionHand);

        if(player instanceof LocalPlayer){
            setAugmentModificationScreen(itemStack, null);
        }

        AugmentItemHelper.discoverUse(itemStack, player);
        return InteractionResultHolder.fail(player.getItemInHand(interactionHand));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.getComponents().has(CUSTOM_MODEL_DATA) && newStack.has(CUSTOM_MODEL_DATA);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSelected) {
        AugmentItemHelper.discoverTick(entity, itemStack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        var rating = DataComponentRegistry.AUGMENT_RATING.get();
        if(stack.has(rating)) return stack.get(rating) >= 20.0;
        return false;
    }

    @Override
    public @NotNull Component getName(ItemStack itemStack) {
        return AugmentItemHelper.getHoverName(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        AugmentItemHelper.getHoverText(pStack, pTooltipComponents, false, pContext.level());
    }

    @Override
    public AbstractContainerMenu getMenu() {
        return null;
    }
}
