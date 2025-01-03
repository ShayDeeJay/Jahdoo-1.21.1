package org.jahdoo.ability.abilities.ability;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.IceBombAbility;
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

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttributesRegister.FROST_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.utils.ModHelpers.getRandomParticleVelocity;

public class IceBomb extends DefaultEntityBehaviour {

    public static int INT = 60;
    boolean hasHitBlock;
    int currentLifetime;
    private double aoe = 0.3;
    List<UUID> getHitEntities = new ArrayList<>();

    double radScale;
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
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                FROST_MAGIC_DAMAGE_MULTIPLIER
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
    public WandAbilityHolder getWandAbilityHolder() {
        return this.elementProjectile.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return IceBombAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.elementProjectile.setDeltaMovement(0,0,0);
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        this.elementProjectile.setDeltaMovement(0,0,0);
    }

    private void onDetonate() {
        if(this.hasHitBlock){
            this.elementProjectile.setDeltaMovement(0, 0, 0);
            elementProjectile.setShowTrailParticles(false);
            elementProjectile.setAnimation(2);

            ModHelpers.getSoundWithPosition(level(), this.elementProjectile.blockPosition(), SoundRegister.EXPLOSION.get());
            ModHelpers.getSoundWithPosition(level(), this.elementProjectile.blockPosition(), SoundRegister.ICE_ATTACH.get());
            PositionGetters.getOuterRingOfRadiusRandom(this.elementProjectile.position(), 1, 200,
                worldPosition -> this.setParticleNova(worldPosition, 0.7)
            );
        }
    }

    @Override
    public void onTickMethod() {
        applyInertia(this.elementProjectile, 0.955f);
        this.hasHitBlock = this.elementProjectile.tickCount > INT - 30;
        if(!hasHitBlock){
            elementProjectile.setAnimation(3);
            this.iceBombIdleParticles();
            this.playPeriodicIdleSound();
        } else {
            if(currentLifetime == 0) this.onDetonate();
            this.applyDamageAndEffectNova();
            this.currentLifetime++;
        }
    }

    private void setParticleNova(Vec3 worldPosition, double particleMultiplier){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), (float) particleMultiplier);
        var directions = positionScrambler.subtract(this.elementProjectile.position()).normalize();
        var part1 = this.getElementType().particleColourPrimary();
        var part2 = this.getElementType().textColourPrimary();
        var lifetime = (int) (particleMultiplier * 20);
        var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, lifetime, 5, part1, part2, false);
        var bakedParticle = bakedParticleOptions(this.getElementType().getTypeId(), lifetime, 5, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle).get(ModHelpers.Random.nextInt(2));
        var randSpeed = ModHelpers.Random.nextDouble(0.6, 1.4);
        var positions = worldPosition.offsetRandom(RandomSource.create(), 0.5f);

        ParticleHandlers.sendParticles(
            level(), getRandomParticle , positions, 0, directions.x, directions.y, directions.z, randSpeed
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
        level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.elementProjectile.getOwner(),
            this.elementProjectile.getBoundingBox().inflate(aoe)
        ).forEach(this::entityFreezeEffectAndDamage);
    }

    private void entityFreezeEffectAndDamage(LivingEntity hitEntity){
        if(!canDamageEntity(hitEntity, (LivingEntity) this.elementProjectile.getOwner())) return;
        if(!this.getHitEntities.contains(hitEntity.getUUID())){
            this.getHitEntities.add(hitEntity.getUUID());
            DamageUtil.damageWithJahdoo(hitEntity, this.elementProjectile.getOwner(), this.damage);
            if (!hitEntity.hasEffect(EffectsRegister.ICE_EFFECT.getDelegate())) {
                hitEntity.addEffect(
                    new CustomMobEffect(EffectsRegister.ICE_EFFECT.getDelegate(), (int) effectDuration, (int) this.effectStrength)
                );
            }
        }
    }

    void iceBombIdleParticles(){
        if(hasHitBlock) return;
        var bakedParticle = bakedParticleOptions(this.getElementType().getTypeId(), 2, ModHelpers.Random.nextFloat(1.5f, 2f), false);
        var genericParticle = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 3, 1);

        PositionGetters.getRandomSphericalPositions(
            this.elementProjectile,
            this.elementProjectile.getBbWidth() + 0.2 + radScale,
            Math.min(14 + (this.radScale * 50), 20),
            position -> {
                var getPositions = getRandomParticleVelocity(this.elementProjectile, 0.05);
                var newPosition = position.add(this.elementProjectile.getDeltaMovement().scale(-1.5));
                this.idleStandard(getPositions, newPosition, level(), bakedParticle);
                this.idleStandard(getPositions, newPosition, level(), genericParticle);
            }
        );
    }

    private void playPeriodicIdleSound(){
        if(this.elementProjectile.tickCount == 1){
            ModHelpers.getSoundWithPosition(
                level(),
                this.elementProjectile.blockPosition(),
                SoundRegister.TIMER.get(),
                0.8f, 0.1f
            );
        }

        if (this.elementProjectile.tickCount % (this.elementProjectile.tickCount < (INT - 45) ? 21 : 2) == 0) {
            this.radScale += 0.1f;
            ModHelpers.getSoundWithPosition(
                level(),
                this.elementProjectile.blockPosition(),
                SoundRegister.TIMER.get(),
                0.8f, 0.1f
            );
        }
    }

    private void idleStandard(
        Vec3 pos2,
        Vec3 nPos,
        Level lvl,
        ParticleOptions particle
    ){
        var min = Math.min((float) this.elementProjectile.tickCount / 300, 0.12);
        ParticleHandlers.sendParticles(lvl, particle, nPos.add(0,0.15,0), 1, pos2.x, pos2.y, pos2.z, min);
    }

    @Override
    public void discardCondition() {
        if(currentLifetime > 50) this.elementProjectile.discard();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.FROST.get();
    }

    ResourceLocation abilityId = ModHelpers.res("ice_bomb_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new IceBomb();
    }
}
