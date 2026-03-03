package org.jahdoo.common.entities.eternal_wizard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.entities.goals.*;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.items.caster_item.CasterItemHelper;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.abilities_combat.EtherealArrow;
import org.jahdoo.trial_nexus.ability.abilities_combat.fireball.FireballAbility;
import org.jahdoo.trial_nexus.ability.abilities_combat.frostbolts.FrostboltsAbility;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static org.jahdoo.common.entities.EntityHelpers.canTarget;
import static org.jahdoo.common.items.caster_item.CastHelper.castAnimation;
import static org.jahdoo.common.items.caster_item.ItemAnimations.SINGLE_CAST_ID;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.ability.abilities_combat.armageddon.ArmageddonModule.IS_BUDDY;
import static org.jahdoo.trial_nexus.ability.abilities_combat.fireball.FireballAbility.abilityId;

public class EternalWizard extends AbstractSkeleton implements ITamableEntity {

    private static final EntityDataAccessor<Boolean> SET_MODE = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFETIMES = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PRIVATE_TICKS = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.OPTIONAL_UUID);
    private final RangedCustomAttackGoal<AbstractSkeleton> wandGoal = new RangedCustomAttackGoal<>(this, 1.0D, 0, 60.0F);

    private LivingEntity owner;
    private UUID ownerUUID;
    private double damage;
    private double effectDuration;
    private double effectStrength;
    private double effectChance;
    private double leechChance;
    private int lifeTime;
    private int privateTicks;

    public EternalWizard(EntityType<? extends AbstractSkeleton> entityType, Level level) {
        super(entityType, level);
        this.owner = null;
        this.lifeTime = -1;
        this.setLifetimes(-1);
        this.damage = 5;
        this.reassessWeaponGoal();
    }

    public EternalWizard(Level level, Player player, int lifeTime, double damage) {
        super(EntityReg.ETERNAL_WIZARD.get(), level);
        this.owner = player;
        this.lifeTime = lifeTime;
        this.damage = damage;
        this.reassessWeaponGoal();
        this.setLifetimes(lifeTime);
    }

    public EternalWizard(Level level, Player player, double damage, double effectDuration, double effectStrength, int lifeTime, double effectChance, double leechChance) {
        super(EntityReg.ETERNAL_WIZARD.get(), level);
        this.owner = player;
        this.reassessWeaponGoal();
        this.damage = damage;
        this.effectDuration = effectDuration;
        this.effectStrength = effectStrength;
        this.lifeTime = lifeTime;
        this.effectChance = effectChance;
        this.leechChance = leechChance;
        this.setLifetimes(lifeTime);
    }

    public float getInternalScale() {
        return this.entityData.get(SCALE);
    }

    public void setScale(float getSelectedAbility) {
        this.entityData.set(SCALE, getSelectedAbility);
    }

    public boolean getMode() {
        return this.entityData.get(SET_MODE);
    }

    public void setMode(boolean getSelectedAbility) {
        this.entityData.set(SET_MODE, getSelectedAbility);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIMES);
    }

    public void setLifetimes(int lifetimes) {
        this.entityData.set(LIFETIMES, lifetimes);
    }

    public int getPrivateTicks() {
        return this.entityData.get(PRIVATE_TICKS);
    }

    public void setPrivateTicks(int privateTicks) {
        this.entityData.set(PRIVATE_TICKS, privateTicks);
    }

    public LivingEntity getOwner(){
        return this.owner;
    }

    @Override
    public void setOwner(LivingEntity livingEntity) {
        this.owner = livingEntity;
    }

    @Override
    protected @NotNull SoundEvent getStepSound() {
        return SoundEvents.WITHER_SKELETON_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
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
    public Optional<UUID> getOwnerUUIDOptional() {
        return this.entityData.get(OWNER_UUID);

    }

    @Override
    public void setOwnerUUIDOptional(UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(this.owner != null) CasterItemHelper.setWizardMode(this, player);
        return InteractionResult.CONSUME;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
//        fireballAbility(target);
        shooterAbility(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if(canTarget(target, owner)){
            super.setTarget(target);
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
    }

    @Override
    public void tick() {
        super.tick();
        if(!(this.level() instanceof ServerLevel serverLevel)) return;

        privateTicks++;
        this.setPrivateTicks(this.privateTicks);
        if(this.owner == null) {
            this.owner = this.reassignOwner(level(), owner, ownerUUID);
        }
        if(this.lifeTime != -1) if (this.privateTicks >= lifeTime) this.discard();
        var nearestTarget = serverLevel.getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT, null, getX(), getY(), getZ(), this.getBoundingBox().inflate(20, 20, 20));
        if(this.getMode()){
            if (this.getTarget() == null && nearestTarget != null) this.setTarget(nearestTarget);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SET_MODE, true);
        builder.define(SCALE, 0f);
        builder.define(LIFETIMES, this.lifeTime);
        builder.define(PRIVATE_TICKS, this.privateTicks);
        builder.define(OWNER_UUID, Optional.empty());
    }

    private void fireballAbility(LivingEntity target) {
        ElementProjectile elementProjectile = new ElementProjectile(
            EntityReg.INFERNO_ELEMENT_PROJECTILE.get(), this,
            EntityDataReg.FIRE_BALL.get().setAbilityId(), -0.3,
            fireballModule(),
            abilityId.getPath().intern()
        );
        fireProjectile(target, elementProjectile, 1.2, 0.5F);
    }

    public AbilityHolder fireballModule() {
        //NOTE: Does not work when spawned without player as does not have any attributes, so may need static values
        return new AbilityBuilder(FireballAbility.abilityId.getPath().intern())
            .setDamageWithValue(0,0, this.damage)
            .setEffectDurationWithValue(0,0,this.effectDuration)
            .setEffectChanceWithValue(0,0, this.effectChance)
            .setEffectStrengthWithValue(0,0,this.effectStrength)
            .setModifier(FireballAbility.NOVA_RANGE, 0,0,true, JahdooHelpers.Random.nextInt(4,6))
            .setModifierWithoutBounds(IS_BUDDY, 0)
            .buildAndReturn();
    }

    private void shooterAbility(LivingEntity target) {
        GenericProjectile arrow = new GenericProjectile(
            this, this.getX(), this.getY() + 2, this.getZ(),
            EntityDataReg.ETHEREAL_ARROW.get().setAbilityId(),
            EtherealArrow.setArrowProperties(this.damage, this.effectDuration, this.effectStrength, this.effectChance, ElementReg.vitality().id(), leechChance),
            ElementReg.vitality(),
            FrostboltsAbility.abilityId.getPath().intern()
        );
        fireProjectile(target, arrow, 0, 1.6F);
    }

    @Override
    public void reassessWeaponGoal() {
        if (this.level() instanceof ServerLevel) {
            this.goalSelector.removeGoal(this.wandGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, CastHelper::validCasterType));
            if (itemstack.is(ItemReg.WAND_ITEM_VITALITY.get())) {
                //Set attack interval
                int i = 10;
                this.wandGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(1, this.wandGoal);
            }
        }
    }

    private void fireProjectile(LivingEntity target, Projectile projectile, double offset, float velocity) {
        if (CastHelper.validCasterType(this.getMainHandItem().getItem())) {
            projectile.setOwner(this);
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333D) - projectile.getY() - offset;
            double d2 = target.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            projectile.shoot(d0, d1 + d3 * (double)0.1F, d2, velocity, 0);

            this.playSound(SoundReg.ELEMENTAL_BULLET.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            castAnimation(this, SINGLE_CAST_ID);
            this.level().addFreshEntity(projectile);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new FollowGoal(this, 1.0D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

//        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(1, new AttackNearbyMonsters<>(this, LivingEntity.class, false, false, null));
        this.targetSelector.addGoal(2, new GenericHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new GenericOwnerHurtByTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(3, new GenericOwnerHurtTargetGoal(this, this::getOwner));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(this.owner != null) tag.putUUID("owner", owner.getUUID());
        tag.putBoolean("mode",this.getMode());
        tag.putDouble(DAMAGE, this.damage);
        tag.putDouble(EFFECT_DURATION, this.effectDuration);
        tag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        tag.putDouble(EFFECT_CHANCE, this.effectChance);
        tag.putInt(LIFETIME, this.lifeTime);
        tag.putDouble(LIFE_LEECH, this.leechChance);
        tag.putInt("private_ticks", this.privateTicks);
        saveTag(owner, tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setMode(tag.getBoolean("mode"));
        this.damage = tag.getDouble(DAMAGE);
        this.effectDuration = tag.getDouble(EFFECT_DURATION);
        this.effectStrength = tag.getDouble(EFFECT_STRENGTH);
        this.effectChance = tag.getDouble(EFFECT_CHANCE);
        this.leechChance = tag.getDouble(LIFE_LEECH);
        this.lifeTime = tag.getInt(LIFETIME);
        this.privateTicks = tag.getInt("private_ticks");
        this.setLifetimes(this.lifeTime);
        this.setScale(1);
        this.setMode(tag.getBoolean("mode"));
        var uuid1 = loadTag(tag);
        if(uuid1 != null) this.ownerUUID = uuid1;
    }
}
