package org.jahdoo.common.items.wand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.client.overlay.BoonSelectionScreen;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockReg;
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

import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.Blocks.*;
import static org.jahdoo.ascension.BlockSetupManager.placePerkTables;
import static org.jahdoo.ascension.trading_post.ShoppingItems.getEliteShoppingItem;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.common.block.shopping_table.ShoppingTableBlock.TEXTURE;
import static org.jahdoo.common.items.wand.WandAnimations.*;
import static org.jahdoo.common.items.wand.WandItemHelper.*;
import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.common.registers.ComponentReg.INTERACTION_HAND;
import static org.jahdoo.common.registers.ComponentReg.WAND_DATA;

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
            .fireResistant();
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

        System.out.println(PlayerWallet.getWalletCoins(player));

        PlayerWallet.updateWallet(player, new PlayerWallet.CurrencyConverter(1, 1, 0, 5));

        if (canOffHand(player, interactionHand, true)) {
            player.startUsingItem(interactionHand);
            CastHelper.use(player);
            return InteractionResultHolder.pass(item);
        }

        return InteractionResultHolder.fail(item);
    }

}
