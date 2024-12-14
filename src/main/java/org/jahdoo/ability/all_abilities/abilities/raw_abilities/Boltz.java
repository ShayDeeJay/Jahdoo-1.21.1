package org.jahdoo.ability.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.all_abilities.abilities.BoltzAbility;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttributesRegister.LIGHTNING_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.utils.ModHelpers.Random;

public class Boltz extends DefaultEntityBehaviour {

    double effectChance;
    double effectStrength;
    double effectDuration;
    double dischargeRadius;
    double damage;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        this.elementProjectile = elementProjectile;
        this.effectChance = this.getTag(EFFECT_CHANCE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.dischargeRadius = this.getTag(BoltzAbility.dischargeRadius);
        if(this.elementProjectile.getOwner() != null){
            var player = this.elementProjectile.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = ModHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                this.getElementType(),
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                LIGHTNING_MAGIC_DAMAGE_MULTIPLIER
            );
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(EFFECT_CHANCE, effectChance);
        compoundTag.putDouble(EFFECT_STRENGTH, effectStrength);
        compoundTag.putDouble(EFFECT_DURATION, effectDuration);
        compoundTag.putDouble(DAMAGE, damage);
        compoundTag.putDouble(BoltzAbility.dischargeRadius, dischargeRadius);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.dischargeRadius = compoundTag.getDouble(BoltzAbility.dischargeRadius);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.elementProjectile.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return  BoltzAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.dischargeEffect();
        ModHelpers.getSoundWithPosition(this.elementProjectile.level(), blockHitResult.getBlockPos(), SoundRegister.EXPLOSION.get(),0.2f,2f);
        this.elementProjectile.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        LivingEntity owner = (LivingEntity) this.elementProjectile.getOwner();
        if(DefaultEntityBehaviour.canDamageEntity(hitEntity, (LivingEntity) this.elementProjectile.getOwner())){
            DamageUtil.damageWithJahdoo(hitEntity, owner, (float) this.damage);
            ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.BOLT.get(), 0.1f);
        }
    }

    private void dischargeEffect(){
        var projectile = this.elementProjectile;
        var owner = projectile.getOwner();
        projectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) owner,
            projectile.getBoundingBox().inflate(dischargeRadius)
        ).forEach(
            livingEntity -> {
                if (DefaultEntityBehaviour.canDamageEntity(livingEntity, (LivingEntity) owner)) {
                    DamageUtil.damageWithJahdoo(livingEntity, owner, (float) this.damage);
                    if(Random.nextInt(0, (int) effectChance) == 0) {
                        livingEntity.addEffect(new CustomMobEffect(EffectsRegister.LIGHTNING_EFFECT.getDelegate(), (int) effectDuration, (int) effectStrength));
                    }
                }
            }
        );
        if(projectile.level() instanceof ServerLevel serverLevel){
            var particleOptions = genericParticleOptions(
                GENERIC_PARTICLE_SELECTION, this.getElementType(), Random.nextInt(2,8), 1.4f, 0
            );
            ParticleHandlers.particleBurst(serverLevel, projectile.position(), 1, particleOptions, 0, 0, 0, (float) dischargeRadius / 15);
        }
        ModHelpers.getSoundWithPosition(projectile.level(), projectile.blockPosition(), SoundRegister.BOLT.get(),0.4f,1.5f);
    }

    @Override
    public void onTickMethod() {
        this.elementProjectile.setAnimation(9);
        applyInertia(this.elementProjectile);
    }

    public void applyInertia(Projectile projectile) {
        double inertiaFactor = Random.nextDouble(0.85, 0.90); // Adjust this value to control the rate of slowdown (0.98 means 2% reduction per tick)
        Vec3 currentVelocity = projectile.getDeltaMovement();

        double newVelocityX = currentVelocity.x * inertiaFactor;
        double newVelocityY = currentVelocity.y * inertiaFactor;
        double newVelocityZ = currentVelocity.z * inertiaFactor;

        projectile.setDeltaMovement(newVelocityX, newVelocityY, newVelocityZ);
    }

    void orbEnergyParticles(Projectile projectile, double numberOfPoints, double radius){
        var level = projectile.level();

        var bakedParticle = bakedParticleOptions(
            this.getElementType().getTypeId(), Random.nextInt(2,8), 1, false
        );

        var particleOptions = genericParticleOptions(
            ParticleStore.ELECTRIC_PARTICLE_SELECTION, this.getElementType(), Random.nextInt(2,8), 1.2f, this.dischargeRadius/10
        );

        var velocityA = ModHelpers.getRandomParticleVelocity(projectile, 0.1);
        var velocityB = ModHelpers.getRandomParticleVelocity(projectile, 0.05);

        PositionGetters.getRandomSphericalPositions(projectile, radius, numberOfPoints,
            position -> {
                ParticleHandlers.sendParticles(
                level, bakedParticle, position.add(0,0.2,0), 0,
                    velocityA.x, velocityA.y, velocityA.z, 0.3
                );
            }
        );

        PositionGetters.getRandomSphericalPositions(projectile, radius, numberOfPoints * 5,
            position -> {
                ParticleHandlers.sendParticles(
                    level, particleOptions, position.add(0,0.2,0), 0,
                    velocityB.x, velocityB.y, velocityB.z, 0
                );
            }
        );
    }

    @Override
    public void discardCondition() {
        int getRandom = Random.nextInt(20, 50);
        if(this.elementProjectile.tickCount > 5) orbEnergyParticles(this.elementProjectile, Math.min(dischargeRadius / 4, 0.5), Math.max(dischargeRadius /6, 0.4));
        if(this.elementProjectile.tickCount >= getRandom) {
            this.dischargeEffect();
            this.elementProjectile.discard();
        }
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.LIGHTNING.get();
    }

    ResourceLocation abilityId = ModHelpers.res("boltz_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Boltz();
    }
}
