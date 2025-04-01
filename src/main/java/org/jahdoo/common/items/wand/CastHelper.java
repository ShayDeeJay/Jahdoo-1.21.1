package org.jahdoo.common.items.wand;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.ability.AbilityRegistrar.DISTANCE_CAST;
import static org.jahdoo.ascension.ability.AbilityRegistrar.HOLD_CAST;
import static org.jahdoo.common.components.DataComponentHelper.getSpecificValue;
import static org.jahdoo.common.items.wand.WandAnimations.*;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.common.registers.AttributeReg.COOLDOWN_REDUCTION;
import static org.jahdoo.common.registers.AttributeReg.MANA_COST_REDUCTION;
import static org.jahdoo.common.registers.ComponentReg.WAND_ABILITY_HOLDER;
import static org.jahdoo.common.registers.ElementReg.fromWand;
import static org.jahdoo.ascension.utils.Maths.getFormattedFloat;
import static org.jahdoo.ascension.utils.Helpers.*;


public class CastHelper {

    public static void onCast(Player player, AbilityRegistrar ability){
        if(!ability.selfChargeAbility()){
            castAnimation(player, SINGLE_CAST_ID);
        }
        ability.invokeAbility(player);
        if(ability.getCastType() != HOLD_CAST) player.stopUsingItem();
    }

    public static void castAnimation(LivingEntity livingEntity, String anim) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            var hand = livingEntity.getItemInHand(livingEntity.getUsedItemHand());
            if(hand.getItem() instanceof WandItem wandItem){
                triggerAnimWithController(wandItem, hand, serverLevel, livingEntity, anim);
            }
        }
    }

    public static void chargeManaAndCooldown(String abilityId, Player player){
        var wandItem = Helpers.getUsedItem(player);
        var cooldownCost = getSpecificValue(player, wandItem, COOLDOWN);
        var getManaCost = getSpecificValue(player, wandItem, MANA_COST);
        chargeMana(abilityId, getManaCost, player);
        chargeCooldown(abilityId, cooldownCost, player);
    }

    public static void failedCastNotification(Player player) {
        var crafterFail = SoundEvents.CRAFTER_FAIL;
        var volume = 1.6f;
        var pitch = 1.2f;
        var vexHurt = SoundEvents.VEX_HURT;
        var volume1 = 0.8f;
        var pitch1 = 1.3f;
        if(player instanceof ServerPlayer serverPlayer){
            Helpers.sendClientSound(serverPlayer, crafterFail, volume, pitch, false);
            Helpers.sendClientSound(serverPlayer, vexHurt, volume1, pitch1, false);
        } else {
            player.playSound(crafterFail, volume, pitch);
            player.playSound(vexHurt, volume1, pitch1);
        }
        castAnimation(player, CANT_CAST_ID);
    }

    public static void chargeCooldown(String abilityId, double cooldown, Player player) {
        var attributeValue = getAttributeValue(player, AttributeReg.SKIP_COOLDOWN);
        if(player.isCreative()) return;
        if(attributeValue > 0 && Random.nextFloat(100) < attributeValue) return;

        var cooldownSystem = player.getData(CASTER_DATA);
        var ability = AbilityReg.getFirstSpellByTypeId(abilityId);
        var wand = Helpers.getUsedItem(player);
        var getElement = SharedUI.getElementWithType(ability.orElseThrow(), wand);
        var reCalculatedCooldown = Helpers.attributeModifierCalculator(player, (float) cooldown, false,  getElement.cooldownReduction(), COOLDOWN_REDUCTION);
        cooldownSystem.addCooldown(abilityId, (int) reCalculatedCooldown);
    }

    public static void chargeMana(String abilityId, double manaCost, Player player) {
        var attributeValue = getAttributeValue(player, AttributeReg.SKIP_MANA);
        if(player.isCreative()) return;
        if(attributeValue > 0 && Random.nextFloat(100) < attributeValue) return;

        var manaSystem = player.getData(CASTER_DATA);
        var ability = AbilityReg.getFirstSpellByTypeId(abilityId);
        var wand = Helpers.getUsedItem(player);
        var getElement = SharedUI.getElementWithType(ability.orElseThrow(), wand);
        var reCalculatedMana = Helpers.attributeModifierCalculator(player, (float) manaCost, false, getElement.manaReduction(), MANA_COST_REDUCTION);
        manaSystem.subtractMana(reCalculatedMana, player);
    }

    private static void debugMessages(
        Player player,
        ItemStack wandItem,
        AbstractElement getElement,
        double cooldownCost,
        float adjustedCooldown,
        double getManaCost,
        float adjustedMana
    ) {
        playDebugMessage(player, wandItem);
        playDebugMessage(player, getElement.name());
        playDebugMessage(player, "cooldown = " + cooldownCost);
        playDebugMessage(player, "adjusted cooldown = " + adjustedCooldown);
        playDebugMessage(player, "mana = " + getManaCost);
        playDebugMessage(player, "adjusted mana = " + adjustedMana);
    }

    public static void executeAndCharge(Player player) {
        var wandItem = Helpers.getUsedItem(player);
        var abilityName = DataComponentHelper.getAbilityTypeItemStack(wandItem);
        var ability = AbilityReg.REGISTRY.get(res(abilityName));
        if (ability == null) return;
        var getElement = SharedUI.getElementWithType(ability, wandItem);

        if(!player.isCreative()){
            if(validManaAndCooldown(player)){
                if(!ability.selfChargeAbility()){
                    var cooldownCost = getSpecificValue(player, wandItem, COOLDOWN);
                    var getManaCost = getSpecificValue(player, wandItem, MANA_COST);
                    var adjustedMana = Helpers.attributeModifierCalculator(player, (float) getManaCost, false, getElement.manaReduction(), MANA_COST_REDUCTION);
                    var adjustedCooldown = Helpers.attributeModifierCalculator(player, (float) cooldownCost, false, getElement.cooldownReduction(), COOLDOWN_REDUCTION);
                    chargeCooldown(abilityName, adjustedCooldown, player);
                    chargeMana(abilityName, adjustedMana, player);
                }
                onCast(player, ability);
                OnCastPerks.onCastPerkApply(player);
//                Helpers.hurtAndKeepItem(wandItem, 20, player.level(), player);
            } else failedCastNotification(player);
        } else onCast(player, ability);
    }

    public static InteractionResultHolder<ItemStack> use(Player player) {
        var itemStack = Helpers.getUsedItem(player);
        var abilityName = DataComponentHelper.getAbilityTypeWand(player);
        var getAbility = AbilityReg.REGISTRY.get(abilityName);
        var canUse = getCanApplyDistanceAbility(player, itemStack);
        var cantUseInDim = player.level() instanceof CustomLevel && getAbility != null && !getAbility.isMultiType() && getAbility.getElemenType().equals(ElementReg.utility());
        var cantUse = (player.onGround() && player.isShiftKeyDown()) || getAbility == null ;
        var fail = InteractionResultHolder.fail(itemStack);

        if(cantUseInDim) {
            player.displayClientMessage(Component.literal("You cant use that here"), true);
            failedCastNotification(player);
            return fail;
        }

        if(cantUse) return fail;

        if(canUse) executeAndCharge(player); else failedCastNotification(player);

        return InteractionResultHolder.pass(itemStack);
    }

    public static boolean getCanApplyDistanceAbility(Player player, ItemStack itemStack){
        var isDistanceCast = AbilityReg.REGISTRY.get(DataComponentHelper.getAbilityTypeWand(player));
        if(isDistanceCast != null && isDistanceCast.getCastType() == DISTANCE_CAST){
            var getCurrentAbility = DataComponentHelper.getAbilityTypeItemStack(itemStack);
            var getAbility = Helpers.getModifierValue(itemStack.get(WAND_ABILITY_HOLDER.get()), getCurrentAbility);
            var allowedDistance = getAbility.get(CASTING_DISTANCE).actualValue();
            var lookAtLocation = player.pick(allowedDistance, 0, false);
            var isValidCastLocation = lookAtLocation.getType() == HitResult.Type.MISS;
            var getWandElement = fromWand(itemStack.getItem()).orElseThrow();
            var distance = String.valueOf(Math.round(allowedDistance));
            var colour = getWandElement.partColourB();
            var distanceCompo = Helpers.withStyleComponent(distance, colour);
            var notAllowedDistanceMessage = Component.translatable("casting.jahdoo.distance", distanceCompo);

            if (isValidCastLocation) {
                player.displayClientMessage(notAllowedDistanceMessage, true);
                player.stopUsingItem();
                return false;
            }
        }
        return true;
    }

    public static boolean validManaAndCooldown(Player player){
        var casterData = player.getData(CASTER_DATA);
        var wandItem = Helpers.getUsedItem(player);
        var abilityName = DataComponentHelper.getAbilityTypeItemStack(wandItem);
        var ability = AbilityReg.REGISTRY.get(res(abilityName));
        if(ability == null) return false;
        var getElement = fromWand(wandItem.getItem()).orElseThrow();
        var getManaCost = getSpecificValue(player, wandItem, MANA_COST);
        var typeReduction = getElement.manaReduction();
        var adjustedMana = Helpers.attributeModifierCalculator(player, (float) getManaCost, false, MANA_COST_REDUCTION, typeReduction);
        var manaAvailable = casterData.getManaPool();
        var sufficientMana = casterData.getManaPool() >= adjustedMana;
        var abilityOnCooldown = casterData.isAbilityOnCooldown(abilityName);

        if(!player.isCreative()){
            if (abilityOnCooldown) {
                var nameComp = Helpers.withStyleComponent(ability.getAbilityName(), getElement.partColourB());
                var messageComp = Component.translatable("casting.jahdoo.on_cooldown", nameComp);
                player.displayClientMessage(messageComp, true);
                return false;
            }

            if (!sufficientMana) {
                var formattedCost = getFormattedFloat(adjustedMana);
                var formattedAvailable = getFormattedFloat((float) manaAvailable);
                var costComp = Helpers.withStyleComponent(String.valueOf(formattedCost), getElement.partColourA());
                var availComp = Helpers.withStyleComponent(String.valueOf(formattedAvailable), getElement.partColourB());
                var notEnoughManaMessage = Component.translatable("casting.jahdoo.insufficient_man", availComp, costComp);
                player.displayClientMessage(notEnoughManaMessage, true);
                return false;
            }
        }
        return true;
    }

}
