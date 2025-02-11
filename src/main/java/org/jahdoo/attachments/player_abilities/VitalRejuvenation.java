package org.jahdoo.attachments.player_abilities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.abilities.ability_data.VitalRejuvenationAbility;
import org.jahdoo.attachments.AbstractHoldUseAttachment;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.items.wand.CastHelper;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.Collections;
import java.util.Optional;

import static org.jahdoo.ability.AbilityBuilder.MANA_COST;
import static org.jahdoo.ability.abilities.ability_data.VitalRejuvenationAbility.CAST_DELAY;
import static org.jahdoo.ability.abilities.ability_data.VitalRejuvenationAbility.MAX_ABSORPTION;
import static org.jahdoo.items.wand.CastHelper.validManaAndCooldown;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttachmentRegister.VITAL_REJUVENATION;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.ModHelpers.addTransientAttribute;

public class VitalRejuvenation extends AbstractHoldUseAttachment {

    String name = VitalRejuvenationAbility.abilityId.getPath().intern();
    int ticksUsing;
    int counter;
    private static final int spacers = 3;

    public static void staticTickEvent(Player player){
        player.getData(VITAL_REJUVENATION).onTickMethod(player);
    }

    @Override
    public void onTickMethod(Player player) {
        super.onTickMethod(player);
        if(this.startedUsing && validManaAndCooldown(player)){
            var wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
            if(wandAbilityHolder == null || !wandAbilityHolder.abilityProperties().containsKey(name)) return;
            var castDelay = DataComponentHelper.getSpecificValue(name, wandAbilityHolder, CAST_DELAY);


            if(ticksUsing == 0) {
                counter++;
                unSuccessfulCast(player);
            }

            if(ticksUsing > castDelay){
                counter++;
                if (counter < spacers) unSuccessfulCast(player);
                if (counter == spacers) successfulCast(player, wandAbilityHolder);
                ticksUsing = 0;
            }

            ticksUsing++;
        } else {
            if(ticksUsing > 0) ticksUsing = 0;
            if(counter > 0) counter = 0;
        }
    }

    private void successfulCast(Player player, WandAbilityHolder wandAbilityHolder) {
        var mana = DataComponentHelper.getSpecificValue(name, wandAbilityHolder, MANA_COST);
        CastHelper.chargeMana(name, mana, player);
        applyHeal(player, wandAbilityHolder);
        successfulCastAnimation(player);
        counter = 0;
    }

    private void unSuccessfulCast(Player player) {
        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.HEAL.get(), 0.8f, 0.8f);
        PositionGetters.getOuterRingOfRadius(player.position(), 0.3, 30, vec3 -> setCastingAnimation(vec3, player));
    }

    private void applyHeal(Player player, WandAbilityHolder wandAbilityHolder) {
        var maxAbsorption = DataComponentHelper.getSpecificValue(name, wandAbilityHolder, MAX_ABSORPTION);
        var foodProperties = new FoodProperties(2, 2, true, 0, Optional.empty(), Collections.emptyList());
        player.heal(1);
        addTransientAttribute(player, maxAbsorption * 2, "absorption", Attributes.MAX_ABSORPTION);
        player.setAbsorptionAmount(player.getAbsorptionAmount() + 1);
        player.eat(player.level(), ItemStack.EMPTY, foodProperties);
    }

    public static void successfulCastAnimation(LivingEntity player) {
        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, 1f,1.2f);
        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.HEAL.get(), 1f,1f);
        PositionGetters.getOuterRingOfRadius(player.position(), 0.2, 30, vec3 -> setRejuvenationSuccessEffect(vec3, player));
    }

    public static void setRejuvenationSuccessEffect(Vec3 worldPosition, LivingEntity livingEntity){
        var directions = worldPosition.subtract(livingEntity.position()).normalize();
        var lifetime = 8;
        var element = ElementRegistry.VITALITY.get();
        var col1 = element.particleColourPrimary();
        var col2 = element.particleColourFaded();
        var bakedParticle = bakedParticleOptions(7, lifetime, 0.1f, true);
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.1f, col1, col2, true);

        if(livingEntity.level().isClientSide){
            ParticleHandlers.sendParticles(livingEntity.level(), bakedParticle, worldPosition, 0, directions.x, directions.y + 4, directions.z, Random.nextDouble(3, 6));
            ParticleHandlers.sendParticles(livingEntity.level(), genericParticle, worldPosition, 0, directions.x, directions.y + 4, directions.z, Random.nextDouble(3, 6));
        }
    }

    private void setCastingAnimation(Vec3 worldPosition, Player player){
        var directions = worldPosition.subtract(player.position()).normalize().add(0,8, 0).offsetRandom(RandomSource.create(), 0.05f);
        var lifetime = 6;
        var element = ElementRegistry.VITALITY.get();
        var col1 = element.particleColourPrimary();
        var col2 = element.particleColourFaded();

        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.1f, col1, col2, true);
        if(player.level().isClientSide){
            ParticleHandlers.sendParticles(
                player.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 3.5
            );
        }
    }
}
