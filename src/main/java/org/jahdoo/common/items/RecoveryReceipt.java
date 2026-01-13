package org.jahdoo.common.items;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.InteractionResultHolder.fail;
import static net.minecraft.world.InteractionResultHolder.success;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.*;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.*;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter.*;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.throwNewItem;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;

public class RecoveryReceipt extends BaseItem {

    public RecoveryReceipt() { super(new Properties()); }

    public static final Pair<CoinProperties, CurrencyConverter> SILVER_CHARGE =
        Pair.of(BRONZE, setBronzeCost(20));

    public static final Pair<CoinProperties, CurrencyConverter> GOLD_CHARGE =
        Pair.of(SILVER, setSilverCost(20));

    public static final Pair<CoinProperties, CurrencyConverter> PLATINUM_CHARGE =
        Pair.of(GOLD, setGoldCost(20));

    @Override
    public ItemStack getRecycleItem() {
        return new ItemStack(ItemReg.AUGMENT_CORE);
    }

    @Override
    public double customRecycleChance(ItemStack itemStack) {
        return 100;
    }

    @Override
    public Component getName(ItemStack stack) {
        return withStyleComponent("Recovery Receipt", SUB_HEADER_COLOUR);
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
        var stack = player.getItemInHand(usedHand);
        var getPrice = getRecoveryCost(stack);
        if (getPrice == null) return fail(stack);

        if (canPurchase(getPrice.getSecond(), getWalletValue(player))) {
            player.startUsingItem(usedHand);
            return success(stack);
        } else {
            player.stopUsingItem();
            var message = "Insufficient Funds";
            player.displayClientMessage(withStyleComponent(message, NEGATIVE_RED), true);
            player.playSound(SoundEvents.CAMEL_DASH_READY, 1F, 0.6F);
            return fail(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var getPrice = getRecoveryCost(stack);
        if (getPrice == null) return;

        tooltipComponents
            .add(withStyleComponent("Cost: ", OFF_WHITE)
            .copy()
            .append(withStyleComponent("20" + " " + getPrice.getFirst().getSerializedName(), getPrice.getFirst().getTextColour())));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        var confirmationTime = 50;
        if (!(livingEntity instanceof Player player)) return;

        if(player.isUsingItem() && player.getTicksUsingItem() >= confirmationTime){
            var content = stack.get(DataComponents.BUNDLE_CONTENTS);
            var getPrice = getRecoveryCost(stack);
            if(content != null && getPrice != null){
                for (var itemStack : content.items()) throwNewItem(player, itemStack);

                player.playSound(SoundReg.COINBOX_OPEN.get(), 1, 1.8F);
                purchase(getPrice.getSecond(), player);
                stack.shrink(1);
                player.stopUsingItem();
            }
        } else {
            if(player.getTicksUsingItem() % 14 == 0){
                player.playSound(SoundEvents.ENDER_CHEST_OPEN, 1, 2F);
            }
        }
    }

    private static @Nullable Pair<CoinProperties, CurrencyConverter> getRecoveryCost(ItemStack stack) {
        var getTicket = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(getTicket == null) return null;

        return switch (getTicket.value()){
            case 1 -> SILVER_CHARGE;
            case 2 -> GOLD_CHARGE;
            default -> PLATINUM_CHARGE;
        };
    }

}
