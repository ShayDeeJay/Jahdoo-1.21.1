package org.jahdoo.ascension.ability.abilities_combat.ice_bomb;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.getRandomParticleVelocity;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.registers.AttributeReg.FROST_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;

public class IceBomb extends DefaultEntityBehaviour {

    private final ResourceLocation abilityId = Helpers.res("ice_bomb_property");
    private final List<UUID> getHitEntities = new ArrayList<>();
    private static final int INT = 60;
    private boolean hasHitBlock;
    private int currentLifetime;
    private double aoe = 0.3;

    private double effectDuration;
    private double effectStrength;
    private double radScale;
    private double damage;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        if(this.element.getOwner() != null){
            var player = this.element.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = Helpers.attributeModifierCalculator(
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
    public WandAbilityHolder getWandAbilityHolder() {
        return this.element.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return IceBombAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.element.setDeltaMovement(0,0,0);
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        this.element.setDeltaMovement(0,0,0);
    }

    private Level level(){
        return this.element.level();
    }

    @Override
    public void discardCondition() {
        if(currentLifetime > 50) this.element.discard();
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
        return new IceBomb();
    }

    void applyDamageAndEffectNova(){
        if(aoe < 3) aoe *= 1.5;
        this.freezeAndDamageEnemiesNearby();
    }

    private void idleStandard(
        Vec3 pos2,
        Vec3 nPos,
        Level lvl,
        ParticleOptions particle
    ){
        var min = Math.min((float) this.element.tickCount / 300, 0.12);
        ParticleHandlers.sendParticles(lvl, particle, nPos.add(0,0.15,0), 1, pos2.x, pos2.y, pos2.z, min);
    }

    private void freezeAndDamageEnemiesNearby(){
        level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.element.getOwner(),
            this.element.getBoundingBox().inflate(aoe)
        ).forEach(this::entityFreezeEffectAndDamage);
    }

    private void onDetonate() {
        if(this.hasHitBlock){
            this.element.setDeltaMovement(0, 0, 0);
            element.setShowTrailParticles(false);
            element.setAnimation(2);

            Helpers.getSoundWithPosition(level(), this.element.blockPosition(), SoundReg.EXPLOSION.get());
            Helpers.getSoundWithPosition(level(), this.element.blockPosition(), SoundReg.ICE_ATTACH.get());
            PositionFinders.getOuterRingOfRadiusRandom(this.element.position(), 0.5, 150,
                worldPosition -> this.setParticleNova(worldPosition, 0.7)
            );
        }
    }

    private void entityFreezeEffectAndDamage(LivingEntity hitEntity){
        if(!canDamageEntity(hitEntity, (LivingEntity) this.element.getOwner())) return;
        if(!this.getHitEntities.contains(hitEntity.getUUID())){
            this.getHitEntities.add(hitEntity.getUUID());
            DamageUtils.damageWithJahdoo(hitEntity, this.element.getOwner(), this.damage);
            if (!hitEntity.hasEffect(EffectReg.FROST_EFFECT.getDelegate())) {
                System.out.println(this.effectStrength);
                hitEntity.addEffect(
                    new JahdooMobEffect(EffectReg.FROST_EFFECT.getDelegate(), (int) effectDuration, (int) this.effectStrength)
                );
            }
        }
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
    public void onTickMethod() {
        applyInertia(this.element, 0.955f);
        this.hasHitBlock = this.element.tickCount > INT - 30;
        if(!hasHitBlock){
            element.setAnimation(3);
            this.iceBombIdleParticles();
            this.playPeriodicIdleSound();
        } else {
            if(currentLifetime == 0) this.onDetonate();
            this.applyDamageAndEffectNova();
            this.currentLifetime++;
        }
    }

    void iceBombIdleParticles(){
        if(hasHitBlock) return;
        var bakedParticle = bakedParticle(this.getElementType().id(), 2, Helpers.Random.nextFloat(1.5f, 2f), false);
        var genericParticle = ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE, this.getElementType(), 3, 1);

        PositionFinders.getRandomSphericalPositions(
            this.element,
            this.element.getBbWidth() + 0.2 + radScale,
            Math.min(14 + (this.radScale * 50), 20),
            position -> {
                var getPositions = getRandomParticleVelocity(this.element, 0.05);
                var newPosition = position.add(this.element.getDeltaMovement().scale(-1.5));
                this.idleStandard(getPositions, newPosition, level(), bakedParticle);
                this.idleStandard(getPositions, newPosition, level(), genericParticle);
            }
        );
    }

    private void setParticleNova(Vec3 worldPosition, double particleMultiplier){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), (float) particleMultiplier/2);
        var directions = positionScrambler.subtract(this.element.position()).normalize();
        var part1 = this.getElementType().partColourA();
        var part2 = this.getElementType().textColourA();
        var lifetime = (int) (particleMultiplier * 10);
        var genericParticle = ParticleHandlers.genericParticle(GENERIC_PARTICLE, lifetime, 5, part1, part2, false);
        var bakedParticle = bakedParticle(this.getElementType().id(), lifetime, 5, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle).get(Helpers.Random.nextInt(2));
        var randSpeed = Helpers.Random.nextDouble(0.3, 0.5);
        var positions = worldPosition.offsetRandom(RandomSource.create(), 0.5f);

        ParticleHandlers.sendParticles(
            level(), getRandomParticle , positions, 0, directions.x, directions.y, directions.z, randSpeed
        );
    }

    private void playPeriodicIdleSound(){
        if(this.element.tickCount == 1){
            Helpers.getSoundWithPosition(
                level(),
                this.element.blockPosition(),
                SoundReg.TIMER.get(),
                0.8f, 0.1f
            );
        }

        var isIdle = this.element.tickCount < (INT - 45);
        if(!isIdle) this.element.setDeltaMovement(0,0,0);
        if (this.element.tickCount % (isIdle ? 21 : 2) == 0) {
            this.radScale += 0.07f;
            Helpers.getSoundWithPosition(
                level(),
                this.element.blockPosition(),
                SoundReg.TIMER.get(),
                0.8f, 0.1f
            );
        }
    }
}
