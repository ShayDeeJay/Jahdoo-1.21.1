package org.jahdoo.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.ProjectileProperties;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;

import static java.util.Comparator.*;
import static org.jahdoo.ability.DefaultEntityBehaviour.*;
import static org.jahdoo.entities.EntityAnimations.*;
import static org.jahdoo.entities.EntityMovers.*;
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.registers.DataComponentRegistry.*;
import static org.jahdoo.utils.ModHelpers.*;

public class FlamingSkull extends ProjectileProperties implements IEntityProperties, GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    WandAbilityHolder wandAbilityHolder;
    public String selectedAbility;
    public LivingEntity target;

    public FlamingSkull(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FlamingSkull(
        LivingEntity owner,
//        String selectedAbility,
        double spacing
    ) {
        super(EntitiesRegister.FLAMING_SKULL.get(), owner.level());
        this.setProjectileWithOffsets(this, owner, spacing, 1);
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = owner.getItemInHand(owner.getUsedItemHand()).get(WAND_ABILITY_HOLDER.get());
//        this.selectedAbility = selectedAbility;
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
        var effectInstance = new JahdooMobEffect(EffectsRegister.INFERNO_EFFECT, 100, 2);
        livingEntity.addEffect(effectInstance);
        entity.hurt(this.damageSources().generic(), 5);
        discardTask();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        if(!(level() instanceof ServerLevel )) return;
        discardTask();
    }

    @Override
    public void tick() {
        super.tick();

        if(this.tickCount > 5) this.setTarget();

        if(this.level().isClientSide){
            if (this.lerpSteps > 0) {
                var d = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
                var e = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
                var y = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
                var g = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
                this.setYRot(this.getYRot() + (float) g / (float) this.lerpSteps);
                this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
                --this.lerpSteps;
                this.setPos(d, e, y);
                this.setRot(this.getYRot(), this.getXRot());
            }
        }

        playAttackSound();

        if(target != null) entityMover(target, this, 0.2);

        flamingSkull(this, tickCount, 0.35f, this.getElementType());

        if(this.tickCount > 50) discardTask();
    }

    private void discardTask() {
        for (int i = 0; i < 5; i++){
            var splashParticles = getAllParticleTypes(getElementType(), 10, 2);
            particleBurst(this.level(), this.position(), 1, splashParticles, 0.1f);
        }
        this.discard();
    }


    private void playAttackSound() {

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
            var bound = this.getBoundingBox().inflate(20);
            var nearbyEntities = this.level()
                .getEntitiesOfClass(LivingEntity.class, bound)
                .stream()
                .filter(livingEntity -> !(livingEntity instanceof Player))
                .sorted(comparingDouble(livingEntity -> livingEntity.distanceToSqr(this)))
                .toList();

            if(!nearbyEntities.isEmpty()){
                var getClosest = nearbyEntities.getFirst();
                var canSee = hasLineOfSight(this, getClosest);
                if(canSee) this.target = getClosest;
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);;
    }


    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this,
                state -> switch (this.animationType()) {
                    default -> state.setAndContinue(IDLE_SKULL);
                }
            )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public WandAbilityHolder getwandabilityholder() {
        return this.wandAbilityHolder;
    }
}
