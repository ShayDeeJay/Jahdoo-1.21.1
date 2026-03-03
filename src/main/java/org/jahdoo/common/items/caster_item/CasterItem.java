package org.jahdoo.common.items.caster_item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.items.BaseJahdooItem;
import org.jahdoo.common.registers.AttributeReg;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.minecraft.world.InteractionResultHolder.fail;
import static net.minecraft.world.InteractionResultHolder.pass;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.canOffHand;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.canOffhandWand;
import static org.jahdoo.common.registers.ComponentReg.INTERACTION_HAND;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;
import static org.jahdoo.trial_nexus.utils.ModTags.Block.ALLOWED_BLOCK_INTERACTIONS;

public class CasterItem extends BaseJahdooItem {

    public CasterItem(Properties properties) {
        super(properties);
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
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return CasterItemHelper.getItemName(stack, () -> super.getName(stack));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> toolTip, TooltipFlag flag) {
        appendItemToolTips(stack, context, toolTip, false);
        if(isItemBroken(stack)){
            brokenGearMessage(toolTip, stack);
            return;
        }
        toolTip.addAll(CasterItemHelper.getItemModifiers(stack, context.level()));
        bonusModifierTooltip(stack, toolTip, context, true);
        runeSpacer(stack, toolTip);
    }

    public static Properties wandProperties(){
        return new Properties()
            .stacksTo(1)
            .durability(300)
            .component(JAHDOO_RARITY, 0);
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
        var itemStack = player.getItemInHand(interactionHand);
        var pic = player.pick(player.blockInteractionRange(), 1, false);

//        player.getAttribute(AttributeReg.MAGE_FLIGHT).setBaseValue(0.02);
        AttributeReg.replaceOrAddAttribute(itemStack, "test_name", AttributeReg.MAGE_FLIGHT, 0.06, EquipmentSlot.MAINHAND, false, "blink");

        if(level instanceof ServerLevel){
            if(pic instanceof BlockHitResult result){
                var bPos = result.getBlockPos();
                var entity = level.getBlockEntity(bPos);
                var state = level.getBlockState(bPos);
                var below = level.getBlockState(bPos.below(1));

                if(entity instanceof LockBlockEntity block && !block.canPlace() || state.is(ALLOWED_BLOCK_INTERACTIONS) || below.is(ALLOWED_BLOCK_INTERACTIONS)){
                    return fail(itemStack);
                }
            }
            var castAbility = castAbility(player, interactionHand, itemStack);
            if (castAbility != null) return castAbility;
        }

        return fail(itemStack);
    }

    public static void openTeleportMenu(Level level, Player player, ItemStack itemStack) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            final InteractionHand hand = serverPlayer.getUsedItemHand();

            final Collection<Waystone> waystones = PlayerWaystoneManager.getTargetsForItem(serverPlayer, itemStack);
            PlayerWaystoneManager.ensureSortingIndex(serverPlayer, waystones);
            Balm.getNetworking().openGui(serverPlayer, new BalmMenuProvider<ModMenus.ItemInitiatedWaystoneMenuData>() {
                public Component getDisplayName() {
                    return Component.translatable("container.waystones.waystone_selection");
                }

                public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                    return (new WaystoneSelectionMenu(ModMenus.warpStoneSelection.get(), null, windowId, waystones, Collections.emptySet())).withWarpItem(itemStack).setPostTeleportHandler((context) -> itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand)));
                }

                public ModMenus.ItemInitiatedWaystoneMenuData getScreenOpeningData(ServerPlayer serverPlayer) {
                    return new ModMenus.ItemInitiatedWaystoneMenuData(waystones, itemStack);
                }

                public StreamCodec<RegistryFriendlyByteBuf, ModMenus.ItemInitiatedWaystoneMenuData> getScreenStreamCodec() {
                    return ModMenus.ItemInitiatedWaystoneMenuData.STREAM_CODEC;
                }
            });
        }
    }

    public static void addSlot(LivingEntity player, String type, ResourceLocation location, int slots) {
        CuriosApi.getCuriosInventory(player).ifPresent(
            inventory -> {
                inventory.addTransientSlotModifier(type, location, slots, AttributeModifier.Operation.ADD_VALUE);
            }
        );
    }

    public static void removeSlot(LivingEntity player, String type, ResourceLocation res){
        CuriosApi.getCuriosInventory(player).ifPresent(
            inventory -> {
                inventory.removeSlotModifier(type, res);
            }
        );
    }

    public static InteractionResultHolder<ItemStack> castAbility(Player player, InteractionHand interactionHand, ItemStack item) {
        if (canOffHand(player, true)) {
            player.startUsingItem(interactionHand);
            CastHelper.use(player);
            return pass(item);
        }

        return null;
    }

}
