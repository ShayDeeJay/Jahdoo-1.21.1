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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.all_magic.all_abilities.abilities.FrostboltsAbility;
import org.jahdoo.all_magic.all_abilities.ability_components.EtherealArrow;
import org.jahdoo.entities.goals.*;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.items.wand.WandItemHelper;
import org.jahdoo.registers.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static org.jahdoo.all_magic.AbilityBuilder.*;

public class EternalWizard extends AbstractSkeleton {
    private static final EntityDataAccessor<Boolean> SET_MODE = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(EternalWizard.class, EntityDataSerializers.FLOAT);
    private final RangedCustomAttackGoal<AbstractSkeleton> wandGoal = new RangedCustomAttackGoal<>(this, 1.0D, 0, 30.0F);
    Player player;
    UUID playerUUID;
    double damage;
    int effectDuration;
    int effectStrength;
    int effectChance;
    int lifeTime;

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

    public EternalWizard(EntityType<? extends AbstractSkeleton> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.reassessWeaponGoal();
    }

    public EternalWizard(Level pLevel, Player player) {
        super(EntitiesRegister.ETERNAL_WIZARD.get(), pLevel);
        this.player = player;
        this.reassessWeaponGoal();
    }

    public EternalWizard(Level pLevel, Player player, double damage, int effectDuration, int effectStrength, int lifeTime, int effectChance) {
        super(EntitiesRegister.ETERNAL_WIZARD.get(), pLevel);
        this.player = player;
        this.reassessWeaponGoal();
        this.damage = damage;
        this.effectDuration = effectDuration;
        this.effectStrength = effectStrength;
        this.lifeTime = lifeTime;
        this.effectChance = effectChance;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new FollowGoal(this, 1.0D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(3, (new GenericHurtByTargetGoal(this, (entity) -> entity == getOwner())).setAlertOthers());
        this.targetSelector.addGoal(2, new AttackNearbyMonsters<>(this, Mob.class, false));
    }

    @Override
    public double getAttributeBaseValue(Holder<Attribute> pAttribute) {
        return super.getAttributeBaseValue(pAttribute);
    }

    public LivingEntity getOwner(){
        return this.player;
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
        if(!(this.level() instanceof ServerLevel serverLevel)) return;
        if(player == null && this.playerUUID != null) this.player = serverLevel.getPlayerByUUID(this.playerUUID);
        if(this.tickCount >= lifeTime) this.discard();
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
        GenericProjectile arrow = new GenericProjectile(
            this, this.getX(), this.getY() + 2, this.getZ(),
            ProjectilePropertyRegister.ETHEREAL_ARROW.get().setAbilityId(),
            EtherealArrow.setArrowProperties(this.damage, this.effectDuration, this.effectStrength, effectChance),
            ElementRegistry.VITALITY.get(),
            FrostboltsAbility.abilityId.getPath().intern()
        );
        if (this.getMainHandItem().getItem() instanceof WandItem) {
            arrow.setOwner(this);
            double d0 = pTarget.getX() - this.getX();
            double d1 = pTarget.getY(0.3333333333333333D) - arrow.getY() - 0.6;
            double d2 = pTarget.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            arrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, 0);
            this.playSound(SoundRegister.ORB_CREATE.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level().addFreshEntity(arrow);
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        WandItemHelper.setWizardMode(this, pPlayer);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SET_MODE, true);
        pBuilder.define(SCALE, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("mode",this.getMode());
        pCompound.putDouble(DAMAGE, this.damage);
        pCompound.putInt(EFFECT_DURATION, this.effectDuration);
        pCompound.putInt(EFFECT_STRENGTH, this.effectStrength);
        pCompound.putInt(EFFECT_CHANCE, this.effectChance);
        pCompound.putInt(LIFETIME, this.lifeTime);
        pCompound.putInt("customTicks", this.tickCount);
        if(this.player != null) pCompound.putUUID("savePlayer", player.getUUID());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.playerUUID = pCompound.getUUID("savePlayer");
        this.damage = pCompound.getDouble("abilityAttributes");
        this.effectDuration = pCompound.getInt("effectDuration");
        this.effectStrength = pCompound.getInt("effectStrength");
        this.lifeTime = pCompound.getInt("lifeTime");
        this.tickCount = pCompound.getInt("customTicks");
        this.effectChance = pCompound.getInt("effectChance");
        this.setScale(1);
        this.setMode(pCompound.getBoolean("mode"));
    }
}
