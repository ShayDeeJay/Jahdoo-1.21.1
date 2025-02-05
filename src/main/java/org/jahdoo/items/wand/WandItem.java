package org.jahdoo.items.wand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.item_renderer.WandItemRenderer;
import org.jahdoo.client.overlays.ChoiceSelectionScreen;
import org.jahdoo.client.overlays.StatScreen;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.items.JahdooItem;
import org.jahdoo.registers.*;
import org.jahdoo.utils.Maths;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.sounds.SoundEvents.ALLAY_THROW;
import static net.minecraft.sounds.SoundEvents.EVOKER_PREPARE_SUMMON;
import static net.minecraft.world.InteractionHand.*;
import static org.jahdoo.block.wand.WandBlockEntity.GET_WAND_SLOT;
import static org.jahdoo.items.wand.WandAnimations.*;
import static org.jahdoo.items.wand.WandItemHelper.*;
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.DataComponentRegistry.*;
import static org.jahdoo.registers.ElementRegistry.getElementFromWand;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.PositionGetters.getInnerRingOfRadiusRandom;

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
        if(!interactionTarget.level().isClientSide){
            var maxHealth = Attributes.MAX_HEALTH;
//            interactionTarget.getAttributes().getInstance(maxHealth).setBaseValue(200);
            System.out.println(interactionTarget.getAttributes().getValue(maxHealth));
            System.out.println(interactionTarget.getHealth());
//            System.out.println(interactionTarget.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(200));
//            for (var syncableAttribute : interactionTarget.getAttributes().getInstance().) {
//                System.out.println(interactionTarget.getAttributes().getValue(syncableAttribute.getAttribute()));
//            }
        }
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.addAll(WandItemHelper.getItemModifiers(pStack));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return WandItemHelper.getItemName(pStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var item = player.getItemInHand(interactionHand);

        if(!level.isClientSide){
//            Minecraft.getInstance().setScreen(new ChoiceSelectionScreen());
//            return InteractionResultHolder.pass(item);
            var getEntity = EntityType.ZOMBIE.create(level);
            var maxHealth = Attributes.MAX_HEALTH;
            var instance = getEntity.getAttributes().getInstance(maxHealth);
//            System.out.println(instance.getValue());
//            instance.setBaseValue(25);
//            System.out.println(getEntity.getHealth());
//
//            getEntity.moveTo(player.position());
//            level.addFreshEntity(getEntity);
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
