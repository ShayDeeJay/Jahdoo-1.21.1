package org.jahdoo.items.wand;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.item_renderer.WandItemRenderer;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.items.JahdooItem;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ModHelpers;
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
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.DataComponentRegistry.INTERACTION_HAND;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.PositionGetters.getInnerRingOfRadiusRandom;

public class WandItem extends BlockItem implements GeoItem, JahdooItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String location;

    public static Properties wandProperties(){
        return new Properties()
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
        var clickedPos = pContext.getClickedPos();
        var player = pContext.getPlayer();
        var level = pContext.getLevel();
        var itemStack = pContext.getItemInHand();

        if (player == null || !player.isShiftKeyDown() || !player.onGround()) return InteractionResult.PASS;
        if(!pContext.getLevel().getBlockState(clickedPos).isEmpty()) return InteractionResult.FAIL;

        level.setBlockAndUpdate(clickedPos, BlocksRegister.WAND.get().defaultBlockState());
        var blockEntity = level.getBlockEntity(clickedPos);
        if (!(blockEntity instanceof WandBlockEntity wandBlockEntity)) return InteractionResult.FAIL;

        playPlaceSound(level, pContext.getClickedPos());
        var copiedWand = itemStack.copyWithCount(1);
        player.setItemInHand(pContext.getHand(), ItemStack.EMPTY);
        wandBlockEntity.inputItemHandler.setStackInSlot(GET_WAND_SLOT, copiedWand);
        wandBlockEntity.updateView();
        var getType = getElementByWandType(wandBlockEntity.getWandItemFromSlot().getItem());
        if(!getType.isEmpty()){
            var element = getType.getFirst();
            var par1 = bakedParticleOptions(element.getTypeId(), 10, 1f, false);
            var par2 = genericParticleOptions(GENERIC_PARTICLE_SELECTION, element, 10, 1f, false, 0.3);
            getInnerRingOfRadiusRandom(clickedPos, 0.1, 20,
                positions -> this.placeParticle(level, positions, Random.nextInt(0, 3) == 0 ? par1 : par2)
            );
        }
        return InteractionResult.SUCCESS;
    }

    public void placeParticle(Level level, Vec3 pos, ParticleOptions par1){
        var randomY = Random.nextDouble(0.1 , 0.2);
        var randomStartY = Random.nextDouble(0.05, 0.5);
        level.addParticle(par1, pos.x, pos.y - randomStartY, pos.z, 0, randomY, 0);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    protected @NotNull SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return SoundEvents.EMPTY;
    }

    public void playPlaceSound(Level level, BlockPos bPos){
        ModHelpers.getSoundWithPosition(level, bPos, ALLAY_THROW, 1, 0.8f);
        ModHelpers.getSoundWithPosition(level, bPos, EVOKER_PREPARE_SUMMON, 0.4f, 1.6f);
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

    private static void validateRuneHand(
        ItemStack itemStack,
        Player player,
        Integer interactState,
        boolean isItemInMain,
        boolean isItemInOff
    ) {
        if(interactState != null){

            if(isItemInMain){
                if(interactState != 0) itemStack.set(INTERACTION_HAND, 0);
            } else if (isItemInOff && canOffHand(player, OFF_HAND, false)) {
                if(interactState != 1) itemStack.set(INTERACTION_HAND, 1);
            } else {
                if(interactState != 2) itemStack.set(INTERACTION_HAND, 2);
            }

        } else itemStack.set(INTERACTION_HAND, 2);
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
        if(level instanceof ServerLevel serverLevel){}

        if (canOffHand(player, interactionHand, true)) {
            var item = player.getItemInHand(interactionHand);
            player.startUsingItem(interactionHand);
            CastHelper.use(player);
            return InteractionResultHolder.pass(item);
        }

        return InteractionResultHolder.fail(player.getOffhandItem());
    }

    public static boolean canOffHand(LivingEntity entity, InteractionHand interactionHand, boolean shouldSendMessage){
        var curio = CuriosApi.getCuriosInventory(entity);
        if(interactionHand == OFF_HAND){
            if(curio.isEmpty()) return false;
            var isGauntletEquipped = curio.get().isEquipped(ItemsRegister.BATTLEMAGE_GAUNTLET.get());
            if(isGauntletEquipped){
                return true;
            } else {
                if(shouldSendMessage){
                    var item = entity.getItemInHand(interactionHand).getItem();
                    ElementRegistry.getElementFromWand(item).ifPresent(
                        abstractElement -> {
                            var sendMessage = ModHelpers.withStyleComponent("You don't have the power to offhand this yet.", abstractElement.textColourPrimary());
                            if (entity instanceof Player player) player.displayClientMessage(sendMessage, true);
                        }
                    );
                }
                return false;
            }
        }

        return true;
    }

    public JahdooRarity getRarity(){
        var wandData = this.components().get(WAND_DATA.get());
        var getWandData = wandData == null ? WandData.DEFAULT : wandData;
        var getRarityId = getWandData.rarityId();
        return JahdooRarity.getAllRarities().get(getRarityId);
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
