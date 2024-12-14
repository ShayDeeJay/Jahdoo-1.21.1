package org.jahdoo.entities;

import net.minecraft.core.Holder;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.ability.all_abilities.abilities.FireballAbility;
import org.jahdoo.ability.all_abilities.abilities.FrostboltsAbility;
import org.jahdoo.ability.all_abilities.ability_components.EtherealArrow;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.entities.goals.*;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.items.wand.WandItemHelper;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.ability.all_abilities.abilities.FireballAbility.abilityId;
import static org.jahdoo.ability.all_abilities.ability_components.ArmageddonModule.buddy;
import static org.jahdoo.items.wand.CastHelper.castAnimation;
import static org.jahdoo.items.wand.WandAnimations.SINGLE_CAST_ID;

public class EternalWizard extends AbstractSkeleton implements TamableEntity {
    private static final EntityDataAccessor<Boolean> SET_MODE = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFETIMES = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PRIVATE_TICKS = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.INT);
    private final RangedCustomAttackGoal<AbstractSkeleton> wandGoal = new RangedCustomAttackGoal<>(this, 1.0D, 0, 60.0F);

    LivingEntity owner;
    UUID ownerUUID;
    double damage;
    int effectDuration;
    int effectStrength;
    int effectChance;
    int lifeTime;
    public int privateTicks;

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

    public EternalWizard(EntityType<? extends AbstractSkeleton> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.reassessWeaponGoal();
    }

    public EternalWizard(Level pLevel, Player player, int lifeTime, double damage) {
        super(EntitiesRegister.ETERNAL_WIZARD.get(), pLevel);
        this.owner = player;
        this.lifeTime = lifeTime;
        this.damage = damage;
        this.reassessWeaponGoal();
    }

    public EternalWizard(Level pLevel, Player player, double damage, int effectDuration, int effectStrength, int lifeTime, int effectChance) {
        super(EntitiesRegister.ETERNAL_WIZARD.get(), pLevel);
        this.owner = player;
        this.reassessWeaponGoal();
        this.damage = damage;
        this.effectDuration = effectDuration;
        this.effectStrength = effectStrength;
        this.lifeTime = lifeTime;
        this.effectChance = effectChance;
        this.setLifetimes(lifeTime);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new FollowGoal(this, 1.0D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new GenericHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(2, new AttackNearbyMonsters<>(this, LivingEntity.class, true, 10, 30));
    }

    @Override
    public double getAttributeBaseValue(Holder<Attribute> pAttribute) {
        return super.getAttributeBaseValue(pAttribute);
    }

    public LivingEntity getOwner(){
        return this.owner;
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
    public void tick() {
        super.tick();
        privateTicks++;
        if(!(this.level() instanceof ServerLevel serverLevel)) return;
//        if(this.getTarget() == this.owner) this.setTarget(null);
        this.setPrivateTicks(this.privateTicks);
        if(owner == null && this.ownerUUID != null) this.owner = serverLevel.getPlayerByUUID(this.ownerUUID);
        if(this.lifeTime != -1){
            if (this.privateTicks >= lifeTime) this.discard();
        }
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void reassessWeaponGoal() {
        if (this.level() instanceof ServerLevel) {
            this.goalSelector.removeGoal(this.wandGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof WandItem));
            if (itemstack.is(ItemsRegister.WAND_ITEM_VITALITY.get())) {
                //Set attack interval
                int i = 10;
                this.wandGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(2, this.wandGoal);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
//        fireballAbility(pTarget);
//        this.fireProjectile(elementProjectile, player, 0.5f);
        shooterAbility(pTarget);
    }

    private void fireballAbility(LivingEntity target) {
        ElementProjectile elementProjectile = new ElementProjectile(
            EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(), this,
            EntityPropertyRegister.FIRE_BALL.get().setAbilityId(), -0.3,
            fireballModule(),
            abilityId.getPath().intern()
        );
        fireProjectile(target, elementProjectile, 1.2, 0.5F);
    }

    public WandAbilityHolder fireballModule() {
        //NOTE: Does not work when spawned without player as does not have any attributes, so may need static values
        return new AbilityBuilder(null, FireballAbility.abilityId.getPath().intern())
            .setDamageWithValue(0,0, this.damage)
            .setEffectDurationWithValue(0,0,this.effectDuration)
            .setEffectChanceWithValue(0,0, this.effectChance)
            .setEffectStrengthWithValue(0,0,this.effectStrength)
            .setModifier(FireballAbility.novaRange, 0,0,true, ModHelpers.Random.nextInt(4,6))
            .setModifierWithoutBounds(buddy, 0)
            .buildAndReturn();
    }

    private void shooterAbility(LivingEntity pTarget) {
        GenericProjectile arrow = new GenericProjectile(
            this, this.getX(), this.getY() + 2, this.getZ(),
            EntityPropertyRegister.ETHEREAL_ARROW.get().setAbilityId(),
            EtherealArrow.setArrowProperties(this.damage, this.effectDuration, this.effectStrength, this.effectChance),
            ElementRegistry.VITALITY.get(),
            FrostboltsAbility.abilityId.getPath().intern()
        );
        fireProjectile(pTarget, arrow, 0.9, 1.6F);
    }

    private void fireProjectile(LivingEntity pTarget, Projectile projectile, double offset, float velocity) {
        if (this.getMainHandItem().getItem() instanceof WandItem) {
            projectile.setOwner(this);
            double d0 = pTarget.getX() - this.getX();
            double d1 = pTarget.getY(0.3333333333333333D) - projectile.getY() - offset;
            double d2 = pTarget.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            projectile.shoot(d0, d1 + d3 * (double)0.2F, d2, velocity, 0);
            this.playSound(SoundRegister.ORB_CREATE.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            castAnimation(this, SINGLE_CAST_ID);
            this.level().addFreshEntity(projectile);
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if(this.owner != null){
            WandItemHelper.setWizardMode(this, pPlayer);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SET_MODE, true);
        pBuilder.define(SCALE, 0f);
        pBuilder.define(LIFETIMES, this.lifeTime);
        pBuilder.define(PRIVATE_TICKS, this.privateTicks);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if(this.owner != null) pCompound.putUUID("owner", owner.getUUID());
        pCompound.putBoolean("mode",this.getMode());
        pCompound.putDouble(DAMAGE, this.damage);
        pCompound.putInt(EFFECT_DURATION, this.effectDuration);
        pCompound.putInt(EFFECT_STRENGTH, this.effectStrength);
        pCompound.putInt(EFFECT_CHANCE, this.effectChance);
        pCompound.putInt(LIFETIME, this.lifeTime);
        pCompound.putInt("private_ticks", this.privateTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if(pCompound.hasUUID("owner")){
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.setMode(pCompound.getBoolean("mode"));
        this.damage = pCompound.getDouble(DAMAGE);
        this.effectDuration = pCompound.getInt(EFFECT_DURATION);
        this.effectStrength = pCompound.getInt(EFFECT_STRENGTH);
        this.effectChance = pCompound.getInt(EFFECT_CHANCE);
        this.lifeTime = pCompound.getInt(LIFETIME);
        this.privateTicks = pCompound.getInt("private_ticks");
        this.setLifetimes(this.lifeTime);
        this.setScale(1);
        this.setMode(pCompound.getBoolean("mode"));
    }
}
