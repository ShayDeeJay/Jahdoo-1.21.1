package org.jahdoo.items.wand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.client.item_renderer.WandItemRenderer;
import org.jahdoo.client.overlays.StatScreen;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.items.JahdooItem;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.runes.RuneItemHelper;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

import static org.jahdoo.items.wand.WandAnimations.*;
import static org.jahdoo.items.wand.WandItemHelper.*;
import static org.jahdoo.registers.DataComponentRegistry.*;

public class WandItem extends BlockItem implements GeoItem, JahdooItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String location;

    public static Properties wandProperties(){
        return new Item.Properties()
            .stacksTo(1)
            .component(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), WandAbilityHolder.DEFAULT)
            .component(WAND_DATA.get(), WandData.DEFAULT)
            .fireResistant();
    }

    public WandItem(String location) {
        super(BlocksRegister.WAND.get(), wandProperties());
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
        this.location = location;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 72000;
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        return onPlace(pContext);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSoltSelected) {
        if(!(entity instanceof Player player)) return;
        var itemInMain = player.getMainHandItem();
        var itemInOff = player.getOffhandItem();
        var isItemInMain = itemInMain == itemStack;
        var isItemInOff = itemInOff == itemStack;
        var interactState = itemStack.get(INTERACTION_HAND);

        validateRuneHand(itemStack, player, interactState, isItemInMain, isItemInOff);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.addAll(WandItemHelper.getItemModifiers(pStack, pContext.level()));

        var list = pStack.getAttributeModifiers().modifiers().stream().toList();
        if(list.size() > 3){
            for (var entry : list.subList(3, list.size())) {
                pTooltipComponents.add(RuneData.RuneHelpers.standAloneAttributes(entry));
            }
        }

    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return WandItemHelper.getItemName(pStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var item = player.getItemInHand(interactionHand);

        if(level.isClientSide){
//            Minecraft.getInstance().setScreen(new StatScreen(player));
        }

        if (canOffHand(player, interactionHand, true)) {
            player.startUsingItem(interactionHand);
            CastHelper.use(player);
            return InteractionResultHolder.pass(item);
        }

        return InteractionResultHolder.fail(player.getOffhandItem());
    }

    public JahdooRarity getRarity(){
        var wandData = this.components().get(JAHDOO_RARITY.get());
        var getWandData = wandData == null ? 0 : wandData;
        return JahdooRarity.getAllRarities().get(getWandData);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
            new GeoRenderProvider() {
                private WandItemRenderer renderer;
                @Override
                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    if (this.renderer == null) this.renderer = new WandItemRenderer();
                    return this.renderer;
                }
            }
        );
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
