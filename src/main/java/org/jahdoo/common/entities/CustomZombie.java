package org.jahdoo.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jahdoo.common.entities.goals.*;
import org.jahdoo.common.registers.EntityReg;

import java.util.UUID;

public class CustomZombie extends Zombie implements ITamableEntity {
    LivingEntity owner;
    UUID ownerUUID;

    public CustomZombie(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    public CustomZombie(Level level, LivingEntity owner) {
        super(EntityReg.CUSTOM_ZOMBIE.get(), level);
        this.owner = owner;
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
    protected void registerGoals() {
        addBehaviourGoals();
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

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if(this.owner != null) compound.putUUID("saveOwner", owner.getUUID());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.hasUUID("saveOwner")) this.ownerUUID = compound.getUUID("saveOwner");
    }

    public PathNavigation getNavigation() {
        var controlledVehicle = this.getControlledVehicle();
        PathNavigation pathNavigation;
        pathNavigation = (controlledVehicle instanceof Mob mob) ? mob.getNavigation() : this.navigation;
        return pathNavigation;
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
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new ZombieAttackGoal(this, 1.0F, false));
        this.goalSelector.addGoal(2, new AttackNearbyMonsters<>(this, Player.class, false, true, null));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new FollowGoal(this, 1.0D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

//        this.targetSelector.addGoal(1, new GenericHurtByTargetGoal(this));
//        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new GenericOwnerHurtByTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(3, new GenericOwnerHurtTargetGoal(this, this::getOwner));
    }
}
