package org.jahdoo.common.items.wand;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.jahdoo.common.items.wand.WandAnimations.*;
import static org.jahdoo.common.items.wand.WandItemHelper.*;
import static org.jahdoo.common.registers.ComponentReg.*;

public class WandItem extends Item implements GeoItem, JahdooItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String location;

    public WandItem(String location) {
        super(wandProperties());
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
        this.location = location;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        wandItemRenderer(consumer);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return WandItemHelper.getItemName(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> toolTip, TooltipFlag flag) {
        toolTip.addAll(WandItemHelper.getItemModifiers(stack, context.level()));
        bonusModifierTooltip(stack, toolTip, context);
    }

    public static Properties wandProperties(){
        return new Item.Properties()
            .stacksTo(1)
            .durability(300)
            .component(DataComponents.BUNDLE_CONTENTS, new BundleContents(new ArrayList<>()))
            .component(WAND_DATA.get(), WandData.DEFAULT)
            .component(RUNE_HOLDER, RuneHolder.makeRuneSlots(0, 40))
            .component(ComponentReg.JAHDOO_RARITY, 0);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
            ? Optional.ofNullable(stack.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new)
            : Optional.empty();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 0, state -> state.setAndContinue(IDLE_ANIMATION)));
        controllers.add(new AnimationController<>(this, "Activation", 0, state -> PlayState.CONTINUE)
            .triggerableAnim(SINGLE_CAST_ID, SINGLE_CAST)
            .triggerableAnim(CANT_CAST_ID, CANT_CAST)
            .triggerableAnim(HOLD_CAST_ID, HOLD_CAST)
            .triggerableAnim(ROTATION_CAST_ID, ROTATION_CAST)
        );
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSlotSelected) {
        if(!(entity instanceof Player player)) return;
        var itemInMain = player.getMainHandItem();
        var itemInOff = player.getOffhandItem();
        var isItemInMain = itemInMain == itemStack;
        var isItemInOff = itemInOff == itemStack;
        var interactState = itemStack.get(INTERACTION_HAND);

        canOffhandWand(itemStack, player, interactState, isItemInMain, isItemInOff);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var item = player.getItemInHand(interactionHand);
        var data = player.getData(AttachmentReg.CASTER_DATA.get());


        if(!level.isClientSide){
            data.clearData();
            if (canOffHand(player, interactionHand, true)) {
                player.startUsingItem(interactionHand);
                CastHelper.use(player);
                return InteractionResultHolder.pass(item);
            }
        }

        return InteractionResultHolder.fail(item);
    }

    private static void debugKillAll(Level level, Player player) {
        if(level instanceof ServerLevel serverLevel && player.isCreative() && player.isShiftKeyDown()){
            for (var entity : serverLevel.getEntities().getAll()) {
                if(!(entity instanceof Player)){
                    entity.kill();
                }
            }
        }
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return true;
    }


}
