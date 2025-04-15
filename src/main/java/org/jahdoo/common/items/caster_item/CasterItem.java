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
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.registers.ComponentReg;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.world.InteractionResultHolder.fail;
import static net.minecraft.world.InteractionResultHolder.pass;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.*;
import static org.jahdoo.common.registers.ComponentReg.INTERACTION_HAND;
import static org.jahdoo.common.registers.ComponentReg.RUNE_HOLDER;

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
        toolTip.addAll(CasterItemHelper.getItemModifiers(stack, context.level()));
        bonusModifierTooltip(stack, toolTip, context);
    }

    public static Properties wandProperties(){
        return new Properties()
            .stacksTo(1)
            .durability(300)
            .component(RUNE_HOLDER, RuneHolder.makeRuneSlots(0, 40))
            .component(ComponentReg.JAHDOO_RARITY, 0);
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

        if(!level.isClientSide){
            var castAbility = castAbility(player, interactionHand, item);
            if (castAbility != null) return castAbility;
        }

        return fail(item);
    }

    public static InteractionResultHolder<ItemStack> castAbility(Player player, InteractionHand interactionHand, ItemStack item) {
        if (canOffHand(player, interactionHand, true)) {
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
