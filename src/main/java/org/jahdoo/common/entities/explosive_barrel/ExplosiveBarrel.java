package org.jahdoo.common.entities.explosive_barrel;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.ability.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.Maths;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.network.syncher.EntityDataSerializers.FLOAT;
import static net.minecraft.network.syncher.EntityDataSerializers.INT;
import static net.minecraft.network.syncher.SynchedEntityData.defineId;
import static net.minecraft.world.entity.ai.attributes.Attributes.SCALE;
import static org.jahdoo.common.entities.EntityAnimations.*;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.addBaseAttribute;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.PositionFinders.getOuterRingOfRadiusRandom;
import static software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache;

public class ExplosiveBarrel extends LivingEntity implements GeoEntity {

    private final AnimatableInstanceCache geoCache = createInstanceCache(this);
    private static final EntityDataAccessor<Integer> CURRENT_STATE = defineId(ExplosiveBarrel.class, INT);
    private static final EntityDataAccessor<Integer> TYPE = defineId(ExplosiveBarrel.class, INT);
    private static final EntityDataAccessor<Float> DAMAGE_COUNTER = defineId(ExplosiveBarrel.class, FLOAT);
    private static final EntityDataAccessor<Integer> DAMAGE_REQUIRED = defineId(ExplosiveBarrel.class, INT);
    AbstractElement barrelElement;

    public ExplosiveBarrel(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        setBarrelProperties();
    }

    public ExplosiveBarrel(Level level) {
        super(EntityReg.EXPLOSIVE_BARREL.get(), level);
        setBarrelProperties();
    }

    private void setBarrelProperties() {
        var element1 = ElementReg.random();
        this.barrelElement = element1;
        this.setBarrelType(element1.id());
        setDamageRequired(50);
        addBaseAttribute(SCALE, this, 100);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var animation = new AnimationController<>(this, "Attack", this::flyAnimController);
        controllers.add(animation);
    }

    protected PlayState flyAnimController(final AnimationState<ExplosiveBarrel> animTest) {
        if (getCurrentState() == 1) return animTest.setAndContinue(Random.nextInt(2) == 0 ? KNOCK_LEFT : KNOCK_RIGHT);
        if (getCurrentState() == 2) return animTest.setAndContinue(OPEN);
        if (getCurrentState() == 3) return animTest.setAndContinue(CLOSE);

        animTest.getController().forceAnimationReset();
        return PlayState.CONTINUE;
    }

    public int getCurrentState() {
        return this.entityData.get(CURRENT_STATE);
    }

    public void setCurrentState(int lifetimes) {
        this.entityData.set(CURRENT_STATE, lifetimes);
    }

    public int getBarrelType() {
        return this.entityData.get(TYPE);
    }

    public void setBarrelType(int time) {
        this.entityData.set(TYPE, time);
    }

    public float getDamageCounter() {
        return this.entityData.get(DAMAGE_COUNTER);
    }

    public void setDamageCounter(float value) {
        this.entityData.set(DAMAGE_COUNTER, value);
    }

    public int getDamageRequired() {
        return this.entityData.get(DAMAGE_REQUIRED);
    }

    public void setDamageRequired(int value) {
        this.entityData.set(DAMAGE_REQUIRED, value);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CURRENT_STATE, 0);
        builder.define(TYPE, 20);
        builder.define(DAMAGE_COUNTER, 0f);
        builder.define(DAMAGE_REQUIRED, 200);
    }

    @Override
    public Component getName() {
        return Component.empty();
    }

    @Override
    public void kill() {
        Helpers.getSoundWithPosition(level(), this.position(), SoundReg.REJECT.get(), SoundSource.NEUTRAL);
        Helpers.getSoundWithPosition(level(), this.position(), SoundReg.ORB_CREATE.get(), SoundSource.NEUTRAL, 1F, 2F);
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void tick() {
        super.tick();
        if (getCurrentState() == 1) setCurrentState(0);
        if (getCurrentState() >= 2) {
            if (getDamageCounter() == 25) {
                setCurrentState(3);
            }
            if (getDamageCounter() >= 40) {
                this.remove(RemovalReason.KILLED);
            }
            setDamageCounter(getDamageCounter() + 1);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {}

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    public void setDeltaMovement(double x, double y, double z) {
        super.setDeltaMovement(0, -1, 0);
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    public AbstractElement getElementType(){
        return barrelElement;
    }

    public void sharedSound(SoundEvent sEvent, Float volume, Float pitch){
        Helpers.getSoundWithPosition(level(), this.position(), sEvent, SoundSource.HOSTILE, volume, pitch);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getCurrentState() < 2) {
            sharedSound(SoundEvents.AXE_STRIP, 1F, Random.nextFloat(0.8F, Math.max(0.85F, getDamageCounter() / 10)));
            sharedSound(SoundReg.BLOCK.get(), 0.15F, Random.nextFloat(1.2F, 1.6F));
            setCurrentState(1);
            setDamageCounter(getDamageCounter() + amount);

            if (getDamageCounter() >= getDamageRequired()) {
                setDamageCounter(0);
                sharedSound(SoundReg.IMPACT.get(), 1F, 0.6F);
                sharedSound(SoundEvents.GENERIC_EXPLODE.value(), 2F, 0.6F);

                setCurrentState(2);
                if (getCurrentState() == 2) {
                    if (level() instanceof ServerLevel) {
                        novaExplosion(this, getElementType(), 10, 100, 0, 3, 30, this.position());
                        this.discard();
                    };
                }
            }
        }
        return false;
    }

    public static void novaExplosion(LivingEntity livingEntity,  AbstractElement element, double damage, double effectDuration, double effectStrength, double radius, double effectChance, Vec3 pos) {
        particleBurst(
            livingEntity.level(), pos.add(0,0.2,0), 8,
            genericParticle(SOFT_PARTICLE, element, 5, 1.4f),
            0, 1.5, 0, 0.1f
        );
        getOuterRingOfRadiusRandom(pos, radius /2, 50, (a) -> setParticleNova(livingEntity, element, a));
        novaDamageBehaviour(livingEntity, element, damage, effectDuration, effectStrength, radius, effectChance);
    }

    public static void setParticleNova(LivingEntity livingEntity, AbstractElement element, Vec3 pos){
        var positionScrambler = pos.offsetRandom(RandomSource.create(), 0.5F);
        var directions = positionScrambler.subtract(livingEntity.position()).normalize();
        var lifetime = 6;
        var size = 5;
        var bakedParticle = bakedParticle(element.id(), lifetime, size, false);
        var col1 = element.partColourA();
        var col2 = element.partColourFade();
        var genericParticle = genericParticle(GENERIC_PARTICLE, lifetime, size, col1, col2, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle);

        ParticleHandlers.sendParticles(
            livingEntity.level(), getRandomParticle.get(JahdooHelpers.Random.nextInt(2)), pos.add(0, 0.6, 0), 0, directions.x, directions.y, directions.z, 0.5
        );
    }

    private static void novaDamageBehaviour(LivingEntity livingEntity, AbstractElement element, double damage, double effectDuration, double effectStrength, double radius, double effectChance){
        livingEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            livingEntity,
            livingEntity.getBoundingBox().inflate(radius, 1, radius)
        ).forEach(
            lEntity -> {
                if(Maths.percentageChance(effectChance) && lEntity != livingEntity){
                    lEntity.addEffect(new JahdooMobEffect(element.effect(), (int) effectDuration, (int) effectStrength));
                }
                DamageUtils.damageWithJahdoo(lEntity, lEntity, damage, element.damageTypeResourceKey());
            }
        );
    }

}
