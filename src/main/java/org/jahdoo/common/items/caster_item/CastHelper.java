package org.jahdoo.common.items.caster_item;

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
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.items.caster_item.elemental_wand.ElementalWand;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.ascension.ability.Ability.DISTANCE_CAST;
import static org.jahdoo.ascension.ability.Ability.HOLD_CAST;
import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.Maths.getFormattedFloat;
import static org.jahdoo.common.items.caster_item.ItemAnimations.*;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.common.registers.AttributeReg.COOLDOWN_REDUCTION;
import static org.jahdoo.common.registers.AttributeReg.MANA_COST_REDUCTION;
import static org.jahdoo.common.registers.mod.ElementReg.fromWand;


public class CastHelper {

    public static void onCast(Player player, Ability ability){
        if(!ability.selfChargeAbility()) castAnimation(player, SINGLE_CAST_ID);
        ability.invokeAbility(player);
        if(ability.getCastType() != HOLD_CAST) player.stopUsingItem();
    }

    public static void castAnimation(LivingEntity livingEntity, String anim) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            var hand = livingEntity.getItemInHand(livingEntity.getUsedItemHand());
            if(hand.getItem() instanceof ElementalWand wandItem){
                triggerAnimWithController(wandItem, hand, serverLevel, livingEntity, anim);
            }
        }
    }

    public static void chargeManaAndCooldown(String abilityId, Player player){
        var cooldownCost = CasterData.getSpecificValue(player, COOLDOWN);
        var getManaCost = CasterData.getSpecificValue(player, MANA_COST);
        chargeMana(abilityId, getManaCost, player);
        chargeCooldown(abilityId, cooldownCost, player);
    }

    public static void failedCastNotification(Player player) {
        var crafterFail = SoundEvents.CRAFTER_FAIL;
        var volume = 1.6f;
        var pitch = 1.2f;
        var vexHurt = SoundEvents.VEX_HURT;
        var volume1 = 0.8f;
        var pitch1 = 1.6f;
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
        var typeId = CasterData.selectedAbility(player);
        var ability = AbilityReg.getFirstSpellByTypeId(typeId);
        if(ability.isPresent()){
            var getAbility = ability.get();
            var getElement = getAbility.getElemenType();

            if (!player.isCreative()) {
                if(durabilityDamageCount(wandItem) > 0){
                    if (validManaAndCooldown(player)) {
                        if (!getAbility.selfChargeAbility()) {
                            var cooldownCost = CasterData.getSpecificValue(player, COOLDOWN);
                            var getManaCost = CasterData.getSpecificValue(player, MANA_COST);
                            var adjustedMana = Helpers.attributeModifierCalculator(player, (float) getManaCost, false, getElement.manaReduction(), MANA_COST_REDUCTION);
                            var adjustedCooldown = Helpers.attributeModifierCalculator(player, (float) cooldownCost, false, getElement.cooldownReduction(), COOLDOWN_REDUCTION);
                            chargeCooldown(typeId, adjustedCooldown, player);
                            chargeMana(typeId, adjustedMana, player);
                        }
                        onCast(player, getAbility);
                        OnCastPerks.onCastPerkApply(player);
                        Helpers.hurtAndKeepItem(wandItem, 5, player.level(), player);
                        if(CasterItemHelper.canOffHand(player, false)) {
                            var gauntlet = CasterItemHelper.getGauntlet(player);
                            System.out.println(gauntlet);
                            Helpers.hurtAndKeepItem(gauntlet, 5, player.level(), player);
                        }
                    } else failedCastNotification(player);
                } else brokenWandNotification(player, getElement);
            } else onCast(player, getAbility);
        }
    }

    public static void brokenWandNotification(Player player, @Nullable AbstractElement element){
        var colour = element == null ? ColourStore.OFF_WHITE : element.textColourA();
        failedCastNotification(player);
        player.displayClientMessage(withStyleComponent("Wand too damaged to cast", colour), true);
    }

    public static InteractionResultHolder<ItemStack> use(Player player) {
        var itemStack = Helpers.getUsedItem(player);
        var typeId = CasterData.selectedAbility(player);
        var getAbility = AbilityReg.getFirstSpellByTypeId(typeId);
        var canUse = getCanApplyDistanceAbility(player, itemStack);
        var cantUseInDim = player.level() instanceof CustomLevel && getAbility.isPresent() && !getAbility.get().isMultiType() && getAbility.get().getElemenType().equals(ElementReg.utility());
        var fail = InteractionResultHolder.fail(itemStack);

        if(cantUseInDim) {
            player.displayClientMessage(Component.literal("You cant use that here"), true);
            failedCastNotification(player);
            return fail;
        }

        if(canUse) executeAndCharge(player); else failedCastNotification(player);

        return InteractionResultHolder.pass(itemStack);
    }

    public static boolean getCanApplyDistanceAbility(Player player, ItemStack itemStack){
        var isDistanceCast = AbilityReg.getFirstSpellByTypeId(CasterData.selectedAbility(player));
        if(isDistanceCast.isPresent() && isDistanceCast.get().getCastType() == DISTANCE_CAST){
            var getCurrentAbility = CasterData.selectedAbility(player);
            var getAbility = Helpers.getModifierValue(player, getCurrentAbility);
            var allowedDistance = getAbility.get(CASTING_DISTANCE).setValue();
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
        var typeId = CasterData.selectedAbility(player);
        var ability = AbilityReg.getFirstSpellByTypeId(typeId).get();
        var getElement = fromWand(wandItem.getItem()).orElseThrow();
        var getManaCost = CasterData.getSpecificValue(player, MANA_COST);
        var typeReduction = getElement.manaReduction();
        var adjustedMana = Helpers.attributeModifierCalculator(player, (float) getManaCost, false, MANA_COST_REDUCTION, typeReduction);
        var manaAvailable = casterData.getManaPool();
        var sufficientMana = casterData.getManaPool() >= adjustedMana;
        var abilityOnCooldown = casterData.isAbilityOnCooldown(typeId);

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
