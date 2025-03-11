package org.jahdoo.ascension.ability.abilities.vital_rejuvenation;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.attachments.AbstractHoldUseAttachment;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.items.wand.CastHelper;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;

import java.util.Collections;
import java.util.Optional;

import static org.jahdoo.ascension.ability.AbilityBuilder.MANA_COST;
import static org.jahdoo.ascension.ability.abilities.vital_rejuvenation.VitalRejuvenationAbility.CAST_DELAY;
import static org.jahdoo.ascension.ability.abilities.vital_rejuvenation.VitalRejuvenationAbility.MAX_ABSORPTION;
import static org.jahdoo.common.items.wand.CastHelper.validManaAndCooldown;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.common.registers.AttachmentReg.VITAL_REJUVENATION;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.Helpers.addTransientAttribute;

public class VitalRejuvenation extends AbstractHoldUseAttachment {

    String name = VitalRejuvenationAbility.abilityId.getPath().intern();
    private static final int spacers = 3;
    private int ticksUsing;
    private int counter;

    public static void staticTickEvent(Player player){
        player.getData(VITAL_REJUVENATION).onTickMethod(player);
    }

    private void unSuccessfulCast(Player player) {
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundReg.HEAL.get(), 0.8f, 0.8f);
        PositionFinders.getOuterRingOfRadius(player.position(), 0.3, 30, vec3 -> setCastingAnimation(vec3, player));
    }

    public static void successfulCastAnimation(LivingEntity player) {
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, 1f,1.2f);
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundReg.HEAL.get(), 1f,1f);
        PositionFinders.getOuterRingOfRadius(player.position(), 0.2, 30, vec3 -> setRejuvenationSuccessEffect(vec3, player));
    }

    private void successfulCast(Player player, WandAbilityHolder wandAbilityHolder) {
        var mana = DataComponentHelper.getSpecificValue(name, wandAbilityHolder, MANA_COST);
        CastHelper.chargeMana(name, mana, player);
        applyHeal(player, wandAbilityHolder);
        successfulCastAnimation(player);
        counter = 0;
    }

    private void applyHeal(Player player, WandAbilityHolder wandAbilityHolder) {
        var maxAbsorption = DataComponentHelper.getSpecificValue(name, wandAbilityHolder, MAX_ABSORPTION);
        var foodProperties = new FoodProperties(2, 2, true, 0, Optional.empty(), Collections.emptyList());
        player.heal(1);
        addTransientAttribute(player, maxAbsorption * 2, "absorption", Attributes.MAX_ABSORPTION);
        player.setAbsorptionAmount(player.getAbsorptionAmount() + 1);
        player.eat(player.level(), ItemStack.EMPTY, foodProperties);
    }

    private void setCastingAnimation(Vec3 worldPosition, Player player){
        var directions = worldPosition.subtract(player.position()).normalize().add(0,8, 0).offsetRandom(RandomSource.create(), 0.05f);
        var lifetime = 6;
        var element = ElementReg.vitality();
        var col1 = element.partColourA();
        var col2 = element.partColourFade();

        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.1f, col1, col2, true);
        if(player.level().isClientSide){
            ParticleHandlers.sendParticles(
                player.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 3.5
            );
        }
    }

    public static void setRejuvenationSuccessEffect(Vec3 worldPosition, LivingEntity livingEntity){
        var directions = worldPosition.subtract(livingEntity.position()).normalize();
        var lifetime = 8;
        var element = ElementReg.vitality();
        var col1 = element.partColourA();
        var col2 = element.partColourFade();
        var bakedParticle = bakedParticleOptions(element.id(), lifetime, 0.1f, true);
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.1f, col1, col2, true);

        if(livingEntity.level().isClientSide){
            ParticleHandlers.sendParticles(livingEntity.level(), bakedParticle, worldPosition, 0, directions.x, directions.y + 4, directions.z, Random.nextDouble(3, 6));
            ParticleHandlers.sendParticles(livingEntity.level(), genericParticle, worldPosition, 0, directions.x, directions.y + 4, directions.z, Random.nextDouble(3, 6));
        }
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

}
