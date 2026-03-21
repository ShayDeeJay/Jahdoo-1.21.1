package org.jahdoo.trial_nexus.magic.abilities_combat.boltz;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.magic.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.trial_nexus.magic.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class Boltz extends DefaultEntityBehaviour {

    private static final ResourceLocation abilityId = JahdooHelpers.res("boltz_property");
    private double effectChance;
    private double effectStrength;
    private double effectDuration;
    private double dischargeRadius;
    private double damage;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        this.element = elementProjectile;
        this.effectChance = this.getTag(EFFECT_CHANCE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.dischargeRadius = this.getTag(BoltzAbility.DISCHARGE_RADIUS);
//        if(this.elementProjectile.getOwner() != null){
//            var player = this.elementProjectile.getOwner();
//            var damage = this.getTag(DAMAGE);
//            this.damage = ModHelpers.attributeModifierCalculator(
//                (LivingEntity) player,
//                (float) damage,
//                true,
//                MAGIC_DAMAGE_MULTIPLIER,
//                LIGHTNING_MAGIC_DAMAGE_MULTIPLIER
//            );
//        }
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.element.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return  BoltzAbility.abilityId.getPath().intern();
    }

    @Override
    public void onTickMethod() {
        this.element.setAnimation(9);
        applyInertia(this.element);
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.frost();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Boltz();
    }

    public void sharedSound(SoundEvent sEvent, Float volume, Float pitch){
        SoundHelpers.getSoundWithPosition(this.element.level(), this.element.blockPosition(), sEvent, SoundSource.NEUTRAL, volume, pitch);
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.dischargeEffect();
        sharedSound(SoundReg.EXPLOSION.get(), 0.2F, 2F);
        this.element.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        LivingEntity owner = (LivingEntity) this.element.getOwner();
        if(DefaultEntityBehaviour.canDamageEntity(hitEntity, (LivingEntity) this.element.getOwner())){
            DamageUtils.damageWithJahdoo(hitEntity, owner, (float) this.damage, getElementType().damageTypeResourceKey());
            sharedSound(SoundReg.FROST_ABILITY.get(), 0.1F, 1F);
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(EFFECT_CHANCE, effectChance);
        compoundTag.putDouble(EFFECT_STRENGTH, effectStrength);
        compoundTag.putDouble(EFFECT_DURATION, effectDuration);
        compoundTag.putDouble(DAMAGE, damage);
        compoundTag.putDouble(BoltzAbility.DISCHARGE_RADIUS, dischargeRadius);
    }

    public void applyInertia(Projectile projectile) {
        var inertiaFactor = Random.nextDouble(0.85, 0.90); // Adjust this value to control the rate of slowdown (0.98 means 2% reduction per tick)
        var currentVelocity = projectile.getDeltaMovement();
        var newVelocityX = currentVelocity.x * inertiaFactor;
        var newVelocityY = currentVelocity.y * inertiaFactor;
        var newVelocityZ = currentVelocity.z * inertiaFactor;

        projectile.setDeltaMovement(newVelocityX, newVelocityY, newVelocityZ);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.dischargeRadius = compoundTag.getDouble(BoltzAbility.DISCHARGE_RADIUS);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    @Override
    public void discardCondition() {
        var getRandom = Random.nextInt(20, 50);
        var projectile = this.element;

        if(projectile.tickCount > 5) {
            var min = Math.min(dischargeRadius / 4, 0.5);
            var max = Math.max(dischargeRadius / 6, 0.4);
            orbEnergyParticles(projectile, min, max);
        }

        if(projectile.tickCount >= getRandom) {
            this.dischargeEffect();
            projectile.discard();
        }
    }

    private void dischargeEffect(){
        var projectile = this.element;
        var owner = projectile.getOwner();
//        var instance = new JahdooMobEffect(getElementType().effect(), (int) effectDuration, (int) effectStrength);
//        var nearbyEntities = projectile.level().getNearbyEntities(
//            LivingEntity.class,
//            TargetingConditions.DEFAULT,
//            (LivingEntity) owner,
//            projectile.getBoundingBox().inflate(dischargeRadius)
//        );
//
//        for (var nearbyEntity : nearbyEntities) {
//            if (DefaultEntityBehaviour.canDamageEntity(nearbyEntity, (LivingEntity) owner)) {
//                DamageUtils.damageWithJahdoo(nearbyEntity, owner, (float) this.damage, this.getElementType().damageTypeResourceKey());
//                if(Random.nextInt(0, (int) effectChance) == 0) {
//                    nearbyEntity.addEffect(instance);
//                }
//            }
//        }

        if(projectile.level() instanceof ServerLevel serverLevel){
            var particleOptions = ParticleHandlers.genericParticle(
                GENERIC_PARTICLE, this.getElementType(), Random.nextInt(2,8), 1.4f, 0
            );
            ParticleHandlers.particleBurst(serverLevel, projectile.position(), 1, particleOptions, 0, 0, 0, (float) dischargeRadius / 15);
        }

        element.playSound(SoundReg.FROST_ABILITY.get(),0.4F,1.5F);
    }

    void orbEnergyParticles(Projectile projectile, double numberOfPoints, double radius){
        var level = projectile.level();

        var bakedParticle = bakedParticle(
            this.getElementType().id(), Random.nextInt(2,8), 1, false
        );

        var particleOptions = ParticleHandlers.genericParticle(
            ParticleStore.ELECTRIC_PARTICLE, this.getElementType(), Random.nextInt(2,8), 1.2f, this.dischargeRadius/10
        );

        var velocityA = JahdooHelpers.getRandomParticleVelocity(projectile, 0.1);
        var velocityB = JahdooHelpers.getRandomParticleVelocity(projectile, 0.05);

        PositionFinders.getRandomSphericalPositions(projectile, radius, numberOfPoints,
            position -> {
                ParticleHandlers.sendParticles(
                    level, bakedParticle, position.add(0,0.2,0), 0,
                    velocityA.x, velocityA.y, velocityA.z, 0.3
                );
            }
        );

        PositionFinders.getRandomSphericalPositions(projectile, radius, numberOfPoints * 5,
            position -> {
                ParticleHandlers.sendParticles(
                    level, particleOptions, position.add(0,0.2,0), 0,
                    velocityB.x, velocityB.y, velocityB.z, 0
                );
            }
        );
    }

}
