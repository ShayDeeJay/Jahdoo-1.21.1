package org.jahdoo.items.wand;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.item_renderer.WandItemRenderer;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.components.WandData;
import org.jahdoo.networking.packet.server2client.EnchantedBlockS2C;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;
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

import static org.jahdoo.block.wand.WandBlockEntity.GET_WAND_SLOT;
import static org.jahdoo.items.wand.WandAnimations.*;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;
import static org.jahdoo.registers.ElementRegistry.UTILITY;

public class WandItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String location;

    public static Properties wandProperties(){
        Properties properties = new Properties();
        properties.stacksTo(1);
        properties.component(DataComponentRegistry.WAND_ABILITY_HOLDER.get(),WandAbilityHolder.DEFAULT);
        properties.component(WAND_DATA.get(), WandData.DEFAULT);
        properties.fireResistant();
        return properties;
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

        if(pContext.getLevel().getBlockState(clickedPos).isEmpty()){
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
    protected @NotNull SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return SoundEvents.EMPTY;
    }

    public void playPlaceSound(Level level, BlockPos bPos){
        SoundEvent soundEvents = SoundEvents.BEACON_ACTIVATE;
        ModHelpers.getSoundWithPosition(level, bPos, soundEvents, 0.4f, 1.5f);
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
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand interactionHand) {
//        Minecraft.getInstance().setScreen(new TestingElements());

        var item = player.getMainHandItem();
        player.startUsingItem(player.getUsedItemHand());
        if (interactionHand == InteractionHand.MAIN_HAND) {
            CastHelper.use(player);
            return InteractionResultHolder.pass(item);
        }

        return InteractionResultHolder.fail(player.getOffhandItem());
    }


    public static void storeBlockType(ItemStack itemStack, BlockState state, Player player, BlockPos pos){
        var compound = new CompoundTag();
        compound.put("block", NbtUtils.writeBlockState(state));
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        player.displayClientMessage(Component.literal("Assigned: ").append(state.getBlock().getName()), true);
    }

    public static Block getStoredBlock(Level level, ItemStack itemStack){
        var holder = level.holderLookup(Registries.BLOCK);
        var component = itemStack.get(DataComponents.CUSTOM_DATA);
        if(component == null) return Blocks.AIR;
        var getFromComp = component.copyTag().getCompound("block");
        var state = NbtUtils.readBlockState(holder, getFromComp);
        return state.getBlock();
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
