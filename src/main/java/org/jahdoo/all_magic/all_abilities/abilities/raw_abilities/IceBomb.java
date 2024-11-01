package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.IceBombAbility;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.all_magic.effects.CustomMobEffect;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.*;
import static org.jahdoo.all_magic.AbilityBuilder.*;

public class IceBomb extends DefaultEntityBehaviour {

    boolean hasHitBlock;
    int currentLifetime;
    private double aoe = 0.3;
    List<UUID> getHitEntities = new ArrayList<>();

    double damage;
    double effectDuration;
    double effectStrength;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
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
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("currentLifetime", this.currentLifetime);
        compoundTag.putBoolean("hasHitBlock", this.hasHitBlock);
        compoundTag.putDouble("aoe",aoe);
        CompoundTag compoundTag1 = new CompoundTag();
        getHitEntities.forEach(uuid -> compoundTag1.putUUID(String.valueOf(getHitEntities.indexOf(uuid)), uuid));
        compoundTag.put("entities", compoundTag1);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.currentLifetime = compoundTag.getInt("currentLifetime");
        this.hasHitBlock = compoundTag.getBoolean("hasHitBlock");
        this.aoe = compoundTag.getDouble("aoe");
        compoundTag.getCompound("entities").getAllKeys().forEach(
            entries -> this.getHitEntities.add(compoundTag.getUUID("entities"))
        );
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    @Override
    public double getTag(String name) {
        var wandAbilityHolder = this.elementProjectile.wandAbilityHolder();
        var ability = IceBombAbility.abilityId.getPath().intern();
        return ModHelpers.getModifierValue(wandAbilityHolder, ability).get(name).actualValue();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(!hasHitBlock){
            hasHitBlock = true;
            double particleMultiplier = 2;
            this.elementProjectile.setDeltaMovement(0,0,0);
            elementProjectile.setShowTrailParticles(false);
            elementProjectile.setAnimation(2);

            ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.EXPLOSION.get());
            ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.ICE_ATTACH.get());
            PositionGetters.getOuterRingOfRadiusRandom(this.elementProjectile.position(), particleMultiplier, 300,
                worldPosition -> this.setParticleNova(worldPosition, particleMultiplier)
            );
        }
    }

    @Override
    public void onTickMethod() {
        if(!hasHitBlock){
            elementProjectile.setAnimation(3);
            this.iceBombIdleParticles();
            this.playPeriodicIdleSound();
            this.elementProjectile.setDeltaMovement(this.elementProjectile.getDeltaMovement().subtract(0, 0.02, 0));
        } else {
            this.currentLifetime++;
            this.applyDamageAndEffectNova();
        }
    }

    private void setParticleNova(Vec3 worldPosition, double particleMultiplier){
        Vec3 positionScrambler = worldPosition.offsetRandom(RandomSource.create(), (float) particleMultiplier);
        Vec3 directions = positionScrambler.subtract(this.elementProjectile.position()).normalize();
        ParticleOptions genericParticle = genericParticleOptions(
            GENERIC_PARTICLE_SELECTION,
            (int) (particleMultiplier * 5),
            5,
            this.getElementType().particleColourPrimary(),
            rgbToInt(255,255,255),
            false
        );

        ParticleOptions bakedParticle = new BakedParticleOptions(
            this.getElementType().getTypeId(),
            (int) (particleMultiplier * 20),
            5,
            false
        );

        List<ParticleOptions> getRandomParticle = List.of(bakedParticle, genericParticle);

        ParticleHandlers.sendParticles(
            level(), getRandomParticle.get(ModHelpers.Random.nextInt(2)) ,worldPosition, 0, directions.x, directions.y, directions.z, ModHelpers.Random.nextDouble(0.8,1.0)
        );
    }

    private Level level(){
        return this.elementProjectile.level();
    }

    void applyDamageAndEffectNova(){
        if(aoe < (double) 8){
            if(aoe < 2) aoe *= 1.5; else aoe += 0.5;
            this.freezeAndDamageEnemiesNearby();
        }
    }

    private void freezeAndDamageEnemiesNearby(){
        this.elementProjectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.elementProjectile.getOwner(),
            this.elementProjectile.getBoundingBox().inflate(aoe)
        ).forEach(this::entityFreezeEffectAndDamage);
    }

    private void entityFreezeEffectAndDamage(LivingEntity hitEntity){
        if(!this.damageEntity(hitEntity)) return;
        if(!this.getHitEntities.contains(hitEntity.getUUID())){
            this.getHitEntities.add(hitEntity.getUUID());
            hitEntity.hurt(
                this.elementProjectile.damageSources().playerAttack((Player) this.elementProjectile.getOwner()),
                (float) this.damage
            );

            if (!hitEntity.hasEffect(EffectsRegister.ICE_EFFECT.getDelegate())) {
                hitEntity.addEffect(
                    new CustomMobEffect(
                        EffectsRegister.ICE_EFFECT.getDelegate(),
                        (int) effectDuration,
                        (int) this.effectStrength
                    )
                );
            }
        }
    }

    void iceBombIdleParticles(){
        if(hasHitBlock) return;
        ParticleOptions bakedParticle = new BakedParticleOptions(
            this.getElementType().getTypeId(),
            1,
            ModHelpers.Random.nextFloat(1.5f, 2.5f),
            false
        );

        ParticleOptions genericParticle = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 2, 1f);

        Vec3 getPositions = ModHelpers.getRandomParticleVelocity(this.elementProjectile, 0.05);
        PositionGetters.getRandomSphericalPositions(this.elementProjectile, 0.5f, 30,
            position -> {
                Vec3 newPosition = position.add(this.elementProjectile.getDeltaMovement().scale(-1.5));
                this.idleStandard(getPositions, newPosition, level(), bakedParticle);
                this.idleStandard(getPositions, newPosition, level(), genericParticle);
            }
        );
    }

    private void playPeriodicIdleSound(){
        if(this.elementProjectile.tickCount == 1){
            ModHelpers.getSoundWithPosition(
                this.elementProjectile.level(),
                this.elementProjectile.blockPosition(),
                SoundRegister.TIMER.get(),
                0.8f, 0.1f
            );
        }

        if (this.elementProjectile.tickCount % 21 == 0) {
            ModHelpers.getSoundWithPosition(
                this.elementProjectile.level(),
                this.elementProjectile.blockPosition(),
                SoundRegister.TIMER.get(),
                0.8f, 0.1f
            );
        }
    }

    private void idleStandard(
        Vec3 getPositions2,
        Vec3 newPosition,
        Level serverLevel,
        ParticleOptions particle
    ){
        ParticleHandlers.sendParticles(
            serverLevel,
            particle,
            newPosition.add(0,0.2,0),
            0, getPositions2.x,getPositions2.y,getPositions2.z,1
        );
    }

    @Override
    public void discardCondition() {
        if(currentLifetime > 50) this.elementProjectile.discard();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.FROST.get();
    }

    ResourceLocation abilityId = ModHelpers.modResourceLocation("ice_bomb_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new IceBomb();
    }
}
