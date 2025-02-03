package org.jahdoo.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.ProjectileProperties;
import org.jahdoo.ability.abilities.ability_data.BurningSkullsAbility;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.living.EternalWizard;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static java.util.Comparator.*;
import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.ability.DefaultEntityBehaviour.*;
import static org.jahdoo.entities.EntityAnimations.*;
import static org.jahdoo.entities.EntityMovers.*;
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.registers.AttributesRegister.INFERNO_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.DataComponentRegistry.*;
import static org.jahdoo.registers.EffectsRegister.*;
import static org.jahdoo.utils.ModHelpers.*;

public class BurningSkull extends ProjectileProperties implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> LIFETIMES = SynchedEntityData.defineId(BurningSkull.class, EntityDataSerializers.INT);

    public LivingEntity target;
    double damage;
    double effectDuration;
    double effectStrength;
    double effectChance;
    private float bobOffset = 0; // This will keep track of the bob progress
    public BurningSkull(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setLifetimes(-1);
    }

    public BurningSkull(
        LivingEntity owner,
        double spacing,
        @Nullable LivingEntity target
    ) {
        super(EntitiesRegister.FLAMING_SKULL.get(), owner.level());
        if(target != null) this.target = target;
        this.setProjectileWithOffsets(this, owner, spacing, 1);
        this.reapplyPosition();
        this.setOwner(owner);
        var holder = owner.getItemInHand(owner.getUsedItemHand()).get(WAND_ABILITY_HOLDER.get());
        this.effectChance = getTag(EFFECT_CHANCE, holder);
        this.effectStrength = getTag(EFFECT_STRENGTH, holder);
        this.effectDuration = getTag(EFFECT_DURATION, holder);
        setLifetimes((int) getTag(LIFETIME, holder));
        if(!(this.getOwner() instanceof Player)){
            this.damage = getTag(DAMAGE, holder);
        } else {
            damageWithModifiers(holder);
        }
    }

    public void setTarget(LivingEntity livingEntity){
        this.target = livingEntity;
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIMES);
    }

    public void setLifetimes(int lifetimes) {
        this.entityData.set(LIFETIMES, lifetimes);
    }

    private void damageWithModifiers(WandAbilityHolder holder) {
        var player = this.getOwner();
        var damage = getTag(DAMAGE, holder);
        this.damage = ModHelpers.attributeModifierCalculator(
            (LivingEntity) player,
            (float) damage,
            true,
            MAGIC_DAMAGE_MULTIPLIER,
            INFERNO_MAGIC_DAMAGE_MULTIPLIER
        );
    }

    @Override
    public void tick() {
        super.tick();
        setTargetDelay();
        ambientSound();
        entityMovement();
        discardTime();
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
        this.lerpX = pX;
        this.lerpY = pY;
        this.lerpZ = pZ;
        this.lerpYRot = pYRot;
        this.lerpXRot = pXRot;
        this.lerpSteps = 15;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        var entity = entityHitResult.getEntity();
        if(!(level() instanceof ServerLevel) || !(entity instanceof LivingEntity livingEntity) ) return;
        if(!canDamageEntity(livingEntity, (LivingEntity) this.getOwner())) return;
        var bitePitch = 0.75F;
        var biteVolume = 0.6F;
        var biteSound = SoundEvents.BLAZE_SHOOT;
        this.playSound(biteSound, biteVolume, bitePitch);
        if(effectChance == 0 || Random.nextInt((int) effectChance) == 0){
            var effectInstance = new JahdooMobEffect(INFERNO_EFFECT, (int) effectDuration, (int) effectStrength);
            livingEntity.addEffect(effectInstance);
        }
        entity.hurt(this.damageSources().generic(), (float) damage);
        discardTask();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        if(!(level() instanceof ServerLevel )) return;
        discardTask();
    }

    private void entityMovement() {
        this.setLifetimes(getLifetime());
        var canPathFind = target != null && target.isAlive() && !level().isClientSide;
        if (canPathFind) entityMover(target, this, 0.2);
        flamingSkull(this, tickCount, 0.35f, this.getElementType());
    }

    private void discardTask() {
        for (int i = 0; i < 5; i++){
            var splashParticles = getAllParticleTypesAlt(getElementType(), 10, 2);
            particleBurst(this.level(), this.position(), 1, splashParticles, 0.1f);
        }
        this.discard();
    }

    private void setTargetDelay(){
        if(this.tickCount > 5) this.setTarget();
    }

    private void discardTime(){
        if(getLifetime() < 0) return;
        if(this.tickCount > getLifetime()) discardTask();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(LIFETIMES, 0);
    }

    private void ambientSound() {

        var bitePitch = 0.5F;
        var biteVolume = 1F;
        var biteSound = SoundEvents.SOUL_ESCAPE.value();

        var firePitch = 1.5F;
        var fireVolume = 0.2F;
        var fireSound = SoundEvents.FIRE_AMBIENT;

        if(tickCount == 1){
            this.playSound(biteSound, biteVolume, bitePitch);
            this.playSound(fireSound, fireVolume, firePitch);
        }

        if(tickCount % 10 == 0){
            this.playSound(biteSound, biteVolume, bitePitch);
            this.playSound(fireSound, fireVolume, firePitch);
        }

    }

    public void setTarget() {
        var isTargetDead = target != null && !target.isAlive();
        if(this.target == null || isTargetDead) {
            var nearbyEntities = getValidTargets(this, (LivingEntity) this.getOwner(), 20);

            if(!nearbyEntities.isEmpty()){
                var getClosest = nearbyEntities.getFirst();
                var canSee = hasLineOfSight(this, getClosest);
                if(canSee) this.target = getClosest;
            }
        }
    }

    public static List<LivingEntity> getValidTargets(Entity entity, LivingEntity owner, int range) {
        return entity.level()
                .getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(range))
                .stream()
                .filter(livingEntity -> !(livingEntity instanceof Player))
                .filter(livingEntity -> canDamageEntity(livingEntity, owner))
                .sorted(comparingDouble(livingEntity -> livingEntity.distanceToSqr(entity)))
                .toList();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putDouble("damage", damage);
        pCompound.putDouble("effect_duration", effectDuration);
        pCompound.putDouble("effect_strength", effectStrength);
        pCompound.putDouble("effect_chance", effectChance);
//        pCompound.putDouble("lifetime", lifetime);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.damage = pCompound.getDouble("damage");
        this.effectDuration = pCompound.getDouble("effect_duration");
        this.effectStrength = pCompound.getDouble("effect_strength");
        this.effectChance = pCompound.getDouble("effect_chance");
//        this.lifetime = pCompound.getDouble("lifetime");
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var animation = new AnimationController<>(this, state -> state.setAndContinue(IDLE_SKULL));
        controllers.add(animation);
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }


}
