package org.jahdoo.trial_nexus.ability.abilities_combat.vital_rejuvenation;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.attachments.AbstractHoldUseAttachment;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import java.util.Collections;
import java.util.Optional;

import static org.jahdoo.trial_nexus.ability.abilities_combat.vital_rejuvenation.VitalRejuvenationAbility.CAST_DELAY;
import static org.jahdoo.trial_nexus.ability.abilities_combat.vital_rejuvenation.VitalRejuvenationAbility.MAX_ABSORPTION;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;
import static org.jahdoo.trial_nexus.utils.Helpers.addTransientAttribute;
import static org.jahdoo.common.items.caster_item.CastHelper.validManaAndCooldown;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.AttachmentReg.VITAL_REJUVENATION;

public class VitalRejuvenation extends AbstractHoldUseAttachment {

    String name = VitalRejuvenationAbility.abilityId.getPath().intern();
    private static final int spacers = 3;
    private int ticksUsing;
    private int counter;

    public static void staticTickEvent(Player player){
        player.getData(VITAL_REJUVENATION).onTickMethod(player);
    }

    private void unSuccessfulCast(Player player) {
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), ElementReg.vitality().sound(), 0.8f, 1.2f);
        PositionFinders.getOuterRingOfRadius(player.position(), 0.3, 30, vec3 -> setCastingAnimation(vec3, player));
    }

    public static void successfulCastAnimation(LivingEntity player) {
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), ElementReg.vitality().sound(), 1f,1.2f);
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundReg.IMPACT.get(), 1f,0.8f);
        setRejuvenationSuccessEffect(player);
    }

    private void successfulCast(Player player, AbilityHolder wandAbilityHolder) {
        CastHelper.chargeMana(name, player);
        applyHeal(player, wandAbilityHolder);
        successfulCastAnimation(player);
        counter = 0;
    }

    private void applyHeal(Player player, AbilityHolder abilityBuilder) {
        var maxAbsorption = CasterData.getSpecificValue(abilityBuilder, MAX_ABSORPTION);
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
        var genericParticle = ParticleHandlers.genericParticle(SOFT_PARTICLE, lifetime, 0.1f, col1, col2, true);

        ParticleHandlers.sendParticles(
            player.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.05
        );
    }

    public static void setRejuvenationSuccessEffect(LivingEntity livingEntity){
        var position = livingEntity.position();

        PositionFinders.innerRadiusRandom(
            position.add(0,livingEntity.getBbHeight() /2, 0), 3, 100,
            positions -> {
                var directions = position
                    .subtract(positions)
                    .normalize()
                    .add(0,livingEntity.getBbHeight() /2,0);

                ParticleHandlers.sendParticles(
                    livingEntity.level(),
                    ParticleHandlers.getAllParticleTypes(ElementReg.vitality(), 18, 2),
                    positions,
                    0,
                    directions.x,
                    Random.nextDouble(-0.3, 0.3),
                    directions.z,
                    0.3
                );
            }
        );

    }

    @Override
    public void onTickMethod(Player player) {
        super.onTickMethod(player);
        if(this.startedUsing && validManaAndCooldown(player)){
            var abilityHolder = CasterData.entityHolderWithSelected(player);
            if(abilityHolder == null) return;
            var castDelay = CasterData.getSpecificValue(abilityHolder, CAST_DELAY);

            player.setDeltaMovement(0, player.getDeltaMovement().y, 0);

            if(ticksUsing == 0) {
                counter++;
                unSuccessfulCast(player);
            }

            if(ticksUsing > castDelay){
                counter++;
                if (counter < spacers) unSuccessfulCast(player);
                if (counter == spacers) successfulCast(player, abilityHolder);
                ticksUsing = 0;
            }

            ticksUsing++;
        } else {
            if(ticksUsing > 0) ticksUsing = 0;
            if(counter > 0) counter = 0;
        }
    }

}
