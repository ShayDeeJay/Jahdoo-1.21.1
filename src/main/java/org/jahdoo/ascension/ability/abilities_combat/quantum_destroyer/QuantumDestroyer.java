package org.jahdoo.ascension.ability.abilities_combat.quantum_destroyer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.EntityMovers;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.List;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;


public class QuantumDestroyer extends DefaultEntityBehaviour {

    private double counter = 1;
    private int privateTicks;
    private boolean isFullForm;

    private double radius;
    private double damage;
    private double lifetime;
    private double gravitationalPull;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        this.radius = this.getTag(QuantumDestroyerAbility.ENERGY_RADIUS);
        this.gravitationalPull = this.getTag(GRAVITATIONAL_PULL);
        this.lifetime = this.getTag(LIFETIME);
        if(this.element.getOwner() != null){
            var player = this.element.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = Helpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                MYSTIC_MAGIC_DAMAGE_MULTIPLIER
            );
        }
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.element.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return QuantumDestroyerAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.element.discard();
    }

    private boolean isImmune(LivingEntity entities) {
        return DefaultEntityBehaviour.canDamageEntity(entities, (LivingEntity) this.element.getOwner());
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.mystic();
    }

    ResourceLocation abilityId = Helpers.res("quantum_destroyer_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new QuantumDestroyer();
    }

    private void ambientSound() {
        Helpers.getSoundWithPosition(
            this.element.level(), this.element.blockPosition(),
            SoundEvents.ELDER_GUARDIAN_AMBIENT, 1.5f, 0.6f
        );
    }

    @Override
    public void discardCondition() {
        if (privateTicks > lifetime) {
            if (this.element.tickCount == lifetime + 1) {
                element.setAnimation(5);
                Helpers.getSoundWithPosition(this.element.level(), this.element.getOnPos(), SoundEvents.ENDER_EYE_DEATH, 2.5F, 0.8F);
            }

            if(privateTicks > lifetime + 6) this.element.discard();
        }
    }

    private void pullParticlesIn(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.element.position()).normalize();
        var lifetime = 2;
        var col1 = this.getElementType().partColourA();
        var col2 = this.getElementType().partColourFade();
        var genericParticle = ParticleHandlers.genericParticle(SOFT_PARTICLE, lifetime, 0.2f, col1, col2, true);

        ParticleHandlers.sendParticles(
            this.element.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.6
        );
    }

    private void pushParticlesOut(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.element.position()).normalize();
        var lifetime = 6;
        var col1 = this.getElementType().partColourA();
        var col2 = this.getElementType().partColourFade();
        var genericParticle = ParticleHandlers.genericParticle(SOFT_PARTICLE, lifetime, 3f, col1, col2, false);

        ParticleHandlers.sendParticles(
            this.element.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.6
        );
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("counter", this.counter);
        compoundTag.putInt("private_ticks", this.privateTicks);
        compoundTag.putBoolean("full_form", this.isFullForm);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(LIFETIME, this.lifetime);
        compoundTag.putDouble(QuantumDestroyerAbility.ENERGY_RADIUS, this.radius);
        compoundTag.putDouble(GRAVITATIONAL_PULL, this.gravitationalPull);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.counter = compoundTag.getDouble("counter");
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.isFullForm = compoundTag.getBoolean("full_form");
        this.damage = compoundTag.getDouble(DAMAGE);
        this.lifetime = compoundTag.getDouble(LIFETIME);
        this.radius = compoundTag.getDouble(QuantumDestroyerAbility.ENERGY_RADIUS);
        this.gravitationalPull = compoundTag.getDouble(GRAVITATIONAL_PULL);
    }

    private void playAmbientSound(){
        if (privateTicks == 21) ambientSound();
        if(privateTicks < this.lifetime - 30){
            if(this.element.tickCount % 40 == 0) ambientSound();
        }
        if(Random.nextInt(0, 20) == 0){
            Helpers.getSoundWithPosition(
                this.element.level(), this.element.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 0.1f
            );
        }
    }

    private void damageCalculator(){
        if(this.element.getOwner() == null) return;
        this.element.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.element.getOwner(),
            this.element.getBoundingBox().inflate(0.8)
        ).forEach(
            livingEntity -> {
                if (this.isImmune(livingEntity)) {
                    DamageUtils.damageWithJahdoo(livingEntity, this.element.getOwner(), (float) this.damage);
                }
            }
        );
    }

    private void entitySpawnParticles(Level level){
        var particleCount = 2;
        var speed = 0.05f;

        ParticleHandlers.particleBurst(
            level, this.element.position(), particleCount,
            bakedParticle(this.getElementType().id(), 4,2,false),
            0,0,0,speed
        );
        ParticleHandlers.particleBurst(
            level, this.element.position(), particleCount,
            ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE, this.getElementType(), 4,2),
            0,0,0,speed
        );
    }

    private void gravityEffect(){
        var nearbyEntities = this.element.level().getEntitiesOfClass(
            LivingEntity.class,
            this.element.getBoundingBox().inflate(radius * 3),
            entity -> true
        );

        for (LivingEntity entities : nearbyEntities) {
            if(isImmune(entities)){
                var knockBackRes = entities.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                var resistance = knockBackRes != null ? knockBackRes.getValue() : 0;
                var velocity = (gravitationalPull) - resistance;
                EntityMovers.entityMoverCenter(this.element, entities, velocity);
            }
        }
    }

    private void particle(){
        var level = this.element.level();
        var explode = privateTicks > lifetime;
        var rando = List.of(
            ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE, this.getElementType(), 5, Random.nextInt(6,8)),
            bakedParticle(this.getElementType().id(), 5, Random.nextInt(5,8), false)
        );

        PositionFinders.getRandomSphericalPositions(this.element, counter, Math.min(radius * 6, 20),
            position -> {
                var directions = this.element.position().subtract(position).normalize();
                ParticleHandlers.sendParticles(
                    level,
                    rando.get(Random.nextInt(2)),
                    position, Random.nextInt(0,2),
                    directions.x, directions.y, directions.z, explode ? 1.2 : Random.nextDouble(0.4, 0.6)
                );
            }
        );
    }

    @Override
    public void onTickMethod() {
        privateTicks++;
        if (privateTicks < 20) {
            element.setShowTrailParticles(true);
            if(privateTicks == 1) this.entitySpawnParticles(element.level());
            if(this.element.tickCount % 2 == 0) {
                PositionFinders.getOuterRingOfRadius(this.element.position(), 0.05, 100, this::pullParticlesIn);
                Helpers.getSoundWithPosition(this.element.level(), this.element.blockPosition(), SoundReg.TIMER.get(), 1.5f, 1.5f);
                this.entitySpawnParticles(element.level());
            }
            this.element.setDeltaMovement(0, 0.5, 0);

        } else {
            if(!isFullForm){
                isFullForm = true;
                Helpers.getSoundWithPosition(this.element.level(), this.element.blockPosition(), SoundReg.ORB_CREATE.get(), 1.5f, 0.8f);
                ParticleHandlers.particleBurst(
                    element.level(), this.element.position(), 20,
                    ParticleHandlers.genericParticle(MAGIC_PARTICLE, this.getElementType(), 15,4),
                    0,0,0,1f
                );
                this.element.setDeltaMovement(0, 0, 0);
            }

            if(privateTicks <= lifetime){
                if(counter < radius) counter *= 1.6;
                element.setAnimation(4);
                playAmbientSound();
                gravityEffect();
            }
            if(this.element.tickCount % 2 == 0){
                damageCalculator();
                PositionFinders.getRandomSphericalPositions(this.element.position(), radius / 2 - 0.5, 25, this::pushParticlesOut);
            }
            particle();
        }
    }
}
