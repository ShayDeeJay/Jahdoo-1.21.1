package org.jahdoo.ability.all_abilities.ability_components;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.ability.AbilityBuilder;

import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.ability.AbilityBuilder.*;

public class EtherealArrow extends DefaultEntityBehaviour {

    public static ResourceLocation abilityId = ModHelpers.res("ethereal_arrow_property");

    double damage;
    double effectDuration;
    double effectStrength;
    double effectChance;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.damage = this.getTag(DAMAGE);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectChance = this.getTag(EFFECT_CHANCE);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.genericProjectile.wandAbilityHolder();
    }

    @Override
    public String abilityId() {
        return abilityId.getPath().intern();
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

    public static WandAbilityHolder setArrowProperties(double damage, double effectDuration, double effectStrength, double effectChance){
        return new AbilityBuilder(null, EtherealArrow.abilityId.getPath().intern())
            .setModifierWithoutBounds(DAMAGE, damage)
            .setModifierWithoutBounds(EFFECT_DURATION, effectDuration)
            .setModifierWithoutBounds(EFFECT_STRENGTH, effectStrength)
            .setModifierWithoutBounds(EFFECT_CHANCE, effectChance)
            .buildAndReturn();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {

        if(this.genericProjectile != null){
            ModHelpers.getSoundWithPosition(this.genericProjectile.level(), hitEntity.blockPosition(), genericProjectile.getElementType().getElementSound(),0.4f);
            if (hitEntity.isAlive()) {
                if (!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
                spawnElectrifiedParticles(
                    serverLevel, hitEntity.position(),
                    new BakedParticleOptions(
                        genericProjectile.getElementType().getTypeId(),
                        10, 1, false
                    ),
                    3, hitEntity, 0.2

                );

                spawnElectrifiedParticles(
                    serverLevel, hitEntity.position(),
                    genericParticleOptions(genericProjectile.getElementType(), 10, 1.2f)
                    ,3, hitEntity, 0.2
                );
            }

            if (ModHelpers.Random.nextInt(0, (int) Math.max(effectChance, 1)) == 0) {
                hitEntity.addEffect(new CustomMobEffect(genericProjectile.getElementType().elementEffect(), (int) effectDuration, (int) effectStrength));
            }

            DamageUtil.damageWithJahdoo(hitEntity, this.genericProjectile.getOwner(), (float) damage);
            this.genericProjectile.discard();
        }
    }

    @Override
    public void onTickMethod() {
        if(this.genericProjectile != null){
            genericProjectile.setDeltaMovement(genericProjectile.getDeltaMovement().subtract(0, 0.02, 0));
            arrowPartEffect(this.genericProjectile, this.genericProjectile.getElementType());
        }
    }

    public static void arrowPartEffect(Projectile projectile, AbstractElement element) {
        ParticleHandlers.sendParticles(
            projectile.level(), ParticleTypes.INSTANT_EFFECT, projectile.position(), 1,
            0, 0, 0, 0
        );
        playParticles3(
            genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, element, 6, 1f, false),
            projectile, 20, 0.01
        );
    }

    @Override
    public void discardCondition() {
        if(this.genericProjectile != null){
            if (this.genericProjectile.tickCount > 30) this.genericProjectile.discard();
        }
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new EtherealArrow();
    }
}
