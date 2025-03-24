package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.augments.AugmentItemHelper;

import java.util.List;
import java.util.Optional;

import static net.minecraft.util.FastColor.ARGB32.color;

public class RecoveryReceipt extends Item {

    public RecoveryReceipt() { super(new Properties()); }

    @Override
    public Component getName(ItemStack stack) {
        return Helpers.withStyleComponent("Recovery Receipt", color(161, 104, 251));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
            ? Optional.ofNullable(stack.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new)
            : Optional.empty();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var testPrice = PlayerWallet.CurrencyConverter.setBronzeCost(100);
        var bronze = PlayerWallet.CoinProperties.BRONZE;
        tooltipComponents.add(Helpers.withStyleComponent("Cost: ", ColourStore.OFF_WHITE).copy().append(Helpers.withStyleComponent( testPrice.bronze() + " " + bronze.getSerializedName(), bronze.getTextColour())));
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int remainingUseDuration) {
        var confirmationTime = 50;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        var hand = serverPlayer.getItemInHand(serverPlayer.getUsedItemHand());
        if(serverPlayer.isUsingItem() && serverPlayer.getTicksUsingItem() >= confirmationTime){
            var content = hand.get(DataComponents.BUNDLE_CONTENTS);

            if(content != null){
                for (var itemStack : content.items()) {
                    AugmentItemHelper.throwNewItem(serverPlayer, itemStack);
                }
            }
            hand.shrink(1);
            serverPlayer.stopUsingItem();
        }

        super.onUseTick(level, player, stack, remainingUseDuration);
    }

}
