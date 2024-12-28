package org.jahdoo.ability.abilities.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.QuantumDestroyerAbility;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.entities.EntityMovers;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.utils.ModHelpers.Random;


public class QuantumDestroyer extends DefaultEntityBehaviour {
    private double counter = 1;
    private int privateTicks;
    private boolean isFullForm;

    double radius;
    double damage;
    double lifetime;
    double gravitationalPull;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        this.radius = this.getTag(QuantumDestroyerAbility.radius);
        this.gravitationalPull = this.getTag(GRAVITATIONAL_PULL);
        this.lifetime = this.getTag(LIFETIME);
        if(this.elementProjectile.getOwner() != null){
            var player = this.elementProjectile.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = ModHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                MYSTIC_MAGIC_DAMAGE_MULTIPLIER
            );
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("counter", this.counter);
        compoundTag.putInt("private_ticks", this.privateTicks);
        compoundTag.putBoolean("full_form", this.isFullForm);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(LIFETIME, this.lifetime);
        compoundTag.putDouble(QuantumDestroyerAbility.radius, this.radius);
        compoundTag.putDouble(GRAVITATIONAL_PULL, this.gravitationalPull);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.counter = compoundTag.getDouble("counter");
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.isFullForm = compoundTag.getBoolean("full_form");
        this.damage = compoundTag.getDouble(DAMAGE);
        this.lifetime = compoundTag.getDouble(LIFETIME);
        this.radius = compoundTag.getDouble(QuantumDestroyerAbility.radius);
        this.gravitationalPull = compoundTag.getDouble(GRAVITATIONAL_PULL);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.elementProjectile.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return QuantumDestroyerAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.elementProjectile.discard();
    }

    @Override
    public void onTickMethod() {
        privateTicks++;
        if (privateTicks < 20) {
            elementProjectile.setShowTrailParticles(true);
            if(privateTicks == 1) this.entitySpawnParticles(elementProjectile.level());
            if(this.elementProjectile.tickCount % 2 == 0) {
                PositionGetters.getOuterRingOfRadius(this.elementProjectile.position(), 0.05, 100, this::pullParticlesIn);
                ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.TIMER.get(), 1.5f, 1.5f);
                this.entitySpawnParticles(elementProjectile.level());
            }
            this.elementProjectile.setDeltaMovement(0, 0.5, 0);

        } else {
            if(!isFullForm){
                isFullForm = true;
                ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.ORB_CREATE.get(), 1.5f, 0.8f);
                ParticleHandlers.particleBurst(
                    elementProjectile.level(), this.elementProjectile.position(), 20,
                    genericParticleOptions(MAGIC_PARTICLE_SELECTION, this.getElementType(), 15,4),
                    0,0,0,1f
                );
                this.elementProjectile.setDeltaMovement(0, 0, 0);
            }

            if(privateTicks <= lifetime){
                if(counter < radius) counter *= 1.6;
                elementProjectile.setAnimation(4);
                playAmbientSound();
                gravityEffect();
            }
            if(this.elementProjectile.tickCount % 2 == 0){
                damageCalculator();
                PositionGetters.getRandomSphericalPositions(this.elementProjectile.position(), radius / 2 - 0.5, 25, this::pushParticlesOut);
            }
            particle();
        }
    }

    private void pushParticlesOut(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.elementProjectile.position()).normalize();
        var lifetime = 6;
        var col1 = this.getElementType().particleColourPrimary();
        var col2 = this.getElementType().particleColourFaded();
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 3f, col1, col2, false);

        ParticleHandlers.sendParticles(
            this.elementProjectile.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.6
        );
    }

    private void pullParticlesIn(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.elementProjectile.position()).normalize();
        var lifetime = 2;
        var col1 = this.getElementType().particleColourPrimary();
        var col2 = this.getElementType().particleColourFaded();
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.2f, col1, col2, true);

        ParticleHandlers.sendParticles(
            this.elementProjectile.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.6
        );
    }

    @Override
    public void discardCondition() {
        if (privateTicks > lifetime) {
            if (this.elementProjectile.tickCount == lifetime + 1) {
                elementProjectile.setAnimation(5);
                ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundEvents.ENDER_EYE_DEATH, 2.5F, 0.8F);
            }

            if(privateTicks > lifetime + 6) this.elementProjectile.discard();
        }
    }

    private void entitySpawnParticles(Level level){
        var particleCount = 2;
        var speed = 0.05f;

        ParticleHandlers.particleBurst(
            level, this.elementProjectile.position(), particleCount,
            bakedParticleOptions(this.getElementType().getTypeId(), 4,2,false),
            0,0,0,speed
        );
        ParticleHandlers.particleBurst(
            level, this.elementProjectile.position(), particleCount,
            genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 4,2),
            0,0,0,speed
        );
    }

    private void playAmbientSound(){
        if (privateTicks == 21) ambientSound();
        if(privateTicks < this.lifetime - 30){
            if(this.elementProjectile.tickCount % 40 == 0) ambientSound();
        }
        if(Random.nextInt(0, 20) == 0){
            ModHelpers.getSoundWithPosition(
                this.elementProjectile.level(), this.elementProjectile.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 0.1f
            );
        }
    }

    private void ambientSound() {
        ModHelpers.getSoundWithPosition(
            this.elementProjectile.level(), this.elementProjectile.blockPosition(),
            SoundEvents.ELDER_GUARDIAN_AMBIENT, 1.5f, 0.6f
        );
    }

    private void particle(){
        var level = this.elementProjectile.level();
        var explode = privateTicks > lifetime;
        var rando = List.of(
            genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 5, Random.nextInt(6,8)),
            bakedParticleOptions(this.getElementType().getTypeId(), 5, Random.nextInt(5,8), false)
        );

        PositionGetters.getRandomSphericalPositions(this.elementProjectile, counter, Math.min(radius * 6, 20),
            position -> {
                var directions = this.elementProjectile.position().subtract(position).normalize();
                ParticleHandlers.sendParticles(
                    level,
                    rando.get(Random.nextInt(2)),
                    position, Random.nextInt(0,2),
                    directions.x, directions.y, directions.z, explode ? 1.2 : Random.nextDouble(0.4, 0.6)
                );
            }
        );
    }

    private void gravityEffect(){
        var nearbyEntities = this.elementProjectile.level().getEntitiesOfClass(
            LivingEntity.class,
            this.elementProjectile.getBoundingBox().inflate(radius * 3),
            entity -> true
        );

        for (LivingEntity entities : nearbyEntities) {
            if(isImmune(entities)){
                var knockBackRes = entities.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                var resistance = knockBackRes != null ? knockBackRes.getValue() : 0;
                var velocity = (gravitationalPull) - resistance;
                EntityMovers.entityMover(this.elementProjectile, entities, velocity);
            }
        }
    }

    private boolean isImmune(LivingEntity entities) {
        return DefaultEntityBehaviour.canDamageEntity(entities, (LivingEntity) this.elementProjectile.getOwner());
    }

    private void damageCalculator(){
        if(this.elementProjectile.getOwner() == null) return;
        this.elementProjectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.elementProjectile.getOwner(),
            this.elementProjectile.getBoundingBox().inflate(0.8)
        ).forEach(
            livingEntity -> {
                if (this.isImmune(livingEntity)) {
                    DamageUtil.damageWithJahdoo(livingEntity, this.elementProjectile.getOwner(), (float) this.damage);
                }
            }
        );
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.MYSTIC.get();
    }

    ResourceLocation abilityId = ModHelpers.res("quantum_destroyer_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new QuantumDestroyer();
    }
}
