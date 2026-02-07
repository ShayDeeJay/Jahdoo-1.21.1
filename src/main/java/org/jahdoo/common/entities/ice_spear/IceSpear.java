package org.jahdoo.common.entities.ice_spear;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.ability.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.registers.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

import javax.annotation.Nullable;

import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.ability.ProjectileProperties.setProjectileWithOffsets;
import static org.jahdoo.common.entities.EntityAnimations.ICE_SPEAR;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE;
import static org.jahdoo.common.registers.AttributeReg.FROST_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.mod.ElementReg.frost;
import static software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache;

public class IceSpear extends AbstractArrow implements GeoEntity {

    private final AnimatableInstanceCache geoCache = createInstanceCache(this);
    private boolean dealtDamage;
    private double damage;
    private double effectDuration;
    private double effectStrength;
    private double range;

    public IceSpear(EntityType<? extends IceSpear> entityType, Level level) {
        super(entityType, level);
    }

    public IceSpear(LivingEntity livingEntity) {
        super(EntityReg.ICE_SPEAR.get(), livingEntity.level());
        this.setOwner(livingEntity);
        setProjectileWithOffsets(this, livingEntity, 0, 2.5);
        this.reapplyPosition();
        if(this.getOwner() != null) {
            var player = this.getOwner();
            this.damage = JahdooHelpers.attributeModifierCalculator(
                (LivingEntity) player, (float) getProperty(DAMAGE), true, MAGIC_DAMAGE_MULTIPLIER, FROST_MAGIC_DAMAGE_MULTIPLIER
            );
        }
        this.effectDuration = getProperty(EFFECT_DURATION);
        this.effectStrength = getProperty(EFFECT_STRENGTH);
        this.range = getProperty(RANGE);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    public double getProperty(String name){
        return CasterData.getSpecificValue((Player) getOwner(), name);
    }

    @Override
    public boolean mayInteract(Level level, BlockPos pos) {
        return true;
    }

    @Override
    public boolean mayBreak(Level level) {
        return true;
    }

    public void tick() {
        if(inGroundTime > 0 && (inGroundTime % 4 == 0)){
            this.playSound(SoundReg.TIMER.get(), 1.0F, 2.0F);
            this.playSound(SoundReg.HEAL.get(), 1.0F, 2.0F);
        }

        if (this.inGroundTime == 24) {
            var maxPart = 10;
            this.dealtDamage = true;
            this.playSound(SoundReg.FROST_ABILITY.get(), 1.0F, 1.0F);
            this.playSound(SoundReg.IMPACT.get(), 1.0F, 1.0F);
            if(level() instanceof ServerLevel serverLevel){
                var lifetime = (int) (range * 10);
                var size = 3F;
                var speed = (float) range/15;
                var generic = genericParticle(MAGIC_PARTICLE, frost(), lifetime, size - 1);
                var baked = bakedParticle(frost().id(), lifetime, size, false);
                particleBurst(serverLevel, this.position(), maxPart, generic, 0, 0, 0, speed);
                particleBurst(serverLevel, this.position(), maxPart, baked, 0, 0, 0, speed);
            }

            var local = level().getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                (LivingEntity) getOwner(),
                this.getBoundingBox().inflate(range)
            );

            for (var entity : local) {
                var instance = new JahdooMobEffect(EffectReg.FROST_EFFECT, (int) effectDuration, (int) effectStrength);
                entity.addEffect(instance);
            }

            this.discard();
        }

        if(tickCount > 1  && !level().isClientSide){
            var particle = genericParticle(MAGIC_PARTICLE, frost(), 3, 1);
            particleBurst(level(), position(), 1, particle, 0.02F);
        }

        super.tick();
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    protected void onHitEntity(EntityHitResult result) {
        var entity = result.getEntity();
        DamageUtils.damageWithJahdoo(entity, getOwner(), damage, DamageTypeReg.FROST_SOURCE);

        this.setDeltaMovement(this.getDeltaMovement().multiply(0, 0, 0));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        this.playSound(SoundReg.FROST_ABILITY.get(), 1.0F, 1.0F);
    }

    protected void hitBlockEnchantmentEffects(ServerLevel level, BlockHitResult hitResult, ItemStack stack) {
        var vec3 = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
        var var6 = this.getOwner();
        var var10002 = var6 instanceof LivingEntity livingentity ? livingentity : null;

        EnchantmentHelper.onHitBlock(level, stack, var10002, this, null, vec3, level.getBlockState(hitResult.getBlockPos()), (p_348680_) -> this.kill());
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public void playerTouch(Player entity) {}

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ItemReg.WAND_ITEM_FROST);
    }

    public ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.dealtDamage = compound.getBoolean("DealtDamage");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var animation = new AnimationController<>(this, state -> state.setAndContinue(ICE_SPEAR));
        controllers.add(animation);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
