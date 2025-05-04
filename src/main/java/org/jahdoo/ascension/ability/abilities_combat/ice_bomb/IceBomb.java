package org.jahdoo.ascension.ability.abilities_combat.ice_bomb;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.minecraft.util.RandomSource.create;
import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.getRandomParticleVelocity;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
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
    private double ricochets;
    private double damage;
    private int currentRicochets;

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
        this.ricochets = this.getTag(NUMBER_OF_RICOCHET);
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.element.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return IceBombAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        var velocity = element.getDeltaMovement();
        var hitDirection = blockHitResult.getDirection();
        var normal = Vec3.atLowerCornerOf(hitDirection.getNormal());
        var reflected = velocity.subtract(normal.scale(2 * velocity.dot(normal)));
        var damping = 0.9;
        currentRicochets++;
        reflected = reflected.scale(damping);
        onDetonate();
        this.applyDamageAndEffectNova();
        element.setDeltaMovement(reflected);
        if(this.ricochets == currentRicochets) this.element.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
//        this.element.setDeltaMovement(0,0,0);
    }

    private Level level(){
        return this.element.level();
    }

    @Override
    public void discardCondition() {
//        if(currentLifetime > 50) this.element.discard();
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
        ParticleHandlers.sendParticles(lvl, particle, nPos.add(0,0.15,0), 1, pos2.x, pos2.y, pos2.z, 0.1F);
    }

    private void freezeAndDamageEnemiesNearby(){
        level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.element.getOwner(),
            this.element.getBoundingBox().inflate(this.currentRicochets)
        ).forEach(this::entityFreezeEffectAndDamage);
    }

    private void onDetonate() {
        element.setShowTrailParticles(false);
        Helpers.getSoundWithPosition(level(), this.element.blockPosition(), SoundReg.IMPACT.get(), 1, 1 + ((float) this.currentRicochets /10));
        Helpers.getSoundWithPosition(level(), this.element.blockPosition(), SoundReg.FROST_ABILITY.get(), 1, 1 + ((float) this.currentRicochets /10));
        PositionFinders.getOuterRingOfRadiusRandom(this.element.position(), 0.5, 150 * currentRicochets,
            worldPosition -> this.setParticleNova(worldPosition, 0.7)
        );
    }

    private void entityFreezeEffectAndDamage(LivingEntity hitEntity){
        if(!canDamageEntity(hitEntity, (LivingEntity) this.element.getOwner())) return;
        if(!this.getHitEntities.contains(hitEntity.getUUID())){
            this.getHitEntities.add(hitEntity.getUUID());
            DamageUtils.damageWithJahdoo(hitEntity, this.element.getOwner(), this.damage, getElementType().damageTypeResourceKey());
            if (!hitEntity.hasEffect(EffectReg.FROST_EFFECT.getDelegate())) {
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
        compoundTag.putDouble(NUMBER_OF_RICOCHET, this.ricochets);
        compoundTag.putInt("current_ricochets", this.currentRicochets);
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
        this.ricochets = compoundTag.getDouble(NUMBER_OF_RICOCHET);
        this.currentRicochets = compoundTag.getInt("current_ricochets");
    }

    @Override
    public void onTickMethod() {
        var deltaMovement = this.element.getDeltaMovement();
        this.element.setDeltaMovement(deltaMovement.x, deltaMovement.y - 0.05, deltaMovement.z);
        element.setAnimation(1);
        this.iceBombIdleParticles();
    }

    void iceBombIdleParticles(){
        if(hasHitBlock) return;
        var bakedParticle = bakedParticle(this.getElementType().id(), 2, Helpers.Random.nextFloat(1.5f, 2f), false);
        var genericParticle = ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE, this.getElementType(), 3, 1);

        PositionFinders.getRandomSphericalPositions(
            this.element, this.element.getBbWidth() + 0.2, 8,
            position -> {
                var getPositions = getRandomParticleVelocity(this.element, 0.05);
                var newPosition = position.add(this.element.getDeltaMovement().scale(-1.5));
                this.idleStandard(getPositions, newPosition, level(), bakedParticle);
                this.idleStandard(getPositions, newPosition, level(), genericParticle);
            }
        );
    }

    private void setParticleNova(Vec3 worldPosition, double particleMultiplier){
        var positionScrambler = worldPosition.offsetRandom(create(), (float) particleMultiplier/2);
        var directions = positionScrambler.subtract(this.element.position()).normalize();
        var rico = this.currentRicochets;
        var i = rico / 2;
        var getParticle = ParticleHandlers.getAllParticleTypes(getElementType(), 10 + (rico), 3 + i);
        var i1 = (float) rico / 8;
        var randSpeed = Helpers.Random.nextDouble(0.1 + i1, 0.2 + i1);
        var positions = worldPosition.offsetRandom(create(), 0.2f);

        ParticleHandlers.sendParticles(level(), getParticle , positions, 0, directions.x, directions.y, directions.z, randSpeed);
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
        if (this.element.tickCount % (21) == 0) {
            Helpers.getSoundWithPosition(
                level(),
                this.element.blockPosition(),
                SoundReg.TIMER.get(),
                0.8f, 0.1f
            );
        }
    }
}
