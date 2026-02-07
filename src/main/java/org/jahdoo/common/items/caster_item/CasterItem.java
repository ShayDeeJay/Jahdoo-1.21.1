package org.jahdoo.common.items.caster_item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.items.BaseJahdooItem;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

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
        var item = player.getItemInHand(interactionHand);
        var pic = player.pick(player.blockInteractionRange(), 1, false);

        if(level instanceof ServerLevel){

            if(pic instanceof BlockHitResult result){
                var bPos = result.getBlockPos();
                var entity = level.getBlockEntity(bPos);
                var state = level.getBlockState(bPos);
                var below = level.getBlockState(bPos.below(1));

                if(entity instanceof LockBlockEntity block && !block.canPlace() || state.is(ALLOWED_BLOCK_INTERACTIONS) || below.is(ALLOWED_BLOCK_INTERACTIONS)){
                    return fail(item);
                }
            }
            var castAbility = castAbility(player, interactionHand, item);
            if (castAbility != null) return castAbility;
        }

        return fail(item);
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
