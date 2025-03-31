package org.jahdoo.common.items.wand;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ComponentReg;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.PositionFinders.innerRadiusRandom;
import static org.jahdoo.common.items.wand.WandAnimations.*;
import static org.jahdoo.common.items.wand.WandItemHelper.*;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.particle.ParticleStore.PLUS_PARTICLE;
import static org.jahdoo.common.registers.BlockReg.WAND;
import static org.jahdoo.common.registers.ComponentReg.*;

public class WandItem extends BlockItem implements GeoItem, JahdooItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String location;

    public WandItem(String location) {
        super(WAND.get(), wandProperties());
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
    public InteractionResult place(BlockPlaceContext context) {
        return onPlace(context);
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
            .component(ComponentReg.WAND_ABILITY_HOLDER.get(), WandAbilityHolder.DEFAULT)
            .component(WAND_DATA.get(), WandData.DEFAULT)
            .component(RUNE_HOLDER, RuneHolder.makeRuneSlots(0, 40))
            .component(ComponentReg.JAHDOO_RARITY, 0);
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
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSoltSelected) {
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

        if(player.isCreative() && player.isShiftKeyDown()){
            if (level instanceof ServerLevel serverLevel) {
                for (var entity : serverLevel.getEntities().getAll()) {
                    if (!(entity instanceof Player)) {
                        entity.kill();
                    }
                }
            }
        }

        if (canOffHand(player, interactionHand, true)) {
            player.startUsingItem(interactionHand);
            CastHelper.use(player);
            return InteractionResultHolder.pass(item);
        }

        return InteractionResultHolder.fail(item);
    }

    public static void test(ItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci){

        float scale = 2; // Modify this to adjust scale dynamically
        if(entity.tickCount % 10 == 0){
            var getPositions = innerRadiusRandom(entity.position().subtract(0, 0.45, 0), scale / 4, scale);

            for (var vec3 : getPositions) {
                int bState = 1;
                var byState = bState == 0 ? color(207, 62, 62) : bState == 1 ? color(43, 193, 252) : color(59, 173, 80);
                var genericParticle = ParticleHandlers.genericParticle(PLUS_PARTICLE, 16, scale * 4, byState, byState, false);
                sendParticles(entity.level(), genericParticle, vec3, 0, 0, 0.5, 0, 35);
            }
        }

        poseStack.translate(0, 0.5, 0);
        poseStack.scale(scale, scale, scale);
    }

}
