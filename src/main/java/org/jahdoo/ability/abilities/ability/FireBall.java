package org.jahdoo.ability.abilities.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.SharedFireProperties;
import org.jahdoo.ability.abilities.ability_data.FireballAbility;
import org.jahdoo.ability.ability_components.ArmageddonModule;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.*;
import static org.jahdoo.registers.AttributesRegister.INFERNO_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;

public class FireBall extends DefaultEntityBehaviour {
    private boolean hasHitLocation;
    private double aoe = 0.3;
    private double fireballTrail;
    private double maxRadius;
    public List<UUID> hitTargets = new ArrayList<>();

    double damage;
    double effectChance;
    double effectStrength;
    double effectDuration;
    double novaMaxSize;
    boolean isBuddy;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        this.effectChance = this.getTag(EFFECT_CHANCE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.novaMaxSize = this.getTag(FireballAbility.novaRange);
        this.isBuddy = this.getTag(ArmageddonModule.buddy) == 0.0;
        if(!(this.elementProjectile.getOwner() instanceof Player)){
            this.damage = this.getTag(DAMAGE);
        } else {
            var player = this.elementProjectile.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = ModHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                INFERNO_MAGIC_DAMAGE_MULTIPLIER
            );
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
        compoundTag.putDouble(FireballAbility.novaRange, this.novaMaxSize);
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
        this.novaMaxSize = compoundTag.getDouble(FireballAbility.novaRange);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.elementProjectile.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return FireballAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        onHitBehaviour();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        var chance = effectChance == 0 ? 100 : effectChance;
        if (ModHelpers.Random.nextInt(0, (int) chance) == 0) {
            hitEntity.addEffect(new CustomMobEffect(EffectsRegister.INFERNO_EFFECT.getDelegate(), (int) effectDuration, (int) effectStrength));
        }
        DamageUtil.damageWithJahdoo(hitEntity, this.elementProjectile.getOwner(), damage);
        onHitBehaviour();
    }

    @Override
    public void onTickMethod() {
        if(!hasHitLocation){
            elementProjectile.setShowTrailParticles(true);
            elementProjectile.setAnimation(1);
        }

        if(fireballTrail < 0.6) fireballTrail += 0.1; else fireballTrail = 0;
        if(maxRadius < 0.6) maxRadius += 0.1;
        if(aoe >= novaMaxSize) this.elementProjectile.discard();
        if(hasHitLocation || this.elementProjectile.tickCount > 50){
            if(this.elementProjectile.tickCount == 51 && !hasHitLocation) onHitBehaviour();

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

    private void fireballTrailingSound(){
        if (this.elementProjectile.tickCount % 7 == 0) {
            ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundEvents.FIRE_AMBIENT, 1, 1.5f);
        }
    }

    private void setShockwaveNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.elementProjectile.position()).normalize();
        var lifetime = 3;
        var col1 = -8487298;
        var col2 = -13355980;
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.1f, col1, col2, true);

        ParticleHandlers.sendParticles(
            level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 1.5
        );
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 0.3f);
        var directions = positionScrambler.subtract(this.elementProjectile.position()).normalize();
        var lifetime = (int) this.novaMaxSize;
        var size = ModHelpers.Random.nextDouble(0.2, 0.6);
        var bakedParticle = bakedParticleOptions(this.getElementType().getTypeId(), lifetime, (float) size, true);
        var col1 = this.getElementType().particleColourPrimary();
        var col2 =  color(51, 51, 51);
        var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, lifetime, (float) (size - 0.2), col1, col2, true);
        var getRandomParticle = List.of(bakedParticle, genericParticle);
        var randomSpeed = ModHelpers.Random.nextDouble(this.novaMaxSize/12, this.novaMaxSize/8);
        var randomType = getRandomParticle.get(ModHelpers.Random.nextInt(2));

        ParticleHandlers.sendParticles(
            level(), randomType, worldPosition, 0, directions.x, directions.y+0.05, directions.z, randomSpeed
        );
    }

    private void novaDamageBehaviour(){
        this.elementProjectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.elementProjectile.getOwner(),
            this.elementProjectile.getBoundingBox()
                .inflate(aoe,0, aoe)
                .deflate(0,1,0 )
        ).forEach(
            livingEntity -> {
                if(canDamageEntity(livingEntity, (LivingEntity) this.elementProjectile.getOwner())){
                    if (!this.hitTargets.contains(livingEntity.getUUID())) {
                        DamageUtil.damageWithJahdoo(livingEntity, this.elementProjectile.getOwner(), this.damage);
                        this.hitTargets.add(livingEntity.getUUID());
                    }
                }
            }
        );
    }

    private void fireTrailVegetationBurn(){
        BlockPos entityPos = this.elementProjectile.blockPosition();
        for (int x = (int) -aoe; x <= aoe; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = (int) -aoe; z <= aoe ; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= aoe) {
                        BlockPos blockPos = entityPos.offset(x, y, z);
                        BlockState blockState = this.elementProjectile.level().getBlockState(blockPos);
                        SharedFireProperties.fireTrailVegetationRemover(blockState, blockPos, this.elementProjectile);
                    }
                }
            }
        }
    }

    private void fireball(){
        var getPositions = ModHelpers.getRandomParticleVelocity(this.elementProjectile, 0.1);
        var getPositions2 = ModHelpers.getRandomParticleVelocity(this.elementProjectile, 0.05);
        PositionGetters.getRandomSphericalPositions(this.elementProjectile, maxRadius, 16,
            position -> {
                var newPosition = position.add(this.elementProjectile.getDeltaMovement().scale(-1.5));
                var size = ModHelpers.Random.nextFloat(2.5f, 3.5f);
                var pType = bakedParticleOptions(this.getElementType().getTypeId(), 2, size, false);
                ParticleHandlers.sendParticles(
                    level(),
                    pType,
                    newPosition.add(0,0.2,0),
                    0, getPositions2.x,getPositions2.y,getPositions2.z,0);
            }
        );
        PositionGetters.getSphericalPositions(this.elementProjectile, fireballTrail, 18,
            position -> {
                var newPosition = position.add(this.elementProjectile.getDeltaMovement().scale(-1.5));
                var size = ModHelpers.Random.nextFloat(2f, 3f);
                var pType = genericParticleOptions(GENERIC_PARTICLE_SELECTION, 4, size, getElementType().particleColourPrimary(), color(51, 51, 51));

//                var pType = genericParticleOptions(GENERIC_PARTICLE_SELECTION, this.getElementType(), 4, size);
                ParticleHandlers.sendParticles(
                    level(),
                    pType,
                    newPosition.add(0,0.2,0),
                    1, getPositions.x,getPositions.y,getPositions.z,aoe >= novaMaxSize ? 0.5 : 0.01);
            }
        );
    }

    private void onHitBehaviour() {
        float speed = (float) (this.novaMaxSize/10);
        PositionGetters.getOuterRingOfRadiusRandom(this.elementProjectile.position(), 1.5, this.novaMaxSize * 30, this::setParticleNova);
        if(!this.isBuddy){
            PositionGetters.getOuterRingOfRadius(this.elementProjectile.position(), 0.5, 300, this::setShockwaveNova);
        }
        var maxPart = Math.max((int) this.maxRadius / 2, 1);

        if(this.elementProjectile.level() instanceof ServerLevel serverLevel){
            ParticleHandlers.particleBurst(serverLevel, this.elementProjectile.position(), maxPart,
                genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, this.getElementType(), 40, 3f)
                ,0,0,0,speed
            );

            ParticleHandlers.particleBurst(serverLevel, this.elementProjectile.position(), maxPart,
                genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, 40, 3f, rgbToInt(61,61,61), rgbToInt(218,218,218))
                ,0,0,0,speed
            );
        }

        ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.EXPLOSION.get(),2f);
        ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundEvents.FIRE_AMBIENT);

        this.elementProjectile.setDeltaMovement(0,0,0);
        this.hasHitLocation = true;
        elementProjectile.setShowTrailParticles(false);
        elementProjectile.setAnimation(2);

    }

    private Level level(){
        return this.elementProjectile.level();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    ResourceLocation abilityId = ModHelpers.res("fireball_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new FireBall();
    }
}
