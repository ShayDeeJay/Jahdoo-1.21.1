package org.jahdoo.common.items.caster_item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.jahdoo.common.items.caster_item.elemental_wand.ElementalWand;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static org.jahdoo.common.items.caster_item.ItemAnimations.*;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.common.registers.AttributeReg.COOLDOWN_REDUCTION;
import static org.jahdoo.common.registers.AttributeReg.MANA_COST_REDUCTION;
import static org.jahdoo.common.registers.mod.ElementReg.fromWand;
import static org.jahdoo.trial_nexus.ability.Ability.DISTANCE_CAST;
import static org.jahdoo.trial_nexus.ability.Ability.HOLD_CAST;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.*;


public class CastHelper {

    public static boolean validCasterType(Item item){
        return item instanceof CasterItem || item instanceof BaseMagicWeapon;
    }

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
        chargeMana(abilityId, player);
        chargeCooldown(abilityId, player);
    }

    public static void failedCastNotification(Player player) {
        var crafterFail = SoundEvents.CRAFTER_FAIL;
        var volume = 1.6f;
        var pitch = 1.2f;
        var vexHurt = SoundEvents.VEX_HURT;
        var volume1 = 0.8f;
        var pitch1 = 1.6f;
        if(player instanceof ServerPlayer serverPlayer){
            JahdooHelpers.sendClientSound(serverPlayer, crafterFail, volume, pitch, false);
            JahdooHelpers.sendClientSound(serverPlayer, vexHurt, volume1, pitch1, false);
        } else {
            player.playSound(crafterFail, volume, pitch);
            player.playSound(vexHurt, volume1, pitch1);
        }
        castAnimation(player, CANT_CAST_ID);
    }

    public static void chargeCooldown(String abilityId, Player player) {
        var attributeValue = getAttributeValue(player, AttributeReg.SKIP_COOLDOWN);
        if(player.isCreative()) return;
        if(attributeValue > 0 && Random.nextFloat(100) < attributeValue) return;

        var cooldownCost = CasterData.getSpecificValue(player, COOLDOWN);
        var cooldownSystem = player.getData(CASTER_DATA);
        var ability = AbilityReg.getFirstSpellByTypeId(abilityId);
        var reCalculatedCooldown = JahdooHelpers.attributeModifierCalculator(player, (float) cooldownCost, false,  ability.orElseThrow().getElemenType().cooldownReduction(), COOLDOWN_REDUCTION);
        cooldownSystem.addCooldown(abilityId, (int) reCalculatedCooldown);
    }

    public static void chargeMana(String abilityId, Player player) {
        var attributeValue = getAttributeValue(player, AttributeReg.SKIP_MANA);
        if(player.isCreative()) return;
        if(attributeValue > 0 && Random.nextFloat(100) < attributeValue) return;

        var getManaCost = CasterData.getSpecificValue(player, MANA_COST);
        var manaSystem = player.getData(CASTER_DATA);
        var ability = AbilityReg.getFirstSpellByTypeId(abilityId);
        var reCalculatedMana = JahdooHelpers.attributeModifierCalculator(player, (float) getManaCost, false, ability.orElseThrow().getElemenType().manaReduction(), MANA_COST_REDUCTION);
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
        var wandItem = JahdooHelpers.getUsedItem(player);
        var typeId = CasterData.selectedAbility(player);
        var ability = AbilityReg.getFirstSpellByTypeId(typeId);
        if(ability.isPresent()){
            var getAbility = ability.get();
            var getElement = getAbility.getElemenType();

            if (!player.isCreative()) {
                if(durabilityDamageCount(wandItem) > 0){
                    if (validManaAndCooldown(player)) {
                        if (!getAbility.selfChargeAbility()) {
                            chargeCooldown(typeId, player);
                            chargeMana(typeId, player);
                        }
                        onCast(player, getAbility);
                        OnCastPerks.onCastPerkApply(player);
                        if(player.level() instanceof ServerLevel serverLevel){
                            JahdooHelpers.hurtAndKeepItem(wandItem, 5, serverLevel, player);
                            if (CasterItemHelper.canOffHand(player, false)) {
                                var gauntlet = CasterItemHelper.getGauntlet(player);
                                JahdooHelpers.hurtAndKeepItem(gauntlet, 5, serverLevel, player);
                            }
                        }
                    } else failedCastNotification(player);
                } else brokenWandNotification(player, getElement);
            } else onCast(player, getAbility);
        }
    }

    public static void brokenWandNotification(Player player, @Nullable AbstractElement element){
        var colour = element == null ? ColourHelpers.getOffWhite() : element.textColourA();
        failedCastNotification(player);
        player.displayClientMessage(TextHelpers.withStyleComponent("Wand too damaged to cast", colour), true);
    }

    public static InteractionResultHolder<ItemStack> use(Player player) {
        var itemStack = JahdooHelpers.getUsedItem(player);
        var canUse = getCanApplyDistanceAbility(player, itemStack);

//        var typeId = CasterData.selectedAbility(player);
//        var getAbility = AbilityReg.getFirstSpellByTypeId(typeId);
//        var cantUseInDim = player.level() instanceof CustomLevel && getAbility.isPresent() && getAbility.get().getElemenType().equals(ElementReg.utility());
//        var fail = InteractionResultHolder.fail(itemStack);
//        if(cantUseInDim) {
//            player.displayClientMessage(Component.literal("You cant use that here"), true);
//            failedCastNotification(player);
//            return fail;
//        }

        if(canUse) executeAndCharge(player); else failedCastNotification(player);

        return InteractionResultHolder.pass(itemStack);
    }

    public static boolean getCanApplyDistanceAbility(Player player, ItemStack itemStack){
        var isDistanceCast = AbilityReg.getFirstSpellByTypeId(CasterData.selectedAbility(player));
        if(isDistanceCast.isPresent() && isDistanceCast.get().getCastType() == DISTANCE_CAST){
            var getCurrentAbility = CasterData.selectedAbility(player);
            var getAbility = JahdooHelpers.getModifierValue(player, getCurrentAbility);
            var allowedDistance = getAbility.get(CASTING_DISTANCE).setValue();
            var lookAtLocation = player.pick(allowedDistance, 0, false);
            var isValidCastLocation = lookAtLocation.getType() == HitResult.Type.MISS;
            var getWandElement = fromWand(itemStack.getItem()).orElse(null);
            var distance = String.valueOf(Math.round(allowedDistance));
            var colour = getWandElement == null ? ColourHelpers.getWalletBrown() : getWandElement.partColourB();
            var distanceCompo = TextHelpers.withStyleComponent(distance, colour);
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
        var typeId = CasterData.selectedAbility(player);
        var ability = AbilityReg.getFirstSpellByTypeId(typeId).orElseThrow();
        var getManaCost = CasterData.getSpecificValue(player, MANA_COST);
        var element = ability.getElemenType();
        var adjustedMana = JahdooHelpers.attributeModifierCalculator(player, (float) getManaCost, false, MANA_COST_REDUCTION, element.manaReduction());
        var manaAvailable = casterData.getManaPool();
        var sufficientMana = casterData.getManaPool() >= adjustedMana;
        var abilityOnCooldown = casterData.isAbilityOnCooldown(typeId);

        if(!player.isCreative()){
            if (abilityOnCooldown) {
                var nameComp = TextHelpers.withStyleComponent(ability.getAbilityName(), element.partColourB());
                var messageComp = Component.translatable("casting.jahdoo.on_cooldown", nameComp);
                player.displayClientMessage(messageComp, true);
                return false;
            }

            if (!sufficientMana) {
                var formattedCost = org.shaydee.shaydeeapi.Maths.getFormattedFloat(adjustedMana);
                var formattedAvailable = org.shaydee.shaydeeapi.Maths.getFormattedFloat((float) manaAvailable);
                var costComp = TextHelpers.withStyleComponent(String.valueOf(formattedCost), element.partColourA());
                var availComp = TextHelpers.withStyleComponent(String.valueOf(formattedAvailable), element.partColourB());
                var notEnoughManaMessage = Component.translatable("casting.jahdoo.insufficient_man", availComp, costComp);
                player.displayClientMessage(notEnoughManaMessage, true);
                return false;
            }
        }
        return true;
    }

}
