package org.jahdoo.ascension.ability.abilities.fireball;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.SharedFireProperties;
import org.jahdoo.ascension.ability.abilities.armageddon.ArmageddonModule;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.minecraft.sounds.SoundEvents.FIRE_AMBIENT;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.DamageUtils.damageWithJahdoo;
import static org.jahdoo.ascension.utils.PositionFinders.*;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.common.particle.ParticleStore.*;
import static org.jahdoo.common.registers.AttributeReg.INFERNO_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.EffectReg.INFERNO_EFFECT;

public class FireBall extends DefaultEntityBehaviour {

    private final List<UUID> hitTargets = new ArrayList<>();
    private boolean hasHitLocation;
    private double fireballTrail;
    private double maxRadius;
    private double aoe = 0.3;

    private double damage;
    private double effectChance;
    private double effectStrength;
    private double effectDuration;
    private double novaMaxSize;
    private boolean isBuddy;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        this.effectChance = this.getTag(EFFECT_CHANCE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.novaMaxSize = this.getTag(FireballAbility.NOVA_RANGE);
        this.isBuddy = this.getTag(ArmageddonModule.buddy) == 0.0;
        if(!(this.element.getOwner() instanceof Player)){
            this.damage = this.getTag(DAMAGE);
        } else {
            var player = this.element.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = Helpers.attributeModifierCalculator(
                    (LivingEntity) player,
                    (float) damage,
                    true,
                    MAGIC_DAMAGE_MULTIPLIER,
                    INFERNO_MAGIC_DAMAGE_MULTIPLIER
            );
        }
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.element.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return FireballAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        onHitBehaviour();
    }

    private Level level(){
        return this.element.level();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.inferno();
    }

    ResourceLocation abilityId = Helpers.res("fireball_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new FireBall();
    }

    private void fireballTrailingSound(){
        var projectile = this.element;

        if (projectile.tickCount % 7 == 0) {
            Helpers.getSoundWithPosition(projectile.level(), projectile.blockPosition(), FIRE_AMBIENT, 1, 1.5f);
        }
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        var chance = effectChance == 0 ? 100 : effectChance;

        if (Helpers.Random.nextInt(0, (int) chance) == 0) {
            var instance = new JahdooMobEffect(INFERNO_EFFECT.getDelegate(), (int) effectDuration, (int) effectStrength);
            hitEntity.addEffect(instance);
        }

        damageWithJahdoo(hitEntity, this.element.getOwner(), damage);
        onHitBehaviour();
    }

    private void setShockwaveNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.element.position()).normalize();
        var lifetime = 3;
        var col1 = -8487298;
        var col2 = -13355980;
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.1f, col1, col2, true);

        ParticleHandlers.sendParticles(
            level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 1.5
        );
    }

    private void novaDamageBehaviour(){
        this.element.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.element.getOwner(),
            this.element.getBoundingBox()
                .inflate(aoe,0, aoe)
                .deflate(0,1,0 )
        ).forEach(
            livingEntity -> {
                if(canDamageEntity(livingEntity, (LivingEntity) this.element.getOwner())){
                    if (!this.hitTargets.contains(livingEntity.getUUID())) {
                        damageWithJahdoo(livingEntity, this.element.getOwner(), this.damage);
                        this.hitTargets.add(livingEntity.getUUID());
                    }
                }
            }
        );
    }

    private void fireTrailVegetationBurn(){
        BlockPos entityPos = this.element.blockPosition();
        for (int x = (int) -aoe; x <= aoe; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = (int) -aoe; z <= aoe ; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= aoe) {
                        BlockPos blockPos = entityPos.offset(x, y, z);
                        BlockState blockState = this.element.level().getBlockState(blockPos);
                        SharedFireProperties.fireTrailVegetationRemover(blockState, blockPos, this.element);
                    }
                }
            }
        }
    }

    @Override
    public void onTickMethod() {
        if(!hasHitLocation){
            element.setShowTrailParticles(true);
            element.setAnimation(1);
        }

        if(fireballTrail < 0.6) fireballTrail += 0.1; else fireballTrail = 0;
        if(maxRadius < 0.6) maxRadius += 0.1;
        if(aoe >= novaMaxSize) this.element.discard();
        if(hasHitLocation || this.element.tickCount > 50){
            if(this.element.tickCount == 51 && !hasHitLocation) onHitBehaviour();

            if(aoe < novaMaxSize){
                if(aoe < 2) aoe *= 1.4; else aoe += 0.45;
                this.fireTrailVegetationBurn();
                novaDamageBehaviour();
            }

        } else {
            fireball();
            fireballTrailingSound();
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putBoolean("hasHitLocation", hasHitLocation);
        compoundTag.putDouble("aoe", aoe);
        compoundTag.putDouble("fireballTrail", fireballTrail);
        compoundTag.putDouble("maxRadius", maxRadius);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(FireballAbility.NOVA_RANGE, this.novaMaxSize);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.hasHitLocation = compoundTag.getBoolean("hasHitLocation");
        this.aoe = compoundTag.getDouble("aoe");
        this.fireballTrail = compoundTag.getDouble("fireballTrail");
        this.maxRadius = compoundTag.getDouble("maxRadius");
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
        this.novaMaxSize = compoundTag.getDouble(FireballAbility.NOVA_RANGE);
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 0.3f);
        var directions = positionScrambler.subtract(this.element.position()).normalize();
        var lifetime = (int) this.novaMaxSize;
        var size = Helpers.Random.nextDouble(0.2, 0.6);
        var bakedParticle = bakedParticleOptions(this.getElementType().id(), lifetime, (float) size, true);
        var col1 = this.getElementType().partColourA();
        var col2 =  color(51, 51, 51);
        var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, lifetime, (float) (size - 0.2), col1, col2, true);
        var getRandomParticle = List.of(bakedParticle, genericParticle);
        var randomSpeed = Helpers.Random.nextDouble(this.novaMaxSize/12, this.novaMaxSize/8);
        var randomType = getRandomParticle.get(Helpers.Random.nextInt(2));

        ParticleHandlers.sendParticles(
            level(), randomType, worldPosition, 0, directions.x, directions.y+0.05, directions.z, randomSpeed
        );
    }

    private void onHitBehaviour() {
        float speed = (float) (this.novaMaxSize/10);
        getOuterRingOfRadiusRandom(this.element.position(), 1.5, this.novaMaxSize * 30, this::setParticleNova);
        if(!this.isBuddy){
            getOuterRingOfRadius(this.element.position(), 0.5, 300, this::setShockwaveNova);
        }
        var maxPart = Math.max((int) this.maxRadius / 2, 1);

        if(this.element.level() instanceof ServerLevel serverLevel){
            ParticleHandlers.particleBurst(serverLevel, this.element.position(), maxPart,
                genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, this.getElementType(), 40, 3f)
                ,0,0,0,speed
            );

            ParticleHandlers.particleBurst(serverLevel, this.element.position(), maxPart,
                genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, 40, 3f, rgbToInt(61,61,61), rgbToInt(218,218,218))
                ,0,0,0,speed
            );
        }

        Helpers.getSoundWithPosition(this.element.level(), this.element.blockPosition(), SoundReg.EXPLOSION.get(),2f);
        Helpers.getSoundWithPosition(this.element.level(), this.element.blockPosition(), FIRE_AMBIENT);

        this.element.setDeltaMovement(0,0,0);
        this.hasHitLocation = true;
        element.setShowTrailParticles(false);
        element.setAnimation(2);
    }

    private void fireball(){
        var getPositions = Helpers.getRandomParticleVelocity(this.element, 0.1);
        var getPositions2 = Helpers.getRandomParticleVelocity(this.element, 0.05);

        getRandomSphericalPositions(this.element, maxRadius, 16,
            position -> {
                var newPosition = position.add(this.element.getDeltaMovement().scale(-1.5));
                var size = Helpers.Random.nextFloat(2.5f, 3.5f);
                var pType = bakedParticleOptions(this.getElementType().id(), 2, size, false);
                ParticleHandlers.sendParticles(
                    level(),
                    pType,
                    newPosition.add(0,0.2,0),
                    0, getPositions2.x,getPositions2.y,getPositions2.z,0);
            }
        );

        getSphericalPositions(this.element, fireballTrail, 18,
            position -> {
                var newPosition = position.add(this.element.getDeltaMovement().scale(-1.5));
                var size = Helpers.Random.nextFloat(2f, 3f);
                var pType = genericParticleOptions(GENERIC_PARTICLE_SELECTION, 4, size, getElementType().partColourA(), color(51, 51, 51));
                ParticleHandlers.sendParticles(
                    level(),
                    pType,
                    newPosition.add(0,0.2,0),
                    1, getPositions.x,getPositions.y,getPositions.z,aoe >= novaMaxSize ? 0.5 : 0.01);
            }
        );
    }

}
