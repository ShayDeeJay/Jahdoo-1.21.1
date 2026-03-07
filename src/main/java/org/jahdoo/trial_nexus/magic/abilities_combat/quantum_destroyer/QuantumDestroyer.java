package org.jahdoo.trial_nexus.magic.abilities_combat.quantum_destroyer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.EntityMovers;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.magic.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.List;

import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.*;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.trial_nexus.magic.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;


public class QuantumDestroyer extends DefaultEntityBehaviour {

    private double counter;
    private int privateTicks;
    private boolean isFullForm;

    private double radius;
    private double damage;
    private double implosions;
    private double gravitationalPull;
    public static final ResourceLocation abilityId = JahdooHelpers.res("quantum_destroyer_property");

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        this.radius = this.getTag(QuantumDestroyerAbility.ENERGY_RADIUS);
        this.gravitationalPull = this.getTag(GRAVITATIONAL_PULL);
        this.implosions = this.getTag(IMPLOSIONS);
        if(this.element.getOwner() != null){
            var player = this.element.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = JahdooHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                MYSTIC_MAGIC_DAMAGE_MULTIPLIER
            );
        }
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.element.getAbilityHolder();
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

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new QuantumDestroyer();
    }

    public void sharedSound(SoundEvent sEvent, Float volume, Float pitch){
        SoundHelpers.getSoundWithPosition( this.element.level(), this.element.position(), sEvent, SoundSource.NEUTRAL, volume, pitch);
    }

    private void novaShared(double radius1, int points, int lifetime1, double speed, float size) {
        PositionFinders.getOuterRingOfRadius(this.element.position(), radius1, points, (pos) -> pullParticlesIn(pos, lifetime1, speed, size));
    }

    @Override
    public void discardCondition() {
        if(counter == implosions){
            element.setAnimation(5);
            sharedSound(getElementType().sound(), 2.5F, 0.6F);
            this.element.discard();
        }
    }

    private void pullParticlesIn(Vec3 worldPosition, int lifetime, double speed, float size) {
        var directions = worldPosition.subtract(this.element.position()).normalize();
        var col1 = this.getElementType().partColourA();
        var col2 = this.getElementType().partColourFade();
        var genericParticle = genericParticle(SOFT_PARTICLE, lifetime, size, col1, col2, false);

        sendParticles(this.element.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, speed);
    }

    private void onPulse(){
        if(this.element.tickCount % 30 == 0) {
            particleBurst(
                element.level(), this.element.position(), 10,
                genericParticle(MAGIC_PARTICLE, this.getElementType(), 5, 5),
                0,0,0,1f
            );
            counter++;
            novaShared(0.1, 100, Random.nextInt(30, 50), radius/4, 5F);
            damageCalculator();
            sharedSound(SoundReg.QUANTUM.get(), 2f, Random.nextFloat(0.6F, 1.2F));
            sharedSound(SoundReg.LEVITATE.get(), 3f, 1.2F);
        }

        if(Random.nextInt(0, 20) == 0){
            sharedSound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, Random.nextFloat(0.5f, 0.7F));
        }
    }

    private void damageCalculator(){
        if(this.element.getOwner() == null) return;
        this.element.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.element.getOwner(),
            this.element.getBoundingBox().inflate(0.8).inflate(radius, 0, radius)
        ).forEach(
            livingEntity -> {
                if (this.isImmune(livingEntity)) {
                    DamageUtils.damageWithJahdoo(livingEntity, this.element.getOwner(), (float) this.damage, getElementType().damageTypeResourceKey());
                    if(!livingEntity.isAlive()) sharedSound(SoundReg.MYSTIC_ABILITY.get(), 1f, 1.5F);
                }
            }
        );
    }

    private void entitySpawnParticles(Level level){
        var particleCount = 2;
        var speed = 0.05f;

        particleBurst(
            level, this.element.position(), particleCount,
            bakedParticle(this.getElementType().id(), 4,2,false),
            0,0,0,speed
        );
        particleBurst(
            level, this.element.position(), particleCount,
            genericParticle(GENERIC_PARTICLE, this.getElementType(), 4,2),
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
        var rando = List.of(
            genericParticle(GENERIC_PARTICLE, this.getElementType(), 5, Random.nextInt(6,8)),
            bakedParticle(this.getElementType().id(), 15, Random.nextInt(5,8), false)
        );

        PositionFinders.getRandomSphericalPositions(this.element, radius + 1, Math.min(radius * 6, 20),
            position -> {
                var directions = this.element.position().subtract(position).normalize();
                sendParticles(
                    level, rando.get(Random.nextInt(2)), position, Random.nextInt(0,2),
                    directions.x, directions.y, directions.z, Random.nextDouble(0.2, 0.4)
                );
            }
        );
    }

    @Override
    public void onTickMethod() {
        privateTicks++;

        if(privateTicks == 14){
            isFullForm = true;
            sharedSound(getElementType().sound(), 2f, 1f);
            particleBurst(
                element.level(), this.element.position(), 20,
                genericParticle(MAGIC_PARTICLE, this.getElementType(), 15,4),
                0,0,0,1f
            );
        }

        if(isFullForm) {
            this.element.setDeltaMovement(0, 0, 0);
            element.setAnimation(4);
            gravityEffect();
            onPulse();

            if(privateTicks > 20) particle();
        }

        if (!isFullForm) {
            element.setShowTrailParticles(true);
            var size = 2.5F;
            var speed = 0.6;
            var lifetime1 = Random.nextInt(2, 6);
            var lifetime2 = Random.nextInt(10, 20);
            var points = 50;
            var radius1 = 0.05;

            if(privateTicks == 1) {
                shared(radius1, points, lifetime1, speed, size);
                this.entitySpawnParticles(element.level());
                sharedSound(SoundReg.MYSTIC_ABILITY.get(), 2f, 0.4f);
            }

            if(privateTicks % 6 == 0) {
                shared(radius1, points, lifetime2, speed, size);
                sharedSound(SoundReg.THUD_B.get(), 1.5f, 1f);
                sharedSound(SoundReg.QUANTUM.get(), 1f, 2f);
            }

            this.element.setDeltaMovement(0, speed, 0);
        }
    }

    private void shared(double radius1, int points, int lifetime2, double speed, float size) {
        novaShared(radius1, points, lifetime2, speed, size);
        this.entitySpawnParticles(element.level());
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("counter", this.counter);
        compoundTag.putInt("private_ticks", this.privateTicks);
        compoundTag.putBoolean("full_form", this.isFullForm);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(LIFETIME, this.implosions);
        compoundTag.putDouble(QuantumDestroyerAbility.ENERGY_RADIUS, this.radius);
        compoundTag.putDouble(GRAVITATIONAL_PULL, this.gravitationalPull);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.counter = compoundTag.getDouble("counter");
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.isFullForm = compoundTag.getBoolean("full_form");
        this.damage = compoundTag.getDouble(DAMAGE);
        this.implosions = compoundTag.getDouble(LIFETIME);
        this.radius = compoundTag.getDouble(QuantumDestroyerAbility.ENERGY_RADIUS);
        this.gravitationalPull = compoundTag.getDouble(GRAVITATIONAL_PULL);
    }

}
