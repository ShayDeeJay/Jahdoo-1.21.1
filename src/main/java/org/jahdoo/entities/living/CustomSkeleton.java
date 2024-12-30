package org.jahdoo.entities.living;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.CommonHooks;
import org.jahdoo.entities.TamableEntity;
import org.jahdoo.entities.goals.AttackNearbyMonsters;
import org.jahdoo.entities.goals.FollowGoal;
import org.jahdoo.entities.goals.GenericOwnerHurtByTargetGoal;
import org.jahdoo.entities.goals.GenericOwnerHurtTargetGoal;
import org.jahdoo.registers.EntitiesRegister;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

import static net.minecraft.world.entity.projectile.ProjectileUtil.getWeaponHoldingHand;
import static org.jahdoo.utils.ModHelpers.Random;

public class CustomSkeleton extends Skeleton implements TamableEntity {
    LivingEntity owner;
    UUID ownerUUID;
    ItemStack arrowType;
    private final RangedBowAttackGoal<AbstractSkeleton> bowGoal = new RangedBowAttackGoal<>(this, 1.0F, 20, 25.0F);
    public CustomSkeleton(EntityType<? extends Skeleton> entityType, Level level) {
        super(entityType, level);
    }

    public CustomSkeleton(Level level, LivingEntity owner, @Nullable ItemStack arrowType) {
        super(EntitiesRegister.CUSTOM_SKELETON.get(), level);
        this.owner = owner;
        this.arrowType = arrowType;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    public LivingEntity getOwner(){
        return this.owner;
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return super.canAttackType(type);
    }

    @Override
    public void tick() {
        super.tick();
        reassignPlayer();
    }

    private void reassignPlayer() {
        if(!(this.level() instanceof ServerLevel serverLevel)) return;
        if(this.owner == null && this.ownerUUID != null) this.owner = serverLevel.getPlayerByUUID(this.ownerUUID);
    }

    public PathNavigation getNavigation() {
        var controlledVehicle = this.getControlledVehicle();
        PathNavigation pathNavigation;
        pathNavigation = (controlledVehicle instanceof Mob mob) ? mob.getNavigation() : this.navigation;
        return pathNavigation;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0F));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Wolf.class, 6.0F, 1.0F, 1.2));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        var weapon = this.getItemInHand(getWeaponHoldingHand(this, (item) -> item instanceof BowItem));
        var itemStack = this.getProjectile(weapon);
        var getArrow = this.getArrow(itemStack, distanceFactor, weapon);
        var getWeapons = weapon.getItem();
        if (getWeapons instanceof ProjectileWeaponItem weaponItem) {
            getArrow = weaponItem.customArrow(getArrow, itemStack, weapon);
        }

        var d0 = target.getX() - this.getX();
        var d1 = target.getY(0.3333333333333333) - getArrow.getY();
        var d2 = target.getZ() - this.getZ();
        var d3 = Math.sqrt(d0 * d0 + d2 * d2);
        getArrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(getArrow);
    }

    @Override
    public void reassessWeaponGoal() {
        this.level();
        if (!this.level().isClientSide) {
            this.goalSelector.removeGoal(this.bowGoal);
            var itemstack = this.getItemInHand(getWeaponHoldingHand(this, (item) -> item instanceof BowItem));
            if (itemstack.is(Items.BOW)) {
                var i = this.getHardAttackInterval();
                if (this.level().getDifficulty() != Difficulty.HARD) i = this.getAttackInterval();
                this.bowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.bowGoal);
            }
        }
    }

    @Override
    protected AbstractArrow getArrow(ItemStack arrow, float velocity, @Nullable ItemStack weapon) {
        return ProjectileUtil.getMobArrow(this, arrow, velocity, weapon);
    }

    @Override
    public ItemStack getProjectile(ItemStack shootable) {
        if (shootable.getItem() instanceof ProjectileWeaponItem) {
            var tippedArrow = new ItemStack(Items.ARROW);
            var getArrows = this.arrowType == null ? tippedArrow : this.arrowType;
            return CommonHooks.getProjectile(this, shootable, getArrows);
        }
        return CommonHooks.getProjectile(this, shootable, ItemStack.EMPTY);
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createLivingAttributes()
            .add(Attributes.FOLLOW_RANGE, 35.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.23F)
            .add(Attributes.ATTACK_DAMAGE, 3.0F)
            .add(Attributes.ARMOR, 2.0F)
            .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if(this.owner != null) compound.putUUID("saveOwner", owner.getUUID());
        compound.put("item", this.arrowType.save(this.registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.hasUUID("saveOwner")) this.ownerUUID = compound.getUUID("saveOwner");
        if (compound.contains("item")) {
            ItemStack.parse(this.registryAccess(), compound.getCompound("item"))
                .ifPresent(itemStack ->  this.arrowType = itemStack);
        }
    }
}
