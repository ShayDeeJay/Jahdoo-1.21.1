package org.jahdoo.ability.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.all_abilities.abilities.BoltzAbility;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.registers.DamageTypeRegistry.JAHDOO_SOURCE;

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
                AttributesRegister.MAGIC_DAMAGE_MULTIPLIER,
                true
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
        this.dischargeEffect(this.elementProjectile, (LivingEntity) this.elementProjectile.getOwner(), (int) effectChance, (int) effectDuration, (int) effectStrength, (int) dischargeRadius);
        ModHelpers.getSoundWithPosition(this.elementProjectile.level(), blockHitResult.getBlockPos(), SoundRegister.EXPLOSION.get(),0.2f,2f);
        this.elementProjectile.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        LivingEntity owner = (LivingEntity) this.elementProjectile.getOwner();
        hitEntity.hurt(DamageUtil.source(this.elementProjectile.level(), JAHDOO_SOURCE, hitEntity, owner), (float) this.damage);
        ModHelpers.getSoundWithPosition(this.elementProjectile.level(),  this.elementProjectile.blockPosition(), SoundRegister.BOLT.get(), 0.1f);
    }

    private void dischargeEffect(Projectile projectile, LivingEntity owner, int chance, int duration, int amplifier, int dischargeRadius){
        if(projectile.level() instanceof  ServerLevel serverLevel){
            projectile.level().getNearbyEntities(
                LivingEntity.class, TargetingConditions.DEFAULT, owner,
                projectile.getBoundingBox().inflate(dischargeRadius)
            ).forEach(
                entities -> {
                    if(ModHelpers.Random.nextInt(0, 10) == 0) {
                        entities.addEffect(new CustomMobEffect(EffectsRegister.LIGHTNING_EFFECT.getDelegate(), duration, amplifier));
                    }
//                    if(GeneralHelpers.Random.nextInt(0, chance) == 0) {
//                        entities.addEffect(new CustomMobEffect(EffectsRegister.LIGHTNING_EFFECT.getDelegate(), duration, amplifier));
//                    }
                }
            );
            ParticleHandlers.particleBurst(serverLevel, projectile.position(), 1, this.getElementType().getParticleGroup().magicSlow(),0,0,0, (float) dischargeRadius/15);
        }
    }

    @Override
    public void onTickMethod() {
        this.elementProjectile.setAnimation(9);
        applyInertia( this.elementProjectile);
    }

    public void applyInertia(Projectile projectile) {
        double inertiaFactor = ModHelpers.Random.nextDouble(0.85, 0.90); // Adjust this value to control the rate of slowdown (0.98 means 2% reduction per tick)
        Vec3 currentVelocity = projectile.getDeltaMovement();

        double newVelocityX = currentVelocity.x * inertiaFactor;
        double newVelocityY = currentVelocity.y * inertiaFactor;
        double newVelocityZ = currentVelocity.z * inertiaFactor;

        projectile.setDeltaMovement(newVelocityX, newVelocityY, newVelocityZ);
    }

    void orbEnergyParticles(Projectile projectile, double numberOfPoints, double radius){
        var level = projectile.level();

        GenericParticleOptions particleOptions = genericParticleOptions(
            ParticleStore.ELECTRIC_PARTICLE_SELECTION,
            this.getElementType(),
            ModHelpers.Random.nextInt(2,8),
            1f, 0.3
        );

        Vec3 velocityA = ModHelpers.getRandomParticleVelocity(projectile, 0.1);
        Vec3 velocityB = ModHelpers.getRandomParticleVelocity(projectile, 0.05);

        PositionGetters.getRandomSphericalPositions(projectile, radius, numberOfPoints,
            position -> {
                ParticleHandlers.sendParticles(
                level, this.getElementType().getParticleGroup().bakedSlow(), position.add(0,0.2,0), 0,
                    velocityA.x, velocityA.y, velocityA.z, 0.05
                );
            }
        );

        PositionGetters.getRandomSphericalPositions(projectile, radius, numberOfPoints * 10,
            position -> {
                ParticleHandlers.sendParticles(
                    level, particleOptions, position.add(0,0.2,0), 1,
                    velocityB.x, velocityB.y, velocityB.z, 0.05
                );
            }
        );
    }

    @Override
    public void discardCondition() {
        LivingEntity owner = (LivingEntity) this.elementProjectile.getOwner();

        int getRandom = ModHelpers.Random.nextInt(20, 30);

        if(this.elementProjectile.tickCount > 10) orbEnergyParticles(this.elementProjectile, dischargeRadius / 4, Math.max(dischargeRadius /6, 0.4));
        if(this.elementProjectile.tickCount >= getRandom) {
            damageCalculator();
            this.dischargeEffect(this.elementProjectile, owner, (int) effectChance, (int) effectStrength, (int) effectDuration, (int) dischargeRadius);
            ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.BOLT.get(),0.4f,1.5f);
            this.elementProjectile.discard();
        }
    }

    private void damageCalculator(){

        this.elementProjectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.elementProjectile.getOwner(),
            this.elementProjectile.getBoundingBox().inflate(dischargeRadius)
        ).forEach(
            livingEntity -> {
                if (!(livingEntity instanceof Player)) {
                    livingEntity.hurt(
                        DamageUtil.source(this.elementProjectile.level(), JAHDOO_SOURCE, livingEntity, this.elementProjectile.getOwner()),
                        (float) this.damage
                    );
                }
            }
        );
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
