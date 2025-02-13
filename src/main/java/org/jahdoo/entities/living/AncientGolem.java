package org.jahdoo.entities.living;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.entities.TamableEntity;
import org.jahdoo.entities.goals.*;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.SoundRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.neoforged.neoforge.common.CommonHooks.onLivingKnockBack;
import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleHandlers.getAllParticleTypes;

public class AncientGolem extends IronGolem implements TamableEntity {
    private static final EntityDataAccessor<Integer> LIFETIMES = SynchedEntityData.defineId(AncientGolem.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PRIVATE_TICKS = SynchedEntityData.defineId(AncientGolem.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(AncientGolem.class, EntityDataSerializers.FLOAT);
    public static final int INFINITE_LIFE = -1;

    private LivingEntity owner;
    private UUID ownerUUID;
    public AnimationState smash = new AnimationState();
    public AnimationState normal = new AnimationState();
    public AnimationState jump = new AnimationState();
    public double effectDuration;
    public double effectStrength;
    public double effectChance;
    public double damage;
    public int lifeTime;
    public int privateTicks;

    public AncientGolem(EntityType<? extends AncientGolem> entityType, Level level) {
        super(entityType, level);
        this.lifeTime = -1;
        this.damage = 15;
    }

    public AncientGolem(Level level, @Nullable LivingEntity owner) {
        super(EntitiesRegister.ANCIENT_GOLEM.get(), level);
        this.owner = owner;
        this.lifeTime = -1;
        this.damage = 15;
    }

    public AncientGolem(Level pLevel, @Nullable Player player, double damage, double effectDuration, double effectStrength, int lifeTime, double effectChance) {
        super(EntitiesRegister.ANCIENT_GOLEM.get(), pLevel);
        this.owner = player;
        this.effectDuration = effectDuration;
        this.effectStrength = effectStrength;
        this.lifeTime = lifeTime;
        this.effectChance = effectChance;
        this.damage = damage;
        this.setLifetimes(lifeTime);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 100.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.25F)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
            .add(Attributes.STEP_HEIGHT, 1.0F)
            .add(Attributes.ATTACK_DAMAGE, 15f);
    }

    public float getInternalScale() {
        return this.entityData.get(SCALE);
    }

    public void setScale(float getSelectedAbility) {
        this.entityData.set(SCALE, getSelectedAbility);
    }

    public void setLifetimes(int lifetimes) {
        this.entityData.set(LIFETIMES, lifetimes);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIMES);
    }

    public int getPrivateTicks() {
        return this.entityData.get(PRIVATE_TICKS);
    }

    public void setPrivateTicks(int privateTicks) {
        this.entityData.set(PRIVATE_TICKS, privateTicks);
    }

    @Override
    protected SoundEvent getDeathSound() {
        this.playSound(SoundEvents.ELDER_GUARDIAN_HURT, 1, 0.8f);
        this.playSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 1, 0.8f);
        return SoundEvents.EMPTY;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        this.playSound(SoundEvents.ELDER_GUARDIAN_HURT, 1, 2f);
        this.playSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 1, 2f);
        return SoundEvents.EMPTY;
    }

    @Override
    protected void playAttackSound() {
        this.playSound(SoundEvents.VAULT_PLACE, 1, 1.2f);
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1, 0.6f);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        this.playSound(SoundEvents.ELDER_GUARDIAN_AMBIENT, 0.2f, 1.8f);
        this.playSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 0.2f, 2f);
        return SoundEvents.EMPTY;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        var isRunning = this.getSpeed() > 0.25;
        var volume = isRunning ? 2 : 1;
        var pitch = isRunning ? 0.4f : 1.2f;
        var pitch2 = isRunning ? 0.2f : 0.6f;
        this.playSound(SoundEvents.VAULT_PLACE, volume, pitch);
        this.playSound(SoundEvents.IRON_GOLEM_STEP, volume, pitch2);
    }

    public void clientDiggingParticles(LivingEntity livingEntity, Level level) {
        var randomsource = livingEntity.getRandom();
        var blockstate = livingEntity.getBlockStateOn();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 25; ++i) {
                var d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -1F, 1F);
                var d1 = livingEntity.getY();
                var d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -1F, 1F);
                var blockParticle = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
                var pos = new Vec3(d0, d1, d2);
                ParticleHandlers.sendParticles(level, blockParticle, pos, 2, 0, 0.4,0,1.5);
            }
        }
    }

    private void setKnockback(LivingEntity lEntity){
        lEntity.level().getNearbyEntities(
            LivingEntity.class, TargetingConditions.DEFAULT, lEntity,
            lEntity.getBoundingBox().inflate(1)
        ).forEach(
            livingEntity -> {
                var deltaX = livingEntity.getX() - lEntity.getX();
                var deltaY = livingEntity.getY() - lEntity.getY();
                var deltaZ = livingEntity.getZ() - lEntity.getZ();
                var length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if(this.canDamageEntity(livingEntity, this) && livingEntity != this.getTarget()){
                    this.knockback(livingEntity,1.4, -deltaX / length, -deltaZ / length);
                }
            }
        );
    }

    public boolean canDamageEntity(LivingEntity hitEntity, LivingEntity owner){
        if(owner != null) {
            var uuidMatched = hitEntity.getUUID() != owner.getUUID();
            var isTamable = !(hitEntity instanceof TamableEntity tamableEntity && tamableEntity.getOwner() == owner);
            return uuidMatched && isTamable;
        }
        return true;
    }

    private void knockback(LivingEntity targetEntity, double pStrength, double pX, double pZ) {
        var event = onLivingKnockBack(targetEntity, (float) pStrength, pX, pZ);
        if(event.isCanceled()) return;
        pStrength = event.getStrength();
        pX = event.getRatioX();
        pZ = event.getRatioZ();
        pStrength *= 1.0D - targetEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (!(pStrength <= 0.0D)) {
            targetEntity.hasImpulse = true;
            var vec3 = targetEntity.getDeltaMovement();
            var vec31 = (new Vec3(pX, 0.0D, pZ)).normalize().scale(pStrength);
            var y = targetEntity.onGround() ? Math.min(0.8D, vec3.y / 2.0D + pStrength) : vec3.y;
            var z = vec3.z / 2.0D - vec31.z;
            var x = vec3.x / 2.0D - vec31.x;
            targetEntity.setDeltaMovement(x, y, z);
        }
    }

    @Override
    public void tick() {
        privateTicks++;
        super.tick();
        reassignPlayer();
        this.resetFallDistance();
        this.endOfLife();
    }

    public void runningParticle(){
        if(!this.level().isClientSide){
            var isRunning = this.getSpeed() > 0.25 && isEntityMoving();
            if (isRunning) {
                this.setKnockback(this);
                clientDiggingParticles(this, level());
            }
        }
    }

    public void particle(){
        if(!this.level().isClientSide){
            var isRunning = this.getSpeed() > 0.25 && isEntityMoving();
            if (this.tickCount % (isRunning ? 1 : 4) == 0) {
                var level = this.level();
                var position = new Vec3(this.getRandomX(1), this.getRandomY(), this.getRandomZ(1));
                var directions = this.position().subtract(position).normalize();
                ParticleHandlers.sendParticles(
                    level, getAllParticleTypes(element(), 12, 1f),
                    position.add(0, 1, 0), isRunning ? 5 : 0,
                    directions.x, directions.y, directions.z, 0.12
                );
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if(id == 4) this.normal.start(this.tickCount);
        super.handleEntityEvent(id);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SCALE, 0f);
        pBuilder.define(LIFETIMES, this.lifeTime);
        pBuilder.define(PRIVATE_TICKS, this.privateTicks);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        var itemstack = player.getItemInHand(hand);
//        this.smash.start(this.tickCount);
        if (!itemstack.is(ItemsRegister.AUGMENT_HYPER_CORE)) {
            return InteractionResult.PASS;
        } else {
            var health = this.getHealth();
            this.heal(25.0F);
            if (this.getHealth() == health) {
                return InteractionResult.PASS;
            } else {
                var f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(SoundEvents.VAULT_PLACE, 2, 1.2f);
                this.playSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, f1, 0.7f);
                itemstack.consume(1, player);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
    }

    public boolean isEntityMoving() {
        var motion = this.getDeltaMovement();
        var speed = motion.length();
        return speed > 0.0784000015258789;
    }

    private void endOfLife(){
        if(!level().isClientSide){
            if (this.lifeTime != -1) if (this.privateTicks >= lifeTime) this.discard();
        }
    }

    private void reassignPlayer() {
        if(!(this.level() instanceof ServerLevel serverLevel)) return;
        if(this.owner == null && this.ownerUUID != null) this.owner = serverLevel.getPlayerByUUID(this.ownerUUID);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GenericMeleeAttackGoal(this, 1.8F, true));
        this.goalSelector.addGoal(2, new FollowGoal(this, 1.0D, 5.0F, 8.0F, false));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new GenericHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(2, new AttackNearbyMonsters<>(this, LivingEntity.class, true, 10, 30));
    }

    @Override
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putDouble(EFFECT_DURATION, effectDuration);
        compound.putDouble(EFFECT_STRENGTH, effectStrength);
        compound.putDouble(EFFECT_CHANCE, effectChance);
        compound.putDouble(DAMAGE, damage);
        compound.putInt(LIFETIME, lifeTime);
        compound.putInt("ticks", privateTicks);
        if(this.owner != null) compound.putUUID("saveOwner", owner.getUUID());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.effectDuration = compound.getDouble(EFFECT_DURATION);
        this.effectStrength = compound.getDouble(EFFECT_STRENGTH);
        this.effectChance = compound.getDouble(EFFECT_CHANCE);
        this.damage = compound.getDouble(DAMAGE);
        this.lifeTime = compound.getInt(LIFETIME);
        this.privateTicks = compound.getInt("ticks");
        if(compound.hasUUID("saveOwner")) this.ownerUUID = compound.getUUID("saveOwner");
    }

    private AbstractElement element(){
        return ElementRegistry.VITALITY.get();
    }
}
