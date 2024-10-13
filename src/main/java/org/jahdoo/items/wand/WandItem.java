package org.jahdoo.items.wand;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.item_renderer.WandItemRenderer;
import org.jahdoo.components.WandData;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.jahdoo.block.wand.WandBlockEntity.GET_WAND_SLOT;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class WandItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CAST_TWO = RawAnimation.begin().thenPlay("cast_two");
    public String location;

    public static Properties wandProperties(){
        Properties properties = new Properties();
        properties.stacksTo(1);
        properties.component(DataComponentRegistry.WAND_ABILITY_HOLDER.get(),WandAbilityHolder.DEFAULT);
        properties.component(DataComponentRegistry.WAND_DATA.get(), WandData.DEFAULT);
        properties.fireResistant();
        return properties;
    }

    public static ItemAttributeModifiers.Entry setAttribute(String name, Holder<Attribute> attribute, int value, EquipmentSlot equipmentSlot){
        ResourceLocation resourcelocation = GeneralHelpers.modResourceLocation(name);
        var attributes = new AttributeModifier(resourcelocation, value,  AttributeModifier.Operation.ADD_VALUE);
        return new ItemAttributeModifiers.Entry(attribute, attributes,  EquipmentSlotGroup.bySlot(equipmentSlot));
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
        Player player = pContext.getPlayer();

        if (player == null || !player.isShiftKeyDown() || !player.onGround()) return InteractionResult.PASS;

        BlockPos clickedPos = pContext.getClickedPos();
        if(pContext.getLevel().getBlockState(clickedPos).isEmpty()){
            Level level = pContext.getLevel();
            ItemStack itemStack = player.getMainHandItem();
            level.setBlockAndUpdate(clickedPos, BlocksRegister.WAND.get().defaultBlockState());
            BlockEntity blockEntity = level.getBlockEntity(clickedPos);
            if (blockEntity instanceof WandBlockEntity wandBlockEntity) {
                playPlaceSound(level, pContext.getClickedPos());
                ItemStack copiedWand = itemStack.copyWithCount(1);
                itemStack.shrink(1);
                wandBlockEntity.inputItemHandler.setStackInSlot(GET_WAND_SLOT, copiedWand);
                wandBlockEntity.updateView();
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return SoundEvents.EMPTY;
    }

    public void playPlaceSound(Level level, BlockPos bPos){
        SoundEvent soundEvents = SoundEvents.BEACON_ACTIVATE;
        GeneralHelpers.getSoundWithPosition(level, bPos, soundEvents, 0.4f, 1.5f);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSoltSelected) {
        if(!(entity instanceof Player player)) return;
        if (player.getItemInHand(player.getUsedItemHand()) == itemStack) {}
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
        player.startUsingItem(interactionHand);
        if (level instanceof ServerLevel serverLevel) {
            triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(interactionHand), serverLevel), "Activation", "cast");
        }

        if (interactionHand == InteractionHand.MAIN_HAND) {
            if (player instanceof ServerPlayer serverPlayer) return CastHelper.use(serverPlayer);
        }

        return super.use(level, player, interactionHand);
    }

    public void pullParticlesToCenter(Player player){
        var casterData = player.getData(CASTER_DATA);
        var manaReduction = casterData.getMaxMana(player) / 60;
        BakedParticleOptions bakedParticleOptions = new BakedParticleOptions(
            ElementRegistry.VITALITY.get().getTypeId(),
            6, 2f, false
        );
        GenericParticleOptions genericParticleOptions = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, ElementRegistry.VITALITY.get(), 6, 2f);

        List<ParticleOptions> particleOptionsList = List.of(
            bakedParticleOptions,
            genericParticleOptions
        );

        if(casterData.getManaPool() >= manaReduction){
//            casterData.subtractMana(manaReduction);
            player.heal(0.05f);

            GeneralHelpers.getInnerRingOfRadiusRandom(
                player.position()
                    .add(0, player.getBbHeight() / 2, 0)
                    .offsetRandom(RandomSource.create(), 1.5f), 3, (double) player.getTicksUsingItem()/10,
                positions -> {
                    if (player.level() instanceof ServerLevel serverLevel) {
                        Vec3 directions = player.position().subtract(positions).normalize().add(0, player.getBbHeight() / 2, 0);
                        GeneralHelpers.generalHelpers.sendParticles(
                            serverLevel,
                            particleOptionsList.get(RandomSource.create().nextInt(0, 2)),
                            positions,
                            0,
                            directions.x,
                            GeneralHelpers.Random.nextDouble(-0.3, 0.3),
                            directions.z,
                            (double) player.getTicksUsingItem()/500
                        );
                    }
                }
            );
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
//        pullParticlesToCenter((Player) livingEntity);
//        if(livingEntity instanceof Player player){
//            GeneralHelpers.playDebugMessage(player, player.getTicksUsingItem());
//            if(player instanceof ServerPlayer serverPlayer){
//                var pos = serverPlayer.getRespawnPosition();
//                var dimension = serverPlayer.getRespawnDimension();
//                if(player.getTicksUsingItem() % 3 == 0){
//                    GeneralHelpers.getSoundWithPosition(level, player.blockPosition(), SoundEvents.SOUL_ESCAPE.value(), Math.min(2, player.getTicksUsingItem()/5), (float) player.getTicksUsingItem()/100);
//                }
//                if(player.getTicksUsingItem() >= 200){
//                    if(pos != null){
//                        serverPlayer.stopUsingItem();
//                        serverPlayer.teleportTo(serverPlayer.getServer().getLevel(dimension), pos.getX(), pos.getY(), pos.getZ(), serverPlayer.yHeadRot, serverPlayer.yBodyRot);
//                    }
//                }
//            }
//        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private WandItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new WandItemRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return super.getDefaultAttributeModifiers(stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Activation", 0, state -> PlayState.STOP)
            .triggerableAnim("cast", CAST_TWO));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtil.getCurrentTick();
    }
}
