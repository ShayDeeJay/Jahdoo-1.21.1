package org.jahdoo.trial_nexus.ability.abilities_combat;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.ability.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.Maths;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.particle.ParticleStore.PLUS_PARTICLE;

public class EtherealArrow extends DefaultEntityBehaviour {

    public static ResourceLocation abilityId = Helpers.res("ethereal_arrow_property");

    double damage;
    double effectDuration;
    double effectStrength;
    double effectChance;
    double elementType;
    double lifeLeechChance;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.damage = this.getTag(DAMAGE);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectChance = this.getTag(EFFECT_CHANCE);
        this.elementType =  this.getTag(SET_ELEMENT_TYPE);
        this.lifeLeechChance = this.getTag(LIFE_LEECH);
    }


    @Override
    public AbilityHolder getAbilityHolder() {
        return this.generic.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return abilityId.getPath().intern();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new EtherealArrow();
    }

    @Override
    public void onTickMethod() {
        if(this.generic != null){
            generic.setDeltaMovement(generic.getDeltaMovement().subtract(0, 0.02, 0));
            arrowPartEffect(this.generic, this.generic.getElementType());
        }
    }

    @Override
    public void discardCondition() {
        if(this.generic != null){
            if (this.generic.tickCount > 30) this.generic.discard();
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(EFFECT_CHANCE, effectChance);
        compoundTag.putDouble(EFFECT_STRENGTH, effectStrength);
        compoundTag.putDouble(EFFECT_DURATION, effectDuration);
        compoundTag.putDouble(DAMAGE, damage);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    public static void arrowPartEffect(Projectile projectile, AbstractElement element) {
        ParticleHandlers.sendParticles(
            projectile.level(), ParticleTypes.INSTANT_EFFECT, projectile.position(), 1,
            0, 0, 0, 0
        );
        playParticles3(
            genericParticle(GENERIC_PARTICLE, element, 4, 1f, false),
            projectile, 20, 0.01
        );
    }

    public static AbilityHolder setArrowProperties(double damage, double effectDuration, double effectStrength, double effectChance, int elementType){
        return new AbilityBuilder(null, EtherealArrow.abilityId.getPath().intern())
            .setModifierWithoutBounds(DAMAGE, damage)
            .setModifierWithoutBounds(EFFECT_DURATION, effectDuration)
            .setModifierWithoutBounds(EFFECT_STRENGTH, effectStrength)
            .setModifierWithoutBounds(EFFECT_CHANCE, effectChance)
            .setModifierWithoutBounds(SET_ELEMENT_TYPE, elementType)
            .setModifierWithoutBounds(LIFE_LEECH, 0)
            .buildAndReturn();
    }

    public static AbilityHolder setArrowProperties(double damage, double effectDuration, double effectStrength, double effectChance, int elementType, double lifeLeechChance){
        return new AbilityBuilder(null, EtherealArrow.abilityId.getPath().intern())
            .setModifierWithoutBounds(DAMAGE, damage)
            .setModifierWithoutBounds(EFFECT_DURATION, effectDuration)
            .setModifierWithoutBounds(EFFECT_STRENGTH, effectStrength)
            .setModifierWithoutBounds(EFFECT_CHANCE, effectChance)
            .setModifierWithoutBounds(SET_ELEMENT_TYPE, elementType)
            .setModifierWithoutBounds(LIFE_LEECH, lifeLeechChance)
            .buildAndReturn();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        if(this.generic != null){
            var element = generic.getElementType();

            if (element.equals(ElementReg.vitality())) {
                if(Maths.percentageChance(this.lifeLeechChance)){
                    if (generic.getOwner() instanceof EternalWizard eternalWizard) {
                        eternalWizard.heal(2);
                        Helpers.getSoundWithPositionV(eternalWizard.level(), eternalWizard.position(), ElementReg.vitality().sound(), 1f, 1.2f);
                        Helpers.getSoundWithPositionV(eternalWizard.level(), eternalWizard.position(), SoundReg.IMPACT.get(), 1f, 0.8f);
                        particleBurst(
                            eternalWizard.level(), eternalWizard.position().add(0, 0.2, 0), 15,
                            genericParticle(PLUS_PARTICLE, element, 5, 1.4f),
                            0, 1.5, 0, 0.1f
                        );
                    }
                }
            }

            Helpers.getSoundWithPosition(this.generic.level(), hitEntity.blockPosition(), element.sound(),0.4f);
            if (hitEntity.isAlive()) {
                if (!(this.generic.level() instanceof ServerLevel serverLevel)) return;
                var type = bakedParticle(element.id(), 10, 1, false);
                var generic = genericParticle(element, 10, 1.2f);

                spawnElectrifiedParticles(serverLevel, hitEntity.position(), type, 3, hitEntity, 0.2);
                spawnElectrifiedParticles(serverLevel, hitEntity.position(), generic,3, hitEntity, 0.2);
            }

            if (Helpers.Random.nextInt(0, (int) Math.max(effectChance, 1)) == 0 || effectChance == -1) {
                var effect = new JahdooMobEffect(element.effect(), (int) effectDuration, (int) effectStrength);
                hitEntity.addEffect(effect);
            }

            DamageUtils.damageWithJahdoo(hitEntity, this.generic.getOwner(), (float) damage, ElementReg.fromId((int) this.elementType).get().damageTypeResourceKey());
            this.generic.discard();
        }
    }

}
