package org.jahdoo.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.all_abilities.abilities.EscapeDecoyAbility;
import org.jahdoo.entities.goals.*;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

import static net.neoforged.neoforge.common.CommonHooks.onLivingKnockBack;

public class AncientGolem extends IronGolem implements Tamable{

    LivingEntity owner;
    UUID ownerUUID;
    public AncientGolem(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
    }

    public AncientGolem(Level level, @Nullable LivingEntity owner) {
        super(EntitiesRegister.ANCIENT_GOLEM.get(), level);
        this.owner = owner;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 100.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.25F)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
            .add(Attributes.ATTACK_DAMAGE, 15.0F)
            .add(Attributes.STEP_HEIGHT, 1.0F);
    }



    @Override
    protected SoundEvent getDeathSound() {
        var isRunning = this.getSpeed() > 0.25;
        var volume = isRunning ? 2 : 1;
        var pitch = isRunning ? 0.4f : 1.2f;
        var pitch2 = isRunning ? 0.2f : 0.6f;
        ModHelpers.getSoundWithPositionV(level(), this.position(), SoundEvents.VAULT_PLACE, volume, pitch);
        ModHelpers.getSoundWithPositionV(level(), this.position(), SoundEvents.IRON_GOLEM_STEP, volume, pitch2);
        return SoundEvents.EMPTY;
    }



    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        ModHelpers.getSoundWithPositionV(level(), this.position(), SoundEvents.ELDER_GUARDIAN_HURT, 1, 2f);
        ModHelpers.getSoundWithPositionV(level(), this.position(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 1, 2f);
        return SoundEvents.EMPTY;
    }

    @Override
    protected void playAttackSound() {
        ModHelpers.getSoundWithPositionV(level(), this.position(), SoundEvents.VAULT_PLACE, 1, 1.2f);
        ModHelpers.getSoundWithPositionV(level(), this.position(), SoundEvents.IRON_GOLEM_STEP, 1, 0.6f);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        var isRunning = this.getSpeed() > 0.25;
        var volume = isRunning ? 2 : 1;
        var pitch = isRunning ? 0.4f : 1.2f;
        var pitch2 = isRunning ? 0.2f : 0.6f;
        ModHelpers.getSoundWithPositionV(level(), this.position(), SoundEvents.VAULT_PLACE, volume, pitch);
        ModHelpers.getSoundWithPositionV(level(), this.position(), SoundEvents.IRON_GOLEM_STEP, volume, pitch2);
    }

    public void clientDiggingParticles(LivingEntity livingEntity, Level level) {
        RandomSource randomsource = livingEntity.getRandom();
        BlockState blockstate = livingEntity.getBlockStateOn();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 25; ++i) {
                double d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -1F, 1F);
                double d1 = livingEntity.getY();
                double d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -1F, 1F);
                ParticleHandlers.sendParticles(level, new BlockParticleOption(ParticleTypes.BLOCK, blockstate), new Vec3(d0, d1, d2), 2, 0, 0.4,0,1.5);
            }
        }
    }

    private void setKnockback(LivingEntity lEntity){
        lEntity.level().getNearbyEntities(
            LivingEntity.class, TargetingConditions.DEFAULT, lEntity,
            lEntity.getBoundingBox().inflate(1)
        ).forEach(
            livingEntity -> {
                double deltaX = livingEntity.getX() - lEntity.getX();
                double deltaY = livingEntity.getY() - lEntity.getY();
                double deltaZ = livingEntity.getZ() - lEntity.getZ();
                double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if(this.canDamageEntity(livingEntity, this) && livingEntity != this.getTarget()){
                    this.knockback(livingEntity,1.4, -deltaX / length, -deltaZ / length);
                }
            }
        );
    }

    public boolean canDamageEntity(LivingEntity hitEntity, LivingEntity owner){
        if(owner != null) {
            return hitEntity.getUUID() != owner.getUUID() && !(hitEntity instanceof Tamable tamable && tamable.getOwner() == owner);
        }
        return true;
    }

    private void knockback(LivingEntity targetEntity, double pStrength, double pX, double pZ) {
        LivingKnockBackEvent event = onLivingKnockBack(targetEntity, (float) pStrength, pX, pZ);
        if(event.isCanceled()) return;
        pStrength = event.getStrength();
        pX = event.getRatioX();
        pZ = event.getRatioZ();
        pStrength *= 1.0D - targetEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (!(pStrength <= 0.0D)) {
            targetEntity.hasImpulse = true;
            Vec3 vec3 = targetEntity.getDeltaMovement();
            Vec3 vec31 = (new Vec3(pX, 0.0D, pZ)).normalize().scale(pStrength);
            targetEntity.setDeltaMovement(vec3.x / 2.0D - vec31.x, targetEntity.onGround() ? Math.min(0.8D, vec3.y / 2.0D + pStrength) : vec3.y, vec3.z / 2.0D - vec31.z);
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        ModHelpers.getSoundWithPosition(level(), this.blockPosition(), SoundEvents.ELDER_GUARDIAN_AMBIENT, 0.2f, 1.8f);
        ModHelpers.getSoundWithPosition(level(), this.blockPosition(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 0.2f, 2f);
        return SoundEvents.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide) particle();
        reassignPlayer();
    }

    private void particle(){
        if(!this.level().isClientSide){
            var isRunning = this.getSpeed() > 0.25 && isEntityMoving();
            if (isRunning) {
                this.setKnockback(this);
                clientDiggingParticles(this, level());
            }

            if (this.tickCount % (isRunning ? 1 : 4) == 0) {
                var level = this.level();
                var position = new Vec3(this.getRandomX(0.3), this.getRandomY() - 2, this.getRandomZ(0.3));
                var directions = this.position().subtract(position).normalize();
                float y = 1f;
                ParticleHandlers.sendParticles(
                    level,
                    EscapeDecoyAbility.getFromAllRandom(element(), 12, 1f),
                    position.add(0, y, 0), isRunning ? 5 : 0,
                    directions.x, directions.y, directions.z, 0.12
                );
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        System.out.println("im ere");
        return super.mobInteract(player, hand);
    }

    public boolean isEntityMoving() {
        Vec3 motion = this.getDeltaMovement();
        double speed = motion.length();
        return speed > 0.0784000015258789;
    }

    private void reassignPlayer() {
        if(!(this.level() instanceof ServerLevel serverLevel)) return;
        if(this.owner == null && this.ownerUUID != null) this.owner = serverLevel.getPlayerByUUID(this.ownerUUID);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.8F, true));
        this.goalSelector.addGoal(5, new FollowGoal(this, 1.0D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(2, new AttackNearbyMonsters<>(this, LivingEntity.class, true, 10, 30));
        this.targetSelector.addGoal(3, (new GenericHurtByTargetGoal(this, (entity) -> entity == getOwner())).setAlertOthers());
    }

    @Override
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if(this.owner != null) compound.putUUID("saveOwner", owner.getUUID());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.hasUUID("saveOwner")){
            this.ownerUUID = compound.getUUID("saveOwner");
        }
    }

    private AbstractElement element(){
        return ElementRegistry.VITALITY.get();
    }
}
