package org.jahdoo.common.items.caster_item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.items.weapon.BaseWeapon;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.InteractionResultHolder.fail;
import static net.minecraft.world.InteractionResultHolder.pass;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.canOffHand;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.canOffhandWand;
import static org.jahdoo.common.registers.ComponentReg.INTERACTION_HAND;
import static org.jahdoo.trial_nexus.utils.ModTags.Block.ALLOWED_BLOCK_INTERACTIONS;

public class BaseMagicWeapon extends BaseWeapon {

    public BaseMagicWeapon(Properties properties) {
        super(Tiers.NETHERITE, properties);
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

                if(entity instanceof LockBlockEntity block && !block.canPlace() || state.is(ALLOWED_BLOCK_INTERACTIONS) || below.is(ALLOWED_BLOCK_INTERACTIONS)){
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

}
