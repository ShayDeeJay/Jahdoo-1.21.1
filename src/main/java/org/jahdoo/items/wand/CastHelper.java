package org.jahdoo.items.wand;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.client.SharedUI;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.ability.AbilityRegistrar.DISTANCE_CAST;
import static org.jahdoo.ability.AbilityRegistrar.HOLD_CAST;
import static org.jahdoo.components.DataComponentHelper.getSpecificValue;
import static org.jahdoo.items.wand.WandAnimations.*;
import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;
import static org.jahdoo.registers.AttributesRegister.COOLDOWN_REDUCTION;
import static org.jahdoo.registers.AttributesRegister.MANA_COST_REDUCTION;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;
import static org.jahdoo.utils.ModHelpers.*;


public class CastHelper {

    public static void chargeCooldown(String abilityId, double cooldown, Player player) {
        if(player.isCreative()) return;
        var cooldownSystem = player.getData(CASTER_DATA);
        var ability = AbilityRegister.getFirstSpellByTypeId(abilityId);
        var wand = player.getMainHandItem();
        var getElement = SharedUI.getElementWithType(ability.orElseThrow(), wand);
        var reCalculatedCooldown = ModHelpers.attributeModifierCalculator(player, (float) cooldown, false,  getElement.getTypeCooldownReduction().getSecond(), COOLDOWN_REDUCTION);
        cooldownSystem.addCooldown(abilityId, (int) reCalculatedCooldown);
    }

    public static void chargeMana(String abilityId, double manaCost, Player player) {
        if(player.isCreative()) return;
        var manaSystem = player.getData(CASTER_DATA);
        var ability = AbilityRegister.getFirstSpellByTypeId(abilityId);
        var wand = player.getMainHandItem();
        var getElement = SharedUI.getElementWithType(ability.orElseThrow(), wand);
        var reCalculatedMana = ModHelpers.attributeModifierCalculator(player, (float) manaCost, false, getElement.getTypeManaReduction().getSecond(), MANA_COST_REDUCTION);
        manaSystem.subtractMana(reCalculatedMana, player);
    }

    public static void chargeManaAndCooldown(String abilityId, Player player){
        var wandItem = player.getMainHandItem();
        var cooldownCost = getSpecificValue(player, wandItem, COOLDOWN);
        var getManaCost = getSpecificValue(player, wandItem, MANA_COST);
        chargeMana(abilityId, getManaCost, player);
        chargeCooldown(abilityId, cooldownCost, player);
    }

    public static void executeAndCharge(Player player) {
        var casterData = player.getData(CASTER_DATA);
        var wandItem = player.getMainHandItem();
        var abilityName = DataComponentHelper.getAbilityTypeItemStack(wandItem);
        var ability = AbilityRegister.REGISTRY.get(res(abilityName));
        if (ability == null) return;
        var getElement = SharedUI.getElementWithType(ability, wandItem);

        if(!player.isCreative()){
            if(validManaAndCooldown(player)){
                if(!ability.internallyChargeManaAndCooldown()){
                    var cooldownCost = getSpecificValue(player, wandItem, COOLDOWN);
                    var getManaCost = getSpecificValue(player, wandItem, MANA_COST);
                    var adjustedMana = ModHelpers.attributeModifierCalculator(player, (float) getManaCost, false, getElement.getTypeManaReduction().getSecond(), MANA_COST_REDUCTION);
                    var adjustedCooldown = ModHelpers.attributeModifierCalculator(player, (float) cooldownCost, false, getElement.getTypeCooldownReduction().getSecond(), COOLDOWN_REDUCTION);
                    chargeCooldown(abilityName, adjustedCooldown, player);
                    chargeMana(abilityName, adjustedMana, player);

//                    casterData.addCooldown(abilityName, (int) adjustedCooldown);
//                    casterData.subtractMana(adjustedMana, player);
                }
                onCast(player, ability);
            } else failedCastNotification(player);
        } else onCast(player, ability);
    }

    public static void failedCastNotification(Player player) {
        SoundEvent crafterFail = SoundEvents.CRAFTER_FAIL;
        float volume = 1.6f;
        float pitch = 1.2f;
        SoundEvent vexHurt = SoundEvents.VEX_HURT;
        float volume1 = 0.8f;
        float pitch1 = 1.3f;
        if(player instanceof ServerPlayer serverPlayer){
            ModHelpers.sendClientSound(serverPlayer, crafterFail, volume, pitch, false);
            ModHelpers.sendClientSound(serverPlayer, vexHurt, volume1, pitch1, false);
        } else {
            player.playSound(crafterFail, volume, pitch);
            player.playSound(vexHurt, volume1, pitch1);
        }
        castAnimation(player, CANT_CAST_ID);
    }

    public static void castAnimation(LivingEntity livingEntity, String anim) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            if(livingEntity.getMainHandItem().getItem() instanceof WandItem wandItem){
                triggerAnimWithController(wandItem, livingEntity.getMainHandItem(), serverLevel, livingEntity, anim);
            }
        }
    }

    public static boolean validManaAndCooldown(Player player){
        var casterData = player.getData(CASTER_DATA);
        var wandItem = player.getMainHandItem();
        var abilityName = DataComponentHelper.getAbilityTypeItemStack(wandItem);
        var ability = AbilityRegister.REGISTRY.get(res(abilityName));
        if(ability == null) return false;
        var getElement = getElementByWandType(wandItem.getItem()).getFirst();;
        var getManaCost = getSpecificValue(player, wandItem, MANA_COST);
        var adjustedMana = ModHelpers.attributeModifierCalculator(player, (float) getManaCost, false, MANA_COST_REDUCTION, getElement.getTypeManaReduction().getSecond());
        var manaAvailable = casterData.getManaPool();
        var sufficientMana = casterData.getManaPool() >= adjustedMana;
        var abilityOnCooldown = casterData.isAbilityOnCooldown(abilityName);

        if (abilityOnCooldown) {
            var nameComp = ModHelpers.withStyleComponent(ability.getAbilityName(), getElement.particleColourSecondary());
            var messageComp = Component.translatable("casting.jahdoo.on_cooldown", nameComp);
            player.displayClientMessage(messageComp, true);
            return false;
        }

        if (!sufficientMana) {
            var formattedCost = getFormattedFloat(adjustedMana);
            var formattedAvailable = getFormattedFloat((float) manaAvailable);
            var costComp = ModHelpers.withStyleComponent(String.valueOf(formattedCost), getElement.particleColourPrimary());
            var availComp = ModHelpers.withStyleComponent(String.valueOf(formattedAvailable), getElement.particleColourSecondary());
            var notEnoughManaMessage = Component.translatable("casting.jahdoo.insufficient_man", availComp, costComp);
            player.displayClientMessage(notEnoughManaMessage, true);
            return false;
        }

        return true;
    }

    private static void debugMessages(Player player, ItemStack wandItem, AbstractElement getElement, double cooldownCost, float adjustedCooldown, double getManaCost, float adjustedMana) {
        playDebugMessage(player, wandItem);
        playDebugMessage(player, getElement.getElementName());
        playDebugMessage(player, "cooldown = " + cooldownCost);
        playDebugMessage(player, "adjusted cooldown = " + adjustedCooldown);
        playDebugMessage(player, "mana = " + getManaCost);
        playDebugMessage(player, "adjusted mana = " + adjustedMana);
    }

    public static void onCast(Player player, AbilityRegistrar ability){
        if(!ability.internallyChargeManaAndCooldown()){
            castAnimation(player, SINGLE_CAST_ID);
        }
        ability.invokeAbility(player);
        if(ability.getCastType() != HOLD_CAST) player.stopUsingItem();
    }

    public static InteractionResultHolder<ItemStack> use(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        ResourceLocation abilityName = DataComponentHelper.getAbilityTypeWand(player);
        AbilityRegistrar getAbility = AbilityRegister.REGISTRY.get(abilityName);

        if((player.onGround() && player.isShiftKeyDown()) || getAbility == null) {
            return InteractionResultHolder.fail(itemStack);
        }

        if (getCanApplyDistanceAbility(player, itemStack)) {
            executeAndCharge(player);
        } else {
            failedCastNotification(player);
        }

        return InteractionResultHolder.pass(itemStack);
    }

    public static void onSuccessfulCastBonus(){

    }

    public static boolean getCanApplyDistanceAbility(Player player, ItemStack itemStack){
        boolean isDistanceCast = AbilityRegister.REGISTRY.get(DataComponentHelper.getAbilityTypeWand(player)).getCastType() == DISTANCE_CAST;
        if(isDistanceCast){
            var getCurrentAbility = DataComponentHelper.getAbilityTypeItemStack(itemStack);
            var getAbility = ModHelpers.getModifierValue(itemStack.get(WAND_ABILITY_HOLDER.get()), getCurrentAbility);
            var allowedDistance = getAbility.get(CASTING_DISTANCE).actualValue();
            var lookAtLocation = player.pick(allowedDistance, 0, false);
            var isValidCastLocation = lookAtLocation.getType() == HitResult.Type.MISS;
            var getWandElement = getElementByWandType(itemStack.getItem());
            var distance = String.valueOf(Math.round(allowedDistance));
            var colour = getWandElement.getFirst().particleColourSecondary();
            var distanceCompo = ModHelpers.withStyleComponent(distance, colour);
            var notAllowedDistanceMessage = Component.translatable("casting.jahdoo.distance", distanceCompo);

            if (isValidCastLocation) {
                player.displayClientMessage(notAllowedDistanceMessage, true);
                player.stopUsingItem();
                return false;
            }
        }
        return true;
    }

}
