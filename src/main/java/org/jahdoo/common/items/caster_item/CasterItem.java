package org.jahdoo.common.items.caster_item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.items.JahdooItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.world.InteractionResultHolder.fail;
import static net.minecraft.world.InteractionResultHolder.pass;
import static org.jahdoo.ascension.utils.ModTags.Block.ALLOWED_BLOCK_INTERACTIONS;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.*;
import static org.jahdoo.common.registers.ComponentReg.INTERACTION_HAND;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;

public class CasterItem extends Item implements JahdooItem {

    public CasterItem() {
        super(wandProperties());
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
        return CasterItemHelper.getItemName(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> toolTip, TooltipFlag flag) {
        appendItemToolTips(stack, context, toolTip, false);
        toolTip.addAll(CasterItemHelper.getItemModifiers(stack, context.level()));
        bonusModifierTooltip(stack, toolTip, context);
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
                var entity = level.getBlockEntity(result.getBlockPos());
                var state = level.getBlockState(result.getBlockPos());
                var below = level.getBlockState(result.getBlockPos().below(1));

                if(entity instanceof LockBlockEntity block && !block.canPlace() ||  state.is(ALLOWED_BLOCK_INTERACTIONS) || below.is(ALLOWED_BLOCK_INTERACTIONS)){
                    return fail(item);
                }
            }
            var castAbility = castAbility(player, interactionHand, item);
            if (castAbility != null) return castAbility;
        }

        return fail(item);
    }

    public static InteractionResultHolder<ItemStack> castAbility(Player player, InteractionHand interactionHand, ItemStack item) {
        if (canOffHand(player, true)) {
            player.startUsingItem(interactionHand);
            CastHelper.use(player);
            return pass(item);
        }

        return null;
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

}
